package gui;

import client.ChatClient;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class StatusReportViewController {

    @FXML
    private Text statusReportTitle;
    
    @FXML
    private Text chartOverlayText;

    @FXML
    private PieChart statusReportChart;

    public void loadStatusReport(String month, String year) {
    	
        ClientGUIConnectionController.chat.reports_accept("create status report", month, year);
        ClientGUIConnectionController.chat.reports_accept("how many joined", month, year);

        if (ChatClient.FullStatusRep == null || ChatClient.FullStatusRep.isEmpty()) {
            showAlert("No information to display.");
            closeWindow();
            return;
        }

        int activeCount = 0;
        int frozenCount = 0;

        for (String record : ChatClient.FullStatusRep) {
            String[] parts = record.split(" , ");
            if (parts.length >= 3) {
                String status = parts[2].split(":")[1].trim();

                if (status.equalsIgnoreCase("Active")) {
                    activeCount++;
                } else if (status.equalsIgnoreCase("Frozen")) {
                    frozenCount++;
                }
            }
        }

        int totalSubscribers = activeCount + frozenCount;
        
        // Calculate percentages
        double activePercentage = totalSubscribers > 0 ? ((double) activeCount / totalSubscribers) * 100 : 0;
        double frozenPercentage = totalSubscribers > 0 ? ((double) frozenCount / totalSubscribers) * 100 : 0;

        // Format percentages with 2 decimal places
        String formattedActivePercentage = String.format("%.2f", activePercentage);
        String formattedFrozenPercentage = String.format("%.2f", frozenPercentage);

        // Set chart data
        PieChart.Data activeData = new PieChart.Data("Active (" + formattedActivePercentage + "%)", activeCount);
        PieChart.Data frozenData = new PieChart.Data("Frozen (" + formattedFrozenPercentage + "%)", frozenCount);

        statusReportTitle.setText("Status Report for " + month + " " + year);
        statusReportChart.getData().setAll(activeData, frozenData);

        // Update chart overlay text with percentages
        chartOverlayText.setText(
                "Total: " + totalSubscribers +
                "\nActive: " + activeCount + " (" + formattedActivePercentage + "%)" +
                "\nFrozen: " + frozenCount + " (" + formattedFrozenPercentage + "%)" +
                "\nNew subscribers this month: " + ChatClient.SubCnt);

        chartOverlayText.setStyle("-fx-font-size: 14px; -fx-fill: black;");
    }


    private void showAlert(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
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
