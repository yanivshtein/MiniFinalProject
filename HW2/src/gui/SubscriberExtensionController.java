package gui;

import java.io.IOException;
import java.util.ArrayList;

import client.ChatClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * Controller class for managing book return extensions for subscribers.
 * Allows subscribers to extend the return date of borrowed books, provided specific conditions are met.
 */
public class SubscriberExtensionController {
	
	 @FXML
	 private ListView<String> booksListView;
	 
	 @FXML
	 private Label msg;
	 
	 private String selectedBook;
	 
	 /**
     * Initializes the controller by fetching the list of borrowed books for the subscriber
     * and setting up event handlers.
     */
	 @FXML
    public void initialize() {
		 ClientGUIConnectionController.chat.acceptFromController(12, ChatClient.sub1.getSubscriber_id(), "");
        // Simulate fetching book titles from the database
        ArrayList<String> books = ChatClient.borrowedBooks;
        // Populate the ListView
        booksListView.getItems().addAll(books);

        // Add double-click event handler
        booksListView.setOnMouseClicked(event -> handleDoubleClick(event));
     }
	 
	 /**
     * Handles double-click events on the list of borrowed books.
     * Updates the label with the selected book's name.
     *
     * @param event the mouse event triggered by double-clicking on a book in the ListView.
     */
	 private void handleDoubleClick(MouseEvent event) {
	        if (event.getClickCount() == 2) {
	            selectedBook = booksListView.getSelectionModel().getSelectedItem();
	            if (selectedBook != null) {
	                msg.setText("Book Selected: " + selectedBook);
	            }
	        }
	  }
	 
	 /**
     * Handles the action for extending the return date of a selected book.
     * Validates the selection and checks conditions for extending the return date.
     *
     * @param event the action event triggered by clicking the "Extend" button.
     * @throws IOException if an error occurs during communication with the server.
     */
	 public void getExtendBtn (ActionEvent event) throws IOException {
		 int subID = ChatClient.sub1.getSubscriber_id();
		 Alert alert = new Alert(Alert.AlertType.WARNING);
		 if (selectedBook==null) {
			 alert.setTitle("Missing Field");
             alert.setContentText("Oops! You must press on a book name.");
             alert.showAndWait();
             return;	            
	     }
		 ClientGUIConnectionController.chat.acceptFromController(24, subID, selectedBook);
		 if (ChatClient.isSeven == false) {
			 alert.setTitle("More than seven days");
             alert.setContentText("Uh-oh! There are more than 7 days until the return date");
             alert.showAndWait();
             return;	         
	     }
		 if (ChatClient.orderExists == true) {
			 alert.setTitle("Order exists");
             alert.setContentText("Sorry! Someone else is waiting for this book");
             alert.showAndWait();
             return;	 	        
	     }
		 msg.setText("Great! You have your book for 14 more days, Have Fun!");		 
	 }
	 
	 /**
     * Handles the action for the "Return" button.
     * Navigates back to the Client Home Page.
     *
     * @param event the action event triggered by clicking the "Return" button.
     * @throws IOException if an error occurs while loading the home page FXML.
     */
	 public void getReturnBtn(ActionEvent event) throws IOException {
		    // Close the current window
		    ((Node) event.getSource()).getScene().getWindow().hide();
		    
		    // Load the ClientGUIHomePage FXML
		    FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ClientGUIHomePageController.fxml"));
		    Parent root = loader.load();
		    
		    Scene scene = new Scene(root);
		    scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
		    
		    Stage primaryStage = new Stage();
		    primaryStage.setTitle("Client Home Page");
		    primaryStage.setScene(scene);
		    primaryStage.show();
		}
	 
	 /**
     * Handles the action for the "Exit" button.
     * Closes the application.
     *
     * @param event the action event triggered by clicking the "Exit" button.
     * @throws IOException if an error occurs during the exit process.
     */
	 public void getExitBtn(ActionEvent event) throws IOException {
	        System.exit(0);
	    }
}
