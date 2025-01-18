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
import javafx.stage.Stage;

public class LibrarianMessagesController {
	
	@FXML
	 private DialogPane msg;
	
	@FXML
	 private ListView<String> messages;
	 
	String LibName = ChatClient.lib.getLibrarian_name();
	
	 @FXML
	  public void initialize() {
		msg.setContentText(LibName + "'s Messages:");
		ClientGUIConnectionController.chat.acceptMessagesForLibrarian();;
		ArrayList<String> subMessages = ChatClient.libMessages;
        // Populate the List
        messages.getItems().addAll(subMessages);
	}
	 
	 public void getReturnBtn(ActionEvent event) throws IOException {
		    // Close the current window
		    ((Node) event.getSource()).getScene().getWindow().hide();
		    
		    // Load the ClientGUIHomePage FXML
		    FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/LibrarianGUIHomePageController.fxml"));
		    Parent root = loader.load();
		    
		    Scene scene = new Scene(root);
		    scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
		    
		    Stage primaryStage = new Stage();
		    primaryStage.setTitle("Librarian Home Page");
		    primaryStage.setScene(scene);
		    primaryStage.show();
		}
	 
	 public void getExitBtn(ActionEvent event) throws IOException {
	        System.exit(0);
	    }
}
