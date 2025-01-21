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

/**
 * Controller for the Librarian Messages interface.
 * Displays messages received by the librarian and handles navigation actions.
 */
public class LibrarianMessagesController {
	
	@FXML
	 private DialogPane msg;
	
	@FXML
	 private ListView<String> messages;
	 
	String LibName = ChatClient.lib.getLibrarian_name();
	
	/**
     * Initializes the controller by populating the list of messages for the librarian.
     * Fetches messages from the server.
     */
	 @FXML
	  public void initialize() {
		msg.setContentText(LibName + "'s Messages:");
		ClientGUIConnectionController.chat.acceptMessagesForLibrarian();;
		ArrayList<String> subMessages = ChatClient.libMessages;
        // Populate the List
        messages.getItems().addAll(subMessages);
	}
	 
	 /**
     * Handles the "Return" button action to navigate back to the librarian's home page.
     *
     * @param event The action event triggered by the "Return" button.
     * @throws IOException If the FXML file for the home page cannot be loaded.
     */
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
	 /**
     * Handles the "EXIT" button action to close the application.
     *
     * @param event The action event triggered by the "Exit" button.
     * @throws IOException Not applicable for this method but declared for consistency.
     */
	 public void getExitBtn(ActionEvent event) throws IOException {
	        System.exit(0);
	    }
}
