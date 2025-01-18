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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;



public class LibrarianUpdateGUI {
	@FXML
	private Pane pane;

	@FXML
	private Button ViewRelevantBooks, ViewClick, ViewClick1, exitbtn, exitbtn1, ManBorrowClick, SaveChangesbtt, RetButton, RetButton1;

	@FXML
	private ListView<String> RelevantBooks;

	@FXML
	private Text namelabel, emaillabel, phonelabel, statuslabel, borrowhislabel, OriginalDate, NewDate;

	@FXML
	private TextField subID, subID1, name, phone_number, email, history, OldRetDate, NewRetDate, SubStatus;

	@FXML
	private TextArea Bview;

	@FXML
	private DialogPane ChangesSavedPop;
	Alert alert = new Alert(Alert.AlertType.WARNING);
	private String TempStatus, selectedBook;
	private Boolean statusCheck = null;
	private ArrayList<String> borrowHistory;
	
	@FXML
	private void initialize() {
	    RelevantBooks.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
	    });
	}

	public void ViewRelevantBooksclick(ActionEvent event) throws IOException {
		
		if(subID1.getText().isEmpty()) {
			alert.setTitle("No ID Was Entered");
            alert.setContentText("You must enter an ID!");
            alert.showAndWait();
	        return;
			
		}
	    // Check if the frozen status has already been evaluated
	    if (statusCheck == null || statusCheck == true) { 
	        // Evaluate the frozen status only once
	        ClientGUIConnectionController.chat.accept("check if frozen", subID1.getText(), "", "");
	        statusCheck = ChatClient.isFrozen;
	    }

	    if(statusCheck == null) {
	    	alert.setTitle("Not found");
            alert.setContentText("Subscriber not found.");
            alert.showAndWait();
	        return;
	    }
	    else if (statusCheck) {
	    	alert.setTitle("Frozen account");
            alert.setContentText("The account is frozen. It is not possible to extend the borrow.");
            alert.showAndWait();
	        return;
	    }

	    ArrayList<String> bookNames = new ArrayList<>();
	    ArrayList<String> deadlines = new ArrayList<>();

	    // Fetch books near the deadline
	    ClientGUIConnectionController.chat.accept("watch books to extend", subID1.getText(), "", "");
	    ArrayList<String> booksNearDeadline = ChatClient.booksNearDeadline;

	    if (booksNearDeadline == null || booksNearDeadline.isEmpty()) {
	    	alert.setTitle("No relevant books found.");
            alert.setContentText("No relevant books found.");
            alert.showAndWait();
	        RelevantBooks.getItems().clear();  // Clear the ListView if no relevant books are found

	        return;
	    }

	    // Populate bookNames and deadlines
	    for (int i = 0; i < booksNearDeadline.size(); i += 2) {
	        bookNames.add(booksNearDeadline.get(i).trim());
	        deadlines.add(booksNearDeadline.get(i + 1).trim());
	    }

	    if (RelevantBooks != null) {
	        RelevantBooks.getItems().clear();
	        RelevantBooks.getItems().addAll(bookNames);
	    }

	    // Wait until the user selects a book
	    RelevantBooks.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
	        if (newValue != null) {
	            selectedBook = newValue; // Set the selected book
	            for (int i = 0; i < bookNames.size(); i++) {
	                if (bookNames.get(i).equals(selectedBook)) {
	                    OldRetDate.setText(deadlines.get(i)); // Set the deadline
	                    break;
	                }
	            }
	        }
	    });
	}


		

 
    
    public void SaveChangBtt(ActionEvent event) throws IOException {
    	if(statusCheck)
			return;
        if (!DateValidation(OldRetDate.getText(), NewRetDate.getText())) {
        	alert.setTitle("Invalid date");
            alert.setContentText("Invalid New Date! - Must be both after the old date and within 2 weeks!");
            alert.showAndWait();
            return;
        }
            
        ClientGUIConnectionController.chat.book_accept("set new return date", subID1.getText(), selectedBook, OldRetDate.getText(), NewRetDate.getText() , LibrarianGUIHomePageController.BringLibName );

        if (ChatClient.bool) {
            ChangesSavedPop.setContentText("Updated successfully");
        } else {
        	alert.setTitle("Update failed");
            alert.setContentText("Update failed - Book already extended or reserved by another user");
            alert.showAndWait();
        }
    }

    private boolean DateValidation(String oldDateStr, String newDateStr) {
        if (oldDateStr == null || newDateStr == null || oldDateStr.isEmpty() || newDateStr.isEmpty()) {
        	alert.setTitle("Empty string");
            alert.setContentText("Error: One or both date strings are null or empty.");
            alert.showAndWait();
            return false;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date oldDate = dateFormat.parse(oldDateStr);
            Date newDate = dateFormat.parse(newDateStr);

            // Calculate the difference in days
            long diffInDays = TimeUnit.MILLISECONDS.toDays(Math.abs(newDate.getTime() - oldDate.getTime()));

            // Check if new date is after old date AND within 14 days
            return diffInDays < 14 && newDate.after(oldDate);
        } catch (ParseException e) {
        	alert.setTitle("Parsing date");
            alert.setContentText("Error parsing dates: \" + e.getMessage()");
            alert.showAndWait();
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
        scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());

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
		scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
		primaryStage.setTitle("Librarian Return GUI");

		primaryStage.setScene(scene);
		primaryStage.show();
		
	}
	
	 public void getExitBtn(ActionEvent event) throws IOException {
			System.exit(0);
		}
	 
}
	