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
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class SubscriberExtensionController {
	
	 @FXML
	 private ListView<String> booksListView;
	 
	 @FXML
	 private DialogPane msg;
	 
	 private String selectedBook;
	 
	 @FXML
    public void initialize() {
		 ClientGUIConnectionController.chat.acceptFromController(12, ChatClient.subID, "");
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
	                msg.setContentText("You have selected the Book: " + selectedBook + ", press 'Extend' to send");
	            }
	        }
	  }
	 
	 public void getExtendBtn (ActionEvent event) throws IOException {
		 int subID = ChatClient.subID;
		 if (selectedBook==null) {
	            msg.setContentText("Oops! 😞 You must press on a book name.");
	            return;
	        }
		 ClientGUIConnectionController.chat.acceptFromController(24, subID, selectedBook);
		 if (ChatClient.isSeven == false) {
	            msg.setContentText("Uh-oh! 😬 There are more than 7 days until the return date");
	            return;
	     }
		 if (ChatClient.orderExists == true) {
	            msg.setContentText("Sorry! 😬 Someone else is waiting for this book");
	            return;
	     }
		 msg.setContentText("Great! 🎉 You have your book for 7 more days, Have Fun!");		 
	 }
	 
	 public void getReturnBtn(ActionEvent event) throws IOException {
		    // Close the current window
		    ((Node) event.getSource()).getScene().getWindow().hide();
		    
		    // Load the ClientGUIHomePage FXML
		    FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ClientGUIHomePage.fxml"));
		    Parent root = loader.load();
		    
		    Scene scene = new Scene(root);
		    scene.getStylesheets().add(getClass().getResource("/gui/ClientGUIHomePage.css").toExternalForm());
		    
		    Stage primaryStage = new Stage();
		    primaryStage.setTitle("Client Home Page");
		    primaryStage.setScene(scene);
		    primaryStage.show();
		}
	 
	 public void getExitBtn(ActionEvent event) throws IOException {
	        System.exit(0);
	    }
}
