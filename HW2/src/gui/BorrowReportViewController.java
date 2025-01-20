package gui;

import client.ChatClient;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.Map;

public class BorrowReportViewController {

    @FXML
    private Text borrowReportTitle;

    @FXML
    private TextArea borrowReportContent;

    @FXML
    private BarChart<String, Number> borrowBarChart;

    public void loadBorrowReport(String month, String year) {
        // Request report from the server
        ClientGUIConnectionController.chat.reports_accept("create borrow report", month, year);

        if (ChatClient.FullBorrowRep == null || ChatClient.FullBorrowRep.isEmpty()) {
            showAlert("No information to display.");
            closeWindow();
            return;
        }

        StringBuilder reportBuilder = new StringBuilder("### Borrow Report for ")
            .append(month).append("/").append(year).append(" ###\n\n");

        reportBuilder.append(String.format("%-15s %-40s %-15s %-15s %-15s %-10s\n",
            "Subscriber ID", "Book Name", "Borrow Date", "Return Date", "Deadline", "Status"));
        reportBuilder.append("-".repeat(115)).append("\n");

        Map<String, Integer> borrowCount = new HashMap<>();
        Map<String, Integer> returnCount = new HashMap<>();
        Map<String, Integer> overdueCount = new HashMap<>();

        for (String record : ChatClient.FullBorrowRep) {
            String[] parts = record.split(" , ");
            if (parts.length == 6) {
                String borrowDate = parts[2].split(":")[1].trim();
                String returnDate = parts[3].split(":")[1].trim();
                String deadline = parts[4].split(":")[1].trim();
                String status = parts[5].split(":")[1].trim();

                // Extract day from the date (format: YYYY-MM-DD)
                String borrowDay = borrowDate.split("-")[2];
                String returnDay = returnDate.equals("__-__-____") ? null : returnDate.split("-")[2];

                // Update borrow count per day
                borrowCount.put(borrowDay, borrowCount.getOrDefault(borrowDay, 0) + 1);
                
                // Count overdue books
                if (status.toLowerCase().contains("late")) {
                    overdueCount.put(returnDay, overdueCount.getOrDefault(returnDay, 0) + 1);
                }

                // Update return count if return date exists
                else if (returnDay != null) {
                    returnCount.put(returnDay, returnCount.getOrDefault(returnDay, 0) + 1);
                }

               

                reportBuilder.append(String.format("%-15s %-40s %-15s %-15s %-15s %-10s\n",
                    parts[0].split(":")[1].trim(), parts[1].split(":")[1].trim(),
                    borrowDate, returnDate, deadline, status));
            }
        }

        borrowReportTitle.setText("Borrow Report for " + month + " " + year);
        borrowReportContent.setText(reportBuilder.toString());
        borrowReportContent.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 14px;");

        populateBarChart(borrowCount, returnCount, overdueCount);
    }

    private void populateBarChart(Map<String, Integer> borrowCount, Map<String, Integer> returnCount, Map<String, Integer> overdueCount) {
        borrowBarChart.getData().clear();

        XYChart.Series<String, Number> borrowSeries = new XYChart.Series<>();
        borrowSeries.setName("Borrowed Books");

        XYChart.Series<String, Number> returnSeries = new XYChart.Series<>();
        returnSeries.setName("Returned On Time");

        XYChart.Series<String, Number> overdueSeries = new XYChart.Series<>();
        overdueSeries.setName("Returned Late");

        // Fill series with daily data
        for (int day = 1; day <= 31; day++) {
            String dayStr = String.format("%02d", day);
            borrowSeries.getData().add(new XYChart.Data<>(dayStr, borrowCount.getOrDefault(dayStr, 0)));
            returnSeries.getData().add(new XYChart.Data<>(dayStr, returnCount.getOrDefault(dayStr, 0)));
            overdueSeries.getData().add(new XYChart.Data<>(dayStr, overdueCount.getOrDefault(dayStr, 0)));
        }

        borrowBarChart.getData().addAll(borrowSeries, returnSeries, overdueSeries);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) borrowReportTitle.getScene().getWindow();
        stage.close();
    }
}
