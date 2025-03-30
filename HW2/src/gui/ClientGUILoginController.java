package gui;

import java.io.IOException;
import client.ChatClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Controller class for handling the login functionality in the client GUI.
 * It supports login for two user types: Subscriber and Librarian.
 * Handles user authentication, image display, and scene transitions.
 */
public class ClientGUILoginController {

	public static String email;
	public static String passwordString;
	public static String id;
	
	
	@FXML
	private Button exit = null;

	@FXML
	private Button enter = null;
	
	@FXML
	private Button search = null;
	
	@FXML
	private RadioButton radio_sub = null;

	@FXML
	private RadioButton radio_lib = null;
	
	
	@FXML
	private TextField userName = null;
	
	@FXML
	private TextField password = null;
	
	@FXML
	private ImageView picL = null;
	
	@FXML
	private ImageView picS = null;
	
	
    /**
     * Initializes the controller, setting default images for the librarian and subscriber options.
     * Sets the images to be displayed for Librarian and Subscriber.
     */
	@FXML
    public void initialize() {
        // Check if the image is already set by Scene Builder (no need to do this unless you need to update it)
        if (picL.getImage() == null) {
            // Optionally set a default image or handle error
            picL.setImage(new Image("/resources/LibrarianPic.png"));
        }
        if (picS.getImage() == null) {
            // Optionally set a default image or handle error
            picS.setImage(new Image("/resources/UserPic.png"));
        }
    }



	private String user = "Sub";
	
    /**
     * Starts the client login scene.
     * 
     * @param primaryStage the main application window.
     * @throws Exception if the FXML file cannot be loaded.
     */
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/gui/ClientGUILogin.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
		primaryStage.setTitle("Login Screen");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

    /**
     * Handles the login button click event.
     * Verifies user credentials based on the selected user type (Subscriber or Librarian).
     * On successful login, navigates to the appropriate home page.
     * 
     * @param event the action event triggered by the login button.
     * @throws IOException if there is an issue with loading the next scene.
     * @throws InterruptedException if the thread is interrupted during execution.
     */
    public void getEnterBtn(ActionEvent event) throws IOException, InterruptedException {
        FXMLLoader loader = new FXMLLoader();
        Alert alert = new Alert(Alert.AlertType.WARNING);
        email = userName.getText();
        passwordString = password.getText();
        
        if (email.trim().isEmpty() || email.trim().isEmpty()) {
        	 alert.setTitle("Missing Field");
             alert.setContentText("Please enter email and password");
             alert.showAndWait();
        } else {
        	if(user.equals("Sub")) {
        		ClientGUIConnectionController.chat.acceptLogin("searchSub", email,passwordString);
        		if (ChatClient.sub1 == null) {
        			alert.setTitle("Missing Field");
                    alert.setContentText("The email or password do not match!");
                    alert.showAndWait();
                }else {
                    ((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary window
                    Stage primaryStage = new Stage();
                    Pane root = loader.load(getClass().getResource("/gui/ClientGUIHomePageController.fxml").openStream());

                    Scene scene = new Scene(root);
                    scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
                    primaryStage.setTitle("Client Second GUI");

                    primaryStage.setScene(scene);
                    primaryStage.show();
                }
        	}
        	else if(user.equals("Lib")) {
        		ClientGUIConnectionController.chat.acceptLogin("searchLib", email, passwordString);
        		if (ChatClient.lib == null) {
        			alert.setTitle("Missing Field");
                    alert.setContentText("The email or password do not match!");
                    alert.showAndWait();
                }else {
                    ((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary window
                    Stage primaryStage = new Stage();
                    Pane root = loader.load(getClass().getResource("/gui/LibrarianGUIHomePageController.fxml").openStream());

                    Scene scene = new Scene(root);
                    scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
                    primaryStage.setTitle("Librarian HomePage");

                    primaryStage.setScene(scene);
                    primaryStage.show();
                }
        	}
        }
    }
    /**
     * Sets the user role to Subscriber when the corresponding radio button is selected.
     * 
     * @param event the action event triggered by the radio button.
     */
    public void getRadioSubBtn(ActionEvent event) {
    	radio_lib.setSelected(false);
    	user = "Sub";
    }
    
    /**
     * Sets the user role to Librarian when the corresponding radio button is selected.
     * 
     * @param event the action event triggered by the radio button.
     */
    public void getRadioLibBtn(ActionEvent event) {
    	radio_sub.setSelected(false);
    	user = "Lib";
    }
    
    /**
     * Handles the search button click event. Navigates to the search book scene.
     * 
     * @param event the action event triggered by the search button.
     * @throws IOException if there is an issue with loading the next scene.
     */
    public void getSearchBtn(ActionEvent event) throws IOException {
    	// Hiding primary window
	    ((Node) event.getSource()).getScene().getWindow().hide();
	    
	    // Loading FXML and setting up the new stage
	    FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/SearchBookGUIController.fxml"));
	    Parent root = loader.load();
	    
	    Scene scene = new Scene(root);
	    scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
	    
	    Stage primaryStage = new Stage();
	    primaryStage.setTitle("Search");
	    primaryStage.setScene(scene);
	    primaryStage.show();
    }
    
    /**
     * Handles the exit button click event. Exits the application.
     * 
     * @param event the action event triggered by the exit button.
     */
	public void getExitBtn(ActionEvent event) {
		System.out.println("exit");
		System.exit(0);
	}
	

}