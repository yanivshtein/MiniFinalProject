package gui;

import java.io.IOException;
import java.util.ArrayList;

import client.ChatClient;
import client.ClientUI;
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


public class ClientGUIUpdateDetailsController {
	@FXML
	private Button show = null;
	@FXML
	private Button update = null;
	@FXML
	private Button exitBtn = null;
	@FXML
	private Button view = null;
	@FXML
	private TextField secondID = null;
	
	@FXML
	private TextArea HistoryView = null;

	@FXML
	private TextField secondName = null;

	@FXML
	private TextField secondPhoneLeft = null;

	@FXML
	private TextField secondHistory = null;

	@FXML
	private TextField secondEmailLeft = null;

	@FXML
	private TextField secondPhoneRight = null;

	@FXML
	private TextField secondEmailRight = null;
	
	@FXML
	private DialogPane afterUpdate = null;
	
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/gui/ClientGUIHomePage.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/ClientGUIHomePage.css").toExternalForm());
		primaryStage.setTitle("Client GUI");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public void getSelectBtn(ActionEvent event) throws IOException{
		ClientGUIConnectionController.chat.accept("select", ClientGUILoginController.id, "", ""); //retrieve Subscriber info
		Subscriber1 sub = ChatClient.s1; 
		secondID.setText(String.valueOf(sub.getSubscriber_id())); //cast from int to string
		secondName.setText(sub.getSubscriber_name());
		secondHistory.setText(String.valueOf(sub.getDetailed_subscription_history()));
		secondPhoneLeft.setText(sub.getSubscriber_phone_number());
		secondEmailLeft.setText(sub.getSubscriber_email());
	}
	
	public void getViewBtn(ActionEvent event) throws IOException {
	    // Send the request to the server for the activity history
		ClientGUIConnectionController.chat.accept("watch activity history", ClientGUILoginController.id, "", "");
	    
	    // Get the activity history from the client
	    ArrayList<String> activityHistory = ChatClient.activityHistory;
	    
	    // Check if activityHistory is empty
	    if (activityHistory == null || activityHistory.isEmpty()) {
	        HistoryView.setText("No activity history found.");
	        return;
	    }

	    // Create a formatted string for the matrix display
	    StringBuilder historyMatrix = new StringBuilder();
	    
	    // "Book Name: <book_name>, Action: <action>, Date: <date>, Details: <details>"
	    for (int i = 0; i < activityHistory.size(); i++) {
	        String activity = activityHistory.get(i);
	        
	        // Here, I'm assuming you have a structured format like "Book Name: <book_name>, Action: <action>, ..."
	        // If you have specific structure, you can split and format accordingly. Example below:
	        String[] activityDetails = activity.split(","); // Split by commas for example
	        
	        // Append each activity's details on separate lines
	        historyMatrix.append("Book Name: ").append(activityDetails[0].split(":")[1].trim()).append("\n");
	        historyMatrix.append("Action: ").append(activityDetails[1].split(":")[1].trim()).append("\n");
	        historyMatrix.append("Date: ").append(activityDetails[2].split(":")[1].trim()).append("\n");
	        historyMatrix.append("Details: ").append(activityDetails[3].split(":")[1].trim()).append("\n");
	        
	        // Add a separator between activities (optional)
	        historyMatrix.append("\n-------------------------\n\n");
	    }
	    
	    // Set the formatted string into the HistoryView TextArea
	    HistoryView.setText(historyMatrix.toString());
	}



	public void getUpdatedBtn(ActionEvent event) throws IOException{
		afterUpdate.setContentText("Update sent");
		ClientGUIConnectionController.chat.accept("update", ClientGUILoginController.id, secondPhoneRight.getText(), secondEmailRight.getText());
	}
	
	public void getExitBtn(ActionEvent event) throws IOException {
		System.out.println("Exit client");
		System.exit(0);
	}
}
