package gui;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Controller class for the Add Subscriber GUI.
 * This class handles user interactions and business logic for adding new subscribers.
 */
public class AddSubscriberGUIController {
	
	@FXML
	private Button save = null;
	
	@FXML
	private Button return1 = null;
	
	@FXML
	private Button exit = null;
	
	
	@FXML
	private TextField name = null;
	
	@FXML
	private TextField password = null;
	
	@FXML
	private TextField phoneNumber = null;
	
	@FXML
	private TextField email = null;
	
	@FXML
	private Label afterUpdate = null;
	
	/**
	 * Initializes and displays the Add Subscriber GUI.
	 * 
	 * @param primaryStage The primary stage for this GUI.
	 * @throws Exception If an error occurs while loading the FXML file.
	 */			
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/gui/AddSubscriberGUIController.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
        primaryStage.setTitle("Add subscriber");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
	/**
	 * Handles the Save button click event.
	 * Validates input fields and adds a new subscriber if all fields are valid.
	 * 
	 * @param event The ActionEvent triggered by the Save button.
	 */
    public void saveBtn(ActionEvent event) {
    	 afterUpdate.setText("");
        StringBuilder missingFields = new StringBuilder();
        Alert alert = new Alert(Alert.AlertType.WARNING);
        // Check which fields are empty and append them to the missingFields message
        if (name.getText().isEmpty()) {
            missingFields.append("Name, ");
        }
        if (phoneNumber.getText().isEmpty()) {
            missingFields.append("Phone Number, ");
        }
        if (email.getText().isEmpty()) {
            missingFields.append("Email, ");
        }
        if (password.getText().isEmpty()) {
            missingFields.append("Password, ");
        }
        
        // If there are missing fields, show a warning message
        if (missingFields.length() > 0) {
            // Remove the trailing comma and space
            missingFields.setLength(missingFields.length() - 2);
            alert.setTitle("Missing Fields");
            alert.setHeaderText("Some fields are missing");
            alert.setContentText("Please fill in the following fields: " + missingFields.toString());
            alert.showAndWait();
            return;
        } else {
        	// Validate email format
        	if(email.getText().indexOf("@") == -1) {
                alert.setTitle("Error");
                alert.setContentText("Please enter a valid email");
                alert.showAndWait();  
                return;
        	}
        	// Validate phone number
        	String cleanedText = phoneNumber.getText().replaceAll("[^\\d]", ""); 
        	if (cleanedText.length() != 10 || !cleanedText.matches("\\d+")) {
        	    alert.setTitle("Error");
        	    alert.setContentText("Please enter a valid phone number");
        	    alert.showAndWait();
        	    return;
        	}
            // If all fields are filled, proceed with the action
            String status = "active";
            ClientGUIConnectionController.chat.acceptAddSubscriber( name.getText(), phoneNumber.getText(), email.getText(), status, password.getText());
            afterUpdate.setText("Added");
        }
    }
	/**
	 * Handles the Exit button click event.
	 * Exits the application.
	 * 
	 * @param event The ActionEvent triggered by the Exit button.
	 * @throws IOException If an I/O error occurs.
	 */
	public void getExitBtn(ActionEvent event) throws IOException {
		System.out.println("Exit client");
		System.exit(0);
	}
	/**
	 * Handles the Return button click event.
	 * Returns to the Librarian Home Page.
	 * 
	 * @param event The ActionEvent triggered by the Return button.
	 * @throws IOException If an error occurs while loading the FXML file.
	 */
	public void getReturnBtn(ActionEvent event) throws IOException{
		FXMLLoader loader = new FXMLLoader();
		((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary window
        Stage primaryStage = new Stage();
        Pane root = loader.load(getClass().getResource("/gui/LibrarianGUIHomePageController.fxml").openStream());

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
        primaryStage.setTitle("Labrarian home page");

        primaryStage.setScene(scene);
        primaryStage.show();
	}

}