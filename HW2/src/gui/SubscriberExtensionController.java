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
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class SubscriberExtensionController {
	
	 @FXML
	 private ListView<String> booksListView;
	 
	 @FXML
	 private Label msg;
	 
	 private String selectedBook;
	 
	 @FXML
    public void initialize() {
		 ClientGUIConnectionController.chat.acceptFromController(12, ChatClient.sub1.getSubscriber_id(), "");
        // Simulate fetching book titles from the database
        ArrayList<String> books = ChatClient.borrowedBooks;
        System.out.println(books);
        // Populate the ListView
        booksListView.getItems().addAll(books);

        // Add double-click event handler
        booksListView.setOnMouseClicked(event -> handleDoubleClick(event));
     }
	 
	 private void handleDoubleClick(MouseEvent event) {
	        if (event.getClickCount() == 2) {
	            selectedBook = booksListView.getSelectionModel().getSelectedItem();
	            if (selectedBook != null) {
	                msg.setText("Book Selected: " + selectedBook);
	            }
	        }
	  }
	 
	 public void getExtendBtn (ActionEvent event) throws IOException {
		 int subID = ChatClient.sub1.getSubscriber_id();
		 Alert alert = new Alert(Alert.AlertType.WARNING);
		 if (selectedBook==null) {
			 alert.setTitle("Missing Field");
             alert.setContentText("Oops! 😞 You must press on a book name.");
             alert.showAndWait();
             return;	            
	     }
		 ClientGUIConnectionController.chat.acceptFromController(24, subID, selectedBook);
		 if (ChatClient.isSeven == false) {
			 alert.setTitle("More than seven days");
             alert.setContentText("Uh-oh! 😬 There are more than 7 days until the return date");
             alert.showAndWait();
             return;	         
	     }
		 if (ChatClient.orderExists == true) {
			 alert.setTitle("Order exists");
             alert.setContentText("Sorry! 😬 Someone else is waiting for this book");
             alert.showAndWait();
             return;	 	        
	     }
		 msg.setText("Great! 🎉 You have your book for 14 more days, Have Fun!");		 
	 }
	 
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
	 
	 public void getExitBtn(ActionEvent event) throws IOException {
	        System.exit(0);
	    }
}
