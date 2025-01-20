package gui;

import client.ChatClient;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.text.Text;
import javafx.stage.Stage;
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
        // Request reports from the server
        ClientGUIConnectionController.chat.reports_accept("create status report", month, year);
        ClientGUIConnectionController.chat.reports_accept("how many joined", month, year);

        if (ChatClient.FullStatusRep == null || ChatClient.FullStatusRep.isEmpty()) {
            showAlert("No information to display.");
            closeWindow();
            return;
        }

        // Initialize daily counters
        Map<String, Integer> activeCountByDay = new HashMap<>();
        Map<String, Integer> frozenCountByDay = new HashMap<>();

        int newSubscribers = ChatClient.SubCnt; // Get the number of new subscribers for the month

        for (String record : ChatClient.FullStatusRep) {
            String[] parts = record.split(" , ");
            if (parts.length >= 1) {
                String joinDate = parts[0].split(":")[1].trim();
                String freezeDate = (parts.length > 1) ? parts[1].split(":")[1].trim() : null;

                // Extract join date values (format: YYYY-MM-DD)
                String[] joinDateParts = joinDate.split("-");
                int joinYear = Integer.parseInt(joinDateParts[0]);
                int joinMonth = Integer.parseInt(joinDateParts[1]);
                int joinDay = Integer.parseInt(joinDateParts[2]);

                boolean joinedBefore = (joinYear < Integer.parseInt(year)) || 
                                       (joinYear == Integer.parseInt(year) && joinMonth <= Integer.parseInt(month));

                // Determine the freeze day or assume they never froze
                int freezeDay = (freezeDate != null && !freezeDate.equals("None")) 
                                ? Integer.parseInt(freezeDate.split("-")[2]) 
                                : 32;  // Assume it never froze if no freeze date

                if (joinedBefore) {
                    for (int day = (joinMonth == Integer.parseInt(month) ? joinDay : 1); day < freezeDay; day++) {
                        activeCountByDay.put(String.format("%02d", day), activeCountByDay.getOrDefault(String.format("%02d", day), 0) + 1);
                    }
                }

                // If the subscriber has frozen, add to the frozen count from freeze day onward
                if (freezeDate != null && !freezeDate.equals("None")) {
                    for (int day = Integer.parseInt(freezeDate.split("-")[2]); day <= 31; day++) {
                        frozenCountByDay.put(String.format("%02d", day), frozenCountByDay.getOrDefault(String.format("%02d", day), 0) + 1);
                    }
                }
            }
        }

        // Set up the bar chart data
        XYChart.Series<String, Number> activeSeries = new XYChart.Series<>();
        activeSeries.setName("Active Subscribers");

        XYChart.Series<String, Number> frozenSeries = new XYChart.Series<>();
        frozenSeries.setName("Frozen Subscribers");

        // Populate data for each day in the month
        for (int day = 1; day <= 31; day++) {
            String dayStr = String.format("%02d", day);
            activeSeries.getData().add(new XYChart.Data<>(dayStr, activeCountByDay.getOrDefault(dayStr, 0)));
            frozenSeries.getData().add(new XYChart.Data<>(dayStr, frozenCountByDay.getOrDefault(dayStr, 0)));
        }

        statusReportChart.getData().clear();
        statusReportChart.getData().addAll(activeSeries, frozenSeries);

        // Display only new subscribers count in the summary
        chartOverlayText.setText("New subscribers this month: " + newSubscribers);

        statusReportTitle.setText("Status Report for " + month + " " + year);
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
