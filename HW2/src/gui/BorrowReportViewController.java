package gui;

import client.ChatClient;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class BorrowReportViewController {

    @FXML
    private Text borrowReportTitle;

    @FXML
    private TextArea borrowReportContent;

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

        for (String record : ChatClient.FullBorrowRep) {
            String[] parts = record.split(" , ");
            if (parts.length == 6) {
                String subId = parts[0].split(":")[1].trim();
                String bookName = parts[1].split(":")[1].trim();
                String borrowDate = parts[2].split(":")[1].trim();
                String returnDate = parts[3].split(":")[1].trim();
                String deadline = parts[4].split(":")[1].trim();
                String status = parts[5].split(":")[1].trim();

                reportBuilder.append(String.format("%-15s %-40s %-15s %-15s %-15s %-10s\n",
                    subId, bookName, borrowDate, returnDate, deadline, status));
            }
        }

        borrowReportTitle.setText("Borrow Report for " + month + " " + year);
        borrowReportContent.setText(reportBuilder.toString());

        // Apply monospaced font for better tabular alignment
        borrowReportContent.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 14px;");
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
