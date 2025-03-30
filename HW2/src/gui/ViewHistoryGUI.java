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
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * Controller class for the View History GUI. This class handles the logic for
 * displaying activity history and returning to the home page.
 */
public class ViewHistoryGUI {

	@FXML
	private TextArea HistoryView = null;

	@FXML
	private Button ret = null;
	
	@FXML
	private Button exit = null;

	/**
	 * Initializes the controller by setting the subscriber's name in the message
	 * dialog and populating the ListView with their messages from the server.
	 */
	@FXML
	public void initialize() {
		// Send the request to the server for the activity history
		ClientGUIConnectionController.chat.accept("watch activity history", "", "", ClientGUILoginController.email);
		// Get the activity history from the client
		ArrayList<String> activityHistory = ChatClient.activityHistory;
		// Create a formatted string for the matrix display
		StringBuilder historyMatrix = new StringBuilder();

		// "Book Name: <book_name>, Action: <action>, Date: <date>, Details: <details>"
		for (int i = 0; i < activityHistory.size(); i++) {
			String activity = activityHistory.get(i);

			String[] activityDetails = activity.split(","); // Split by commas for example

			// Append each activity's details on separate lines
			historyMatrix.append("Book Name: ").append(activityDetails[0].split(":")[1].trim()).append("\n");
			historyMatrix.append("Action: ").append(activityDetails[1].split(":")[1].trim()).append("\n");
			historyMatrix.append("Date: ").append(activityDetails[2].split(":")[1].trim()).append("\n");
			if (!activityDetails[3].split(":")[1].trim().equals("null")) {
				historyMatrix.append("Additional Info: ").append(activityDetails[3].split(":")[1].trim()).append("\n");
			}
			

			// Add a separator between activities (optional)
			historyMatrix.append("\n-------------------------\n\n");
		}

		// Set the formatted string into the HistoryView TextArea
		HistoryView.setText(historyMatrix.toString());
	}

	/**
	 * Handles the action for the "Return" button. Closes the current window and
	 * navigates back to the Client Home Page.
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
