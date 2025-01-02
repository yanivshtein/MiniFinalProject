package gui;

import java.io.IOException;
import java.util.ArrayList;

import client.ChatClient;
import client.ClientUI;
import client.ReaderCardLibrariaViewUI;
import common.Subscriber1;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
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
	
	private ArrayList<String> borrowHistory;
	
	
	
	
	public void ViewDetBtt(ActionEvent event) throws IOException {
	    // Retrieve Subscriber info
	    ReaderCardLibrariaViewUI.chat.accept("select", subID.getText(), "", ""); 
	    Subscriber1 sub = ChatClient.s1; 
	    subID.setText(String.valueOf(sub.getSubscriber_id())); // Cast from int to string
	    name.setText(sub.getSubscriber_name());
	    phone_number.setText(sub.getSubscriber_phone_number());
	    email.setText(sub.getSubscriber_email());
	    
	    // Retrieve borrow history
	    ReaderCardLibrariaViewUI.chat.accept("watch borrow history", subID.getText(), "", "");
	    // Get the borrow history from the client
	     borrowHistory = ChatClient.borrowHistory;
	    
	    // Check if borrowHistory is empty
	    if (borrowHistory == null || borrowHistory.isEmpty()) {
	        Bview.setText("No borrow history found.");
	        return;
	    }

	    // Iterate through borrowHistory and append each entry to the TextArea
	    for (int i = 0; i < borrowHistory.size(); i++) {
	        Bview.appendText(borrowHistory.get(i));  // Append current borrow history
	        Bview.appendText("\n\n");  // Add a new line after each entry
	    }
	}

	
	public void ManReturnBtt(ActionEvent event) throws IOException {
	    for (int i = 0; i < borrowHistory.size(); i++) {
	        if (borrowHistory.get(i).contains(BookName.getText())) {
	            int dateIndex = borrowHistory.get(i).indexOf("Date: ");
	            if (dateIndex != -1) {
	                // Start of the date and time string (after "Date: ")
	                int startIndex = dateIndex + 6; // 6 is the length of "Date: "
	                // Extract the substring for the date and time (19 characters for dd-mm-yyyy hh:mm:ss)
	                String dateTimePart = borrowHistory.get(i).substring(startIndex, startIndex + 19);
	                OldRetDate.setText(dateTimePart);
	                break;
	            }
	        }
	    }
	}

	
	public void SaveChangBtt(ActionEvent event) throws IOException {
	    // Call the method to update the return date
	    ReaderCardLibrariaViewUI.chat.book_accept("set new return date", subID.getText(), BookName.getText(), OldRetDate.getText(), NewRetDate.getText());

	    // Check the result of the update
	    if (ChatClient.bool) {
	        ChangesSavedPop.setContentText("Updated successfully");
	    } else {
	        ChangesSavedPop.setContentText("The update failed");
	    }
	}



	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/gui/LibrarianWatchAndUpdateGUI.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/LibrarianWatchAndUpdateGUI.css").toExternalForm());
		primaryStage.setTitle("Librarian watch and update GUI");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

}
	