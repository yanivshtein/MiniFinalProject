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
import javafx.scene.control.ListView;
import javafx.stage.Stage;

/**
 * Controller class for the Subscriber Messages view.
 * This class manages the display of subscriber messages and provides options to return to the home page or exit the application.
 */
public class SubscriberMessagesController {
	

	@FXML
	 private ListView<String> messages;
	 
	String subName = ChatClient.sub1.getSubscriber_name();
	
	/**
     * Initializes the controller by setting the subscriber's name in the message dialog
     * and populating the ListView with their messages from the server.
     */
	 @FXML
	  public void initialize() {
		ClientGUIConnectionController.chat.acceptFromController(29, ChatClient.sub1.getSubscriber_id(), "");
		ArrayList<String> subMessages = ChatClient.subMessages;
        // Populate the List
        messages.getItems().addAll(subMessages);
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
