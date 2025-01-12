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
                int entryNumber = 1; // Start from index 1
                for (int i = 1; i < ChatClient.FullBorrowRep.size(); i++) {  // Start from index 1
                    String reportEntry = ChatClient.FullBorrowRep.get(i);
                    reportBuilder.append(String.format("Entry %d:\n%s\n", entryNumber++, reportEntry));
                    reportBuilder.append("\n-------------------------------\n");  // Separator between entries
                }


                // Display the formatted report in the text area
                Displayarea.setText(reportBuilder.toString());
            }
        }
        else if (statusRep.isSelected()) {
        	ClientGUIConnectionController.chat.reports_accept("create status report", selectedMonth , selectedYear );

            // Build a single string to display all borrow report entries
            StringBuilder reportBuilder = new StringBuilder();
            if (ChatClient.FullStatusRep == null || ChatClient.FullStatusRep.isEmpty()) {
                Displayarea.setText("No information to display.");
            } else {
                // Add a title for the report
                reportBuilder.append("### Status Report for ").append(selectedMonth).append("/").append(selectedYear).append(" ###\n\n");

                // Iterate over each entry and format it
                int entryNumber = 1; // Start from index 1
                for (int i = 1; i < ChatClient.FullStatusRep.size(); i++) {  // Start from index 1
                    String reportEntry = ChatClient.FullStatusRep.get(i);
                    reportBuilder.append(String.format("Entry %d:\n%s\n", entryNumber++, reportEntry));
                    reportBuilder.append("\n-------------------------------\n");  // Separator between entries
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