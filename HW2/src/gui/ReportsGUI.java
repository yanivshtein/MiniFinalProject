package gui;

import java.io.IOException;

import client.ChatClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ReportsGUI {

    @FXML
    private RadioButton borrowRep = null;
    @FXML
    private RadioButton statusRep = null;
    @FXML
    private ComboBox<String> months = null;  // Specify type String for ComboBox
    @FXML
    private TextArea Displayarea = null;
    @FXML
    private Button Viewbtt = null;
    @FXML
    private Button ReturnBtn = null;
    @FXML
    private ComboBox<String> years = null;  // Specify type String for ComboBox

    // This method will be called when the FXML file is loaded
    public void initialize() {
        // List of months to populate in the ComboBox
        String[] monthsArray = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };
        
        String[] yearsArray = {
        	    "2020", "2021", "2022", "2023", "2024", "2025"
        	};

        
        // Add the months to the ComboBox
        months.getItems().addAll(monthsArray);
        years.getItems().addAll(yearsArray);
    }
    
    public void ViewBttClick(ActionEvent event) {
        // Get the selected month and year from the ComboBox
        String selectedMonth = getMonthNumber(months.getValue());
        String selectedYear = years.getValue();

        if (borrowRep.isSelected()) {
            // Send a request to create a borrow report
        	ClientGUIConnectionController.chat.reports_accept("create borrow report", selectedMonth , selectedYear );

            // Build a single string to display all borrow report entries
            StringBuilder reportBuilder = new StringBuilder();
            if (ChatClient.FullBorrowRep == null || ChatClient.FullBorrowRep.isEmpty()) {
                Displayarea.setText("No information to display.");
            } else {
                // Add a title for the report
                reportBuilder.append("### Borrow Report for ").append(selectedMonth).append("/").append(selectedYear).append(" ###\n\n");

                // Iterate over each entry and format it
             // First create headers with proper spacing
                reportBuilder.append(String.format("%-20s %-25s %-18s %-18s %-15s %-13s\n",
                    "Subscriber ID", "Book Name", "Borrow Date", "Return Date", "Deadline", "Status"));
                reportBuilder.append("-".repeat(110) + "\n"); // Separator line after headers

                // Format each row with aligned columns
                for (int i = 1; i < ChatClient.FullBorrowRep.size(); i++) {
                    String[] parts = ChatClient.FullBorrowRep.get(i).split(" , ");
                    String subId = parts[0].substring(parts[0].indexOf(":") + 2);
                    String bookName = parts[1].substring(parts[1].indexOf(":") + 2);
                    String borrowDate = parts[2].substring(parts[2].indexOf(":") + 2);
                    String returnDate = parts[3].substring(parts[3].indexOf(":") + 2);
                    String deadline = parts[4].substring(parts[4].indexOf(":") + 2);
                    String status = parts[5].substring(parts[5].indexOf(":") + 2);
                    
                    reportBuilder.append(String.format("%-20s %-30s %-18s %-17s %-15s %-15s\n",
                        subId, bookName, borrowDate, returnDate, deadline, status));
                }

                // Display the formatted report in the text area
                Displayarea.setText(reportBuilder.toString());
            }
        }
        else if (statusRep.isSelected()) {
            ClientGUIConnectionController.chat.reports_accept("create status report", selectedMonth, selectedYear);

            // Build a single string to display all status report entries
            StringBuilder reportBuilder = new StringBuilder();
            if (ChatClient.FullStatusRep == null || ChatClient.FullStatusRep.isEmpty()) {
                Displayarea.setText("No information to display.");
            } else {
                // Add a title for the report
                reportBuilder.append("### Status Report for ").append(selectedMonth).append("/").append(selectedYear).append(" ###\n\n");

                // Create headers with proper spacing
                reportBuilder.append(String.format("%-25s %-15s %-15s\n", "Subscriber Name", "ID", "Status"));
                reportBuilder.append("-".repeat(55) + "\n"); // Separator line after headers

                // Format each row with aligned columns
                for (int i = 1; i < ChatClient.FullStatusRep.size(); i++) {  // Note change to i < FullStatusRep.size()
                    // Parse the entry string to extract the subscriber's name, ID, and status
                    String[] parts = ChatClient.FullStatusRep.get(i).split(" , ");
                    String name = parts[0].substring(parts[0].indexOf(":") + 2).trim();  // Trim to avoid leading/trailing spaces
                    String id = parts[1].substring(parts[1].indexOf(":") + 2).trim();
                    String status = parts[2].substring(parts[2].indexOf(":") + 2).trim();

                    // Append the formatted row to the report with padding to ensure columns remain aligned
                    reportBuilder.append(String.format("%-25s %-15s %-15s\n", name, id, status));
                }

                // Display the formatted report in the text area
                Displayarea.setText(reportBuilder.toString());
            }
        }


    }
    
    


    
    public void BorrowTimeRepClick(ActionEvent event) {
    	statusRep.setSelected(false);
    	
    }
    
    public void SubStatusRepClick(ActionEvent event) {
    	borrowRep.setSelected(false);
    	
    	
    }

    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/gui/ReportsGUI.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/ReportsGUI.css").toExternalForm());
        primaryStage.setTitle("Reports GUI");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    
    public String getMonthNumber(String monthName) {
        String[] monthsArray = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };

        // Loop through the array to find the index of the month
        for (int i = 0; i < monthsArray.length; i++) {
            if (monthsArray[i].equalsIgnoreCase(monthName)) {
                // Format the month number with leading zero if necessary
                return String.format("%02d", i + 1);  // Adds leading zero if the month number is single digit
            }
        }

        // If the month name is not valid, return an error string or throw an exception
        return "Invalid month";  // Invalid month name
    }
    
    public void ReturnButton(ActionEvent event) throws IOException {
        // Get the current stage from the event source
        Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();

        // Load the FXML file for the new page
        Parent root = FXMLLoader.load(getClass().getResource("/gui/LibrarianGUIHomePageController.fxml"));

        // Create a new scene and apply the stylesheet
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/LibrarianGUIHomePageController.css").toExternalForm());

        // Create a new stage for the new page
        Stage newStage = new Stage();
        newStage.setTitle("Librarian Watch and Update GUI");
        newStage.setScene(scene);

        // Hide the current stage
        currentStage.hide();

        // Show the new stage
        newStage.show();
    }



}