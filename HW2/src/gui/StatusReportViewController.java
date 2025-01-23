package gui;

import client.ChatClient;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

public class StatusReportViewController {

    @FXML
    private Text statusReportTitle;

    @FXML
    private Text chartOverlayText;

    @FXML
    private BarChart<String, Number> statusReportChart;

    public void loadStatusReport(String month, String year) {
        if (month == null || year == null) {
            showAlert("Month and Year must not be null.");
            return;
        }

        try {
            // 1. Ask server for the relevant report data
            ClientGUIConnectionController.chat.reports_accept("create status report", month, year);
            // 2. Also retrieve the count of how many joined
            ClientGUIConnectionController.chat.reports_accept("how many joined", month, year);
            

            if (ChatClient.FullStatusRep == null || ChatClient.FullStatusRep.isEmpty()) {
                showAlert("No information to display.");
                closeWindow();
                return;
            }

            // Convert month & year into integers and create date boundaries
            int reportMonth = Integer.parseInt(month);
            int reportYear = Integer.parseInt(year);

            // Start (1st day of the given month) and end (last day of the same month)
            YearMonth ym = YearMonth.of(reportYear, reportMonth);
            LocalDate reportStart = ym.atDay(1);                        // e.g. 2025-01-01
            LocalDate reportEnd = ym.atEndOfMonth();                    // e.g. 2025-01-31

            // Prepare maps to hold the daily counts
            Map<Integer, Integer> activeCountByDay = new HashMap<>();
            Map<Integer, Integer> frozenCountByDay = new HashMap<>();

            // Initialize each day of the month to 0
            for (int d = 1; d <= reportEnd.getDayOfMonth(); d++) {
                activeCountByDay.put(d, 0);
                frozenCountByDay.put(d, 0);
            }

            // Number of new subscribers during the chosen month (provided by server)
            int newSubscribers = ChatClient.SubCnt;

            /*
             * For each subscriber record in FullStatusRep:
             *   Format example: "Join Date: 2024-01-11, Freeze Date: 2025-01-05"
             *   or "Join Date: 2024-08-16, Freeze Date: None"
             *
             *   If freeze is not null => freeze lasts from freezeDate to freezeDate.plusMonths(1).minusDays(1).
             *   We increment active or frozen for each day in the report month accordingly.
             */
            for (String record : ChatClient.FullStatusRep) {
                // Parse each record
                // Expecting something like: "Join Date: yyyy-MM-dd" and possibly "Freeze Date: yyyy-MM-dd"
                String[] parts = record.split(","); // split by comma
                // Clean up each part and separate label from value
                String joinStr = null;
                String freezeStr = null;

                for (String part : parts) {
                    part = part.trim(); // remove extra spaces
                    if (part.startsWith("Join Date:")) {
                        joinStr = part.replace("Join Date:", "").trim();
                    } else if (part.startsWith("Freeze Date:")) {
                        freezeStr = part.replace("Freeze Date:", "").trim();
                    }
                }

                if (joinStr == null || joinStr.isEmpty()) {
                    // Malformed record or missing join date
                    continue;
                }

                LocalDate joinDate = LocalDate.parse(joinStr);
                LocalDate freezeDate = null;
                LocalDate freezeEnd = null;

                if (freezeStr != null && !freezeStr.equalsIgnoreCase("None")) {
                    freezeDate = LocalDate.parse(freezeStr);
                    // Freeze continues for 1 month from freezeDate
                    freezeEnd = freezeDate.plusMonths(1).minusDays(1);
                }

                // Now we go day by day in the reporting month and figure out if subscriber is active or frozen
                for (int d = 1; d <= reportEnd.getDayOfMonth(); d++) {
                    LocalDate currentDay = LocalDate.of(reportYear, reportMonth, d);

                    // If the subscriber hasn't joined yet by this day, skip
                    if (currentDay.isBefore(joinDate)) {
                        continue;
                    }

                    // If subscriber has a freeze date and the current day is within that freeze window
                    if (freezeDate != null &&
                        !currentDay.isBefore(freezeDate) &&         // currentDay >= freezeDate
                        !currentDay.isAfter(freezeEnd)) {           // currentDay <= freezeEnd
                        // Then they are frozen this day
                        frozenCountByDay.put(d, frozenCountByDay.get(d) + 1);
                    } else {
                        // Otherwise, they are active
                        activeCountByDay.put(d, activeCountByDay.get(d) + 1);
                    }
                }
            }

            // Finally, update the bar chart
            updateChart(activeCountByDay, frozenCountByDay, reportEnd.getDayOfMonth());
            displaySummary(newSubscribers, reportMonth, reportYear);

        } catch (Exception e) {
            showAlert("Error processing status report: " + e.getMessage());
        }
    }

    /**
     * Push the daily active/frozen data into the BarChart.
     */
    private void updateChart(Map<Integer, Integer> activeMap, Map<Integer, Integer> frozenMap, int daysInMonth) {
        XYChart.Series<String, Number> activeSeries = new XYChart.Series<>();
        activeSeries.setName("Active Subscribers");

        XYChart.Series<String, Number> frozenSeries = new XYChart.Series<>();
        frozenSeries.setName("Frozen Subscribers");

        for (int day = 1; day <= daysInMonth; day++) {
            String dayStr = String.format("%02d", day);
            activeSeries.getData().add(new XYChart.Data<>(dayStr, activeMap.getOrDefault(day, 0)));
            frozenSeries.getData().add(new XYChart.Data<>(dayStr, frozenMap.getOrDefault(day, 0)));
        }

        statusReportChart.getData().clear();
        statusReportChart.getData().addAll(activeSeries, frozenSeries);
    }

    /**
     * Set overlay text (new subscribers) and chart title.
     */
    private void displaySummary(int newSubscribers, int month, int year) {
        chartOverlayText.setText("New subscribers this month: " + newSubscribers);
        statusReportTitle.setText(String.format("Status Report for %02d/%d", month, year));
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) statusReportTitle.getScene().getWindow();
        stage.close();
    }
}
