package gui;

import java.io.IOException;

import java.time.LocalDateTime;

import client.ChatClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Controller class for the Client Home Page GUI.
 * Handles user interactions such as navigating to different pages, updating the home page, and logging out.
 */
public class ClientGUIHomePageController {

	
	@FXML
	private Button exit = null;

	@FXML
	private Button send = null;
	
	@FXML
	private Label title = null;
	
	@FXML
	private Button LogOut = null;
	
	@FXML
	private ImageView picSunOrMoon = null;
	
    /**
     * Initializes the home page by setting the title with the subscriber's name
     * and choosing an image (sun or moon) based on the time of day.
     */
	@FXML
	private void initialize() {
		title.setText("Hello, " + ChatClient.sub1.getSubscriber_name());
		
		// If it's between 6 AM and 6 PM, set the sun image, else set the moon image
        LocalDateTime now = LocalDateTime.now();
	    
	    // Get the current hour
	    int currentHour = now.getHour();
		
		
		if (currentHour >= 6 && currentHour < 18) {
            // Morning / Afternoon - Sun Image
        	picSunOrMoon.setImage(new Image("/resources/SunPic.png"));
        } else {
            // Evening / Night - Moon Image
        	picSunOrMoon.setImage(new Image("/resources/MoonPic.png"));
        }
	}
	
    /**
     * Starts the Client Home Page GUI by loading the corresponding FXML file.
     *
     * @param primaryStage The primary stage for displaying the home page UI.
     * @throws Exception If there is an error during the initialization.
     */
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/gui/ClientGUIHomePage.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
		primaryStage.setTitle("Login Screen");
		primaryStage.setScene(scene);
		
		primaryStage.show();
	}
	
    /**
     * Navigates to the Update Details page when the "Update Details" button is clicked.
     *
     * @param event The ActionEvent triggered by the button click.
     * @throws IOException If there is an error loading the next FXML file.
     */
	public void getUpdateDetails(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary window
        Stage primaryStage = new Stage();
        Pane root = loader.load(getClass().getResource("/gui/ClientGUIUpdateDetails.fxml").openStream());

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
        primaryStage.setTitle("Client Second GUI");

        primaryStage.setScene(scene);
        primaryStage.show();
		
	}
	
    /**
     * Navigates to the View History page when the "View History" button is clicked.
     *
     * @param event The ActionEvent triggered by the button click.
     * @throws IOException If there is an error loading the next FXML file.
     */
	public void getViewHistory(ActionEvent event) throws IOException {
	    // Hiding primary window
	    ((Node) event.getSource()).getScene().getWindow().hide();
	    
	    // Loading FXML and setting up the new stage
	    FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ViewHistoryGUI.fxml"));
	    Parent root = loader.load();
	    
	    Scene scene = new Scene(root);
	    scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
	    
	    Stage primaryStage = new Stage();
	    primaryStage.setTitle("View History");
	    primaryStage.setScene(scene);
	    primaryStage.show();
	}
	
    /**
     * Navigates to the Search Book page when the "Search Book" button is clicked.
     *
     * @param event The ActionEvent triggered by the button click.
     * @throws IOException If there is an error loading the next FXML file.
     */
	public void getSearch (ActionEvent event) throws IOException {
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
     * Navigates to the Order page when the "Order" button is clicked.
     *
     * @param event The ActionEvent triggered by the button click.
     * @throws IOException If there is an error loading the next FXML file.
     */
	public void getOrder (ActionEvent event) throws IOException {
		// Hiding primary window
	    ((Node) event.getSource()).getScene().getWindow().hide();
	    
	    // Loading FXML and setting up the new stage
	    FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/SubscriberOrderUI.fxml"));
	    Parent root = loader.load();
	    
	    Scene scene = new Scene(root);
	    scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
	    
	    Stage primaryStage = new Stage();
	    primaryStage.setTitle("Order");
	    primaryStage.setScene(scene);
	    primaryStage.show();
	}
	
    /**
     * Navigates to the Extension page when the "Extension" button is clicked.
     *
     * @param event The ActionEvent triggered by the button click.
     * @throws IOException If there is an error loading the next FXML file.
     */
	public void getExtension (ActionEvent event) throws IOException {
		// Hiding primary window
	    ((Node) event.getSource()).getScene().getWindow().hide();
	    
	    // Loading FXML and setting up the new stage
	    FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/SubscriberExtension.fxml"));
	    Parent root = loader.load();
	    
	    Scene scene = new Scene(root);
	    scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
	    
	    Stage primaryStage = new Stage();
	    primaryStage.setTitle("Extension");
	    primaryStage.setScene(scene);
	    primaryStage.show();
	}
	
    /**
     * Navigates to the Subscriber Messages page when the "My Messages" button is clicked.
     *
     * @param event The ActionEvent triggered by the button click.
     * @throws IOException If there is an error loading the next FXML file.
     */
	public void getSubMessages (ActionEvent event) throws IOException {
		// Hiding primary window
	    ((Node) event.getSource()).getScene().getWindow().hide();
	    
	    // Loading FXML and setting up the new stage
	    FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/SubscriberMessages.fxml"));
	    Parent root = loader.load();
	    
	    Scene scene = new Scene(root);
	    scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
	    
	    Stage primaryStage = new Stage();
	    primaryStage.setTitle("My Messages");
	    primaryStage.setScene(scene);
	    primaryStage.show();
	}		


    /**
     * Logs the user out and navigates back to the login screen.
     *
     * @param event The ActionEvent triggered by the button click.
     * @throws IOException If there is an error loading the next FXML file.
     */
	public void getLogOutBtn(ActionEvent event) throws IOException{
		ChatClient.sub1 = null;
		FXMLLoader loader = new FXMLLoader();
		((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary window
        Stage primaryStage = new Stage();
        Pane root = loader.load(getClass().getResource("/gui/ClientGUILogin.fxml").openStream());

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
        primaryStage.setTitle("Login");

        primaryStage.setScene(scene);
        primaryStage.show();
	}
	
    /**
     * Exits the application when the "Exit" button is clicked.
     *
     * @param event The ActionEvent triggered by the button click.
     */
	public void getExitBtn(ActionEvent event) {
		System.out.println("exit");
		System.exit(0);
	}
}
