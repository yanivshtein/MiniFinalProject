package gui;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import client.ChatClient;
import client.ClientConsole;
import client.ClientUI;
import common.Subscriber1;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;



public class LibrarianWatchAndUpdateGUI {

	@FXML
    private TextField subID = null;
    @FXML
    private TextField name = null;
    @FXML
    private TextField phone_number = null;
    @FXML
    private TextField email = null;
    @FXML
    private TextField history = null;
    @FXML
    private TextArea Bview = null;
    @FXML
    private Button ViewClick = null;
    @FXML
    private Button ManBorrowClick = null;
    @FXML
    private TextField BookName = null;
    @FXML
    private TextField OldRetDate = null;
    @FXML
    private TextField NewRetDate = null;
    @FXML
    private DialogPane ChangesSavedPop = null;
    @FXML
    private Button SaveChangesbtt = null;
    @FXML
    private TextField SubStatus = null; //active OR frozen
    @FXML
    private Button RetButton = null;
    
    private  String TempStatus;
        
    private ArrayList<String> borrowHistory;

    public void ViewDetBtt(ActionEvent event) throws IOException {
    	ClientGUIConnectionController.chat.accept("select", subID.getText(), "", "");
        Subscriber1 sub = ChatClient.s1;
        subID.setText(String.valueOf(sub.getSubscriber_id()));
        name.setText(sub.getSubscriber_name());
        phone_number.setText(sub.getSubscriber_phone_number());
        email.setText(sub.getSubscriber_email());
        SubStatus.setText(sub.getSub_status());
        
        
        ClientGUIConnectionController.chat.accept("watch borrow history", subID.getText(), "", "");
        borrowHistory = ChatClient.borrowHistory;
        

        if (borrowHistory == null || borrowHistory.isEmpty()) {
            Bview.setText("No borrow history found.");
            return;
        }

        for (int i = 0; i < borrowHistory.size(); i++) {
            Bview.appendText(borrowHistory.get(i));
            Bview.appendText("\n\n");
        }
        TempStatus = sub.getSub_status();
        if (TempStatus.equals("frozen")) {
        	ChangesSavedPop.setContentText("The account is frozen. It is not possible to extend the borrow.");
        	return;
        	
        }
    }

    public void ManReturnBtt(ActionEvent event) throws IOException {
        if (TempStatus.equals("frozen")) {
            return;
        }
        for (int i = 0; i < borrowHistory.size(); i++) {
            if (borrowHistory.get(i).contains(BookName.getText())) {
            	
                // Extract the "Date" portion
                int dateIndex = borrowHistory.get(i).indexOf("Deadline: ");
                if (dateIndex != -1) {
                    int startIndex = dateIndex + 10; // Length of "Date: " is 10
                    int endIndex = borrowHistory.get(i).indexOf(",", startIndex);
                    
                    // If there's no comma, assume the date goes to the end of the string
                    if (endIndex == -1) {
                        endIndex = borrowHistory.get(i).length();
                    }
                    
                    String dateTimePart = borrowHistory.get(i).substring(startIndex, endIndex).trim();
                    OldRetDate.setText(dateTimePart);
                    break;
                }
            }
        }
    }


    public void SaveChangBtt(ActionEvent event) throws IOException {
    	if (TempStatus.equals("frozen")) {
    		return;
    	}
        if (!isLessThanTwoWeeks(OldRetDate.getText(), NewRetDate.getText())) {
            ChangesSavedPop.setContentText("Invalid New Date! - must be less than 2 weeks!");
            return;
        }

        ClientGUIConnectionController.chat.book_accept("set new return date", subID.getText(), BookName.getText(), OldRetDate.getText(), NewRetDate.getText() , LibrarianGUIHomePageController.BringLibName );

        if (ChatClient.bool) {
            ChangesSavedPop.setContentText("Updated successfully");
        } else {
            ChangesSavedPop.setContentText("The update failed - An extension has already been made ");
        }
    }

    private boolean isLessThanTwoWeeks(String oldDateStr, String newDateStr) {
        if (oldDateStr == null || newDateStr == null || oldDateStr.isEmpty() || newDateStr.isEmpty()) {
            ChangesSavedPop.setContentText("Error: One or both date strings are null or empty.");
            return false;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date oldDate = dateFormat.parse(oldDateStr);
            Date newDate = dateFormat.parse(newDateStr);

            // Calculate the difference in days
            long diffInDays = TimeUnit.MILLISECONDS.toDays(Math.abs(newDate.getTime() - oldDate.getTime()));

            // Return true if less than 14 days
            return diffInDays < 14;
        } catch (ParseException e) {
            ChangesSavedPop.setContentText("Error parsing dates: " + e.getMessage());
            return false;
        }
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



	
	public void returnBookBtt(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader();

		//((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary window
		Stage primaryStage = new Stage();
		Pane root = loader.load(getClass().getResource("/gui/LibrarianReturnGUI.fxml").openStream());

		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/LibrarianReturnGUI.css").toExternalForm());
		primaryStage.setTitle("Librarian Return GUI");

		primaryStage.setScene(scene);
		primaryStage.show();
		
	}



}
	