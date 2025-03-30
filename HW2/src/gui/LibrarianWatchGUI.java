package gui;

import java.io.IOException;
import java.util.ArrayList;
import client.ChatClient;
import common.Subscriber1;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;

/**
 * Controller class for the Librarian Watch GUI.
 * Enables librarians to view and manage subscriber details and borrowing history.
 */
public class LibrarianWatchGUI {
	@FXML
	private Pane pane;

	@FXML
	private Button ViewRelevantBooks, ViewClick, ViewClick1, exitbtn, exitbtn1, ManBorrowClick, SaveChangesbtt,
			RetButton, RetButton1;

	@FXML
	private ListView<String> RelevantBooks;

	@FXML
	private Text namelabel, emaillabel, phonelabel, statuslabel, borrowhislabel, OriginalDate, NewDate;

	@FXML
	private TextField subID;

	@FXML
	private Label   name, phone_number, email, history, SubStatus;

	
	@FXML
	private TextArea Bview ;

	@FXML
	private DialogPane ChangesSavedPop;

	
	/**
     * Initializes the GUI by setting styles and any necessary setup logic.
     */
	public void initialize() {       
		Bview.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12;");
    }

	/**
     * Handles the "View Details" button click to fetch and display subscriber details and activity history.
     *
     * @param event The action event triggered by the button click.
     * @throws IOException If an I/O error occurs.
     */
	public void ViewDetBtt(ActionEvent event) throws IOException {
		//fetch subscriber details to display
		ClientGUIConnectionController.chat.accept("select", subID.getText(), "", "");
		Subscriber1 sub = ChatClient.s1;
		Alert alert = new Alert(Alert.AlertType.WARNING);
		if (sub.getSubscriber_id() == 0) {
			alert.setTitle("Not found");
            alert.setContentText("Oops! Subscriber not found");
            alert.showAndWait();
			name.setText("");
			phone_number.setText("");
			email.setText("");
			SubStatus.setText("");
			return;

		}
		subID.setText(String.valueOf(sub.getSubscriber_id()));
		name.setText(sub.getSubscriber_name());
		phone_number.setText(sub.getSubscriber_phone_number());
		email.setText(sub.getSubscriber_email());
		SubStatus.setText(sub.getSub_status());
		//fetch subscriber activity history
		ClientGUIConnectionController.chat.accept("watch activity history", "", "", sub.getSubscriber_email());
		ArrayList<String> activityHistory = ChatClient.activityHistory;
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
		Bview.setText(historyMatrix.toString());
		
		
		
	}

	/**
     * Navigates back to the librarian's home page.
     *
     * @param event The action event triggered by the button click.
     * @throws IOException If an I/O error occurs.
     */
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

	/**
     * Opens the return book page.
     *
     * @param event The action event triggered by the button click.
     * @throws IOException If an I/O error occurs.
     */
	public void returnBookBtt(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader();

		// ((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary
		// window
		Stage primaryStage = new Stage();
		Pane root = loader.load(getClass().getResource("/gui/LibrarianReturnGUI.fxml").openStream());

		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
		primaryStage.setTitle("Librarian Return GUI");

		primaryStage.setScene(scene);
		primaryStage.show();

	}
	
	/**
     * Exits the application.
     *
     * @param event The action event triggered by the button click.
     */
	public void getExitBtn(ActionEvent event) throws IOException {
		System.exit(0);
	}

}
