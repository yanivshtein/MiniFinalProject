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


public class ViewHistoryGUI {
	
	@FXML
	private TextArea HistoryView = null;
	
	@FXML
	private Button ret = null;

	
	public void getViewBtn(ActionEvent event) throws IOException {
		Alert alert = new Alert(Alert.AlertType.WARNING);
	    // Send the request to the server for the activity history
		ClientGUIConnectionController.chat.accept("watch activity history", "", "", ClientGUILoginController.email);
	    
	    // Get the activity history from the client
	    ArrayList<String> activityHistory = ChatClient.activityHistory;
	    
	    // Check if activityHistory is empty
	    if (activityHistory == null || activityHistory.isEmpty()) {
	    	alert.setTitle("Activity History");
            alert.setContentText("No activity history has been found");
            alert.showAndWait();
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
	        
	        // Add a separator between activities (optional)
	        historyMatrix.append("\n-------------------------\n\n");
	    }
	    
	    // Set the formatted string into the HistoryView TextArea
	    HistoryView.setText(historyMatrix.toString());
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



}
