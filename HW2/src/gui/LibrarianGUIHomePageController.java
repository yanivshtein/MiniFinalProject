package gui;
import java.io.IOException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import client.ChatClient;
import client.ClientUI;
import common.Subscriber1;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;

/**
 * Controller class for the Librarian Home Page GUI.
 * Handles the actions and navigation between different librarian functionalities
 * such as borrowing a book, adding a subscriber, viewing reports, and logging out.
 */
public class LibrarianGUIHomePageController {
	
	public static String BringLibName;
	
	@FXML
	private Label title = null;
	
	@FXML
	private Button addSub = null;
	
	@FXML
	private Button borrowBook = null;
	
	@FXML
	private Button returnBook = null;
	
	@FXML
	private Button GetReports = null;
	
	@FXML
	private Button searchBook = null;
	
	@FXML
	private Button readerDetails = null;
	
	@FXML
	private Button LogOut = null;
	
	@FXML
	private Button exit = null;
	@FXML
	private ImageView picSunOrMoon = null;
	
	@FXML
	private Button  messagesBtn = null;
	
    /**
     * Initializes the librarian's home page with the librarian's name and
     * sets the appropriate background image based on the current time of day.
     */

	@FXML
	private void initialize() {
	    // Set librarian name
	    title.setText("Hello, " + ChatClient.lib.getLibrarian_name());
	    
	    // Get the current time
	    LocalDateTime now = LocalDateTime.now();
	    
	    // Get the current hour
	    int currentHour = now.getHour();
	    
	    // Optionally set a default value for librarian's name (if required)
	    BringLibName = ChatClient.lib.getLibrarian_name();

	 
	        // If it's between 6 AM and 6 PM, set the sun image, else set the moon image
	        if (currentHour >= 6 && currentHour < 18) {
	            // Morning / Afternoon - Sun Image
	        	picSunOrMoon.setImage(new Image("/resources/SunPic.png"));
	        } else {
	            // Evening / Night - Moon Image
	        	picSunOrMoon.setImage(new Image("/resources/MoonPic.png"));
	        }
    }
	

    /**
     * Handles the action when the exit button is clicked. 
     * Exits the client application.
     * 
     * @param event The ActionEvent triggered by the exit button.
     * @throws IOException If there is an error during the shutdown.
     */	
	public void getExitBtn(ActionEvent event) throws IOException {
		System.out.println("Exit client");
		System.exit(0);
	}
	
    /**
     * Starts a new stage to display the Librarian Home Page GUI.
     * 
     * @param primaryStage The primary stage to display the GUI.
     * @throws Exception If there is an error loading the FXML file.
     */
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/gui/LibrarianGUIHomePageController.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
        primaryStage.setTitle("Librarian watch and update GUI");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /**
     * Handles the action when the logout button is clicked. 
     * Logs out the librarian and redirects to the login page.
     * 
     * @param event The ActionEvent triggered by the logout button.
     * @throws IOException If there is an error during the logout process.
     */
	public void getLogOutBtn(ActionEvent event) throws IOException{
		ChatClient.lib = null;
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
     * Opens the borrowing book page when the borrow book button is clicked.
     * 
     * @param event The ActionEvent triggered by the borrow book button.
     * @throws IOException If there is an error loading the borrowing book page.
     */
	public void BorrowingBookBtn(ActionEvent event) throws IOException{
		FXMLLoader loader = new FXMLLoader();
		((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary window
        Stage primaryStage = new Stage();
        Pane root = loader.load(getClass().getResource("/gui/BorrowBookGUIController.fxml").openStream());

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
        primaryStage.setTitle("Borrow a book");

        primaryStage.setScene(scene);
        primaryStage.show();
	}
	
    /**
     * Opens the search book page when the search book button is clicked.
     * 
     * @param event The ActionEvent triggered by the search book button.
     * @throws IOException If there is an error loading the search book page.
     */
	public void SearchBookBtn(ActionEvent event) throws IOException{
		FXMLLoader loader = new FXMLLoader();
		((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary window
        Stage primaryStage = new Stage();
        Pane root = loader.load(getClass().getResource("/gui/SearchBookGUIController.fxml").openStream());

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
        primaryStage.setTitle("Search a book");

        primaryStage.setScene(scene);
        primaryStage.show();
	}
	
	
	
    /**
     * Opens the librarian watch page when the watch button is clicked.
     * 
     * @param event The ActionEvent triggered by the watch button.
     * @throws IOException If there is an error loading the librarian watch page.
     */
	public void getLibrarianWatch(ActionEvent event) throws IOException {
		 try {
		        ((Node) event.getSource()).getScene().getWindow().hide(); // Hide the current window

		        // Load the LibrarianWatchAndUpdateGUI FXML
		        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/LibrarianWatchGUI.fxml"));
		        Parent root = loader.load();

		        // Set up the new stage
		        Stage primaryStage = new Stage();
		        Scene scene = new Scene(root);

		        // Apply stylesheets
		        scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());

		        // Configure the stage
		        primaryStage.setTitle("Librarian Watch  GUI");
		        primaryStage.setScene(scene);

		        // Show the stage
		        primaryStage.show();
		    } catch (IOException e) {
		        System.err.println("Error: Could not load FXML file");
		        e.printStackTrace();
		        // Optionally, display an error message to the user here
		    }
	}
	
    /**
     * Opens the librarian update page when the update button is clicked.
     * 
     * @param event The ActionEvent triggered by the update button.
     * @throws IOException If there is an error loading the librarian update page.
     */
	public void getLibrarianUpdate(ActionEvent event) throws IOException {
		 try {
		        ((Node) event.getSource()).getScene().getWindow().hide(); // Hide the current window

		        // Load the LibrarianWatchAndUpdateGUI FXML
		        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/LibrarianUpdateGUI.fxml"));
		        Parent root = loader.load();

		        // Set up the new stage
		        Stage primaryStage = new Stage();
		        Scene scene = new Scene(root);

		        // Apply stylesheets
		        scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());

		        // Configure the stage
		        primaryStage.setTitle("Librarian Update GUI");
		        primaryStage.setScene(scene);

		        // Show the stage
		        primaryStage.show();
		    } catch (IOException e) {
		        System.err.println("Error: Could not load FXML file");
		        e.printStackTrace();
		        // Optionally, display an error message to the user here
		    }
	}
	
	
	
    /**
     * Opens the reports page when the reports button is clicked.
     * 
     * @param event The ActionEvent triggered by the reports button.
     * @throws IOException If there is an error loading the reports page.
     */
	
	public void getReportsGUIBtn(ActionEvent event) throws IOException {
	    try {
	        // Hide the current window
	        ((Node) event.getSource()).getScene().getWindow().hide();

	        // Load the ReportsGUI FXML
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ReportsGUI.fxml"));
	        Parent root = loader.load();

	        // Set up the new stage
	        Stage primaryStage = new Stage();
	        Scene scene = new Scene(root);

	        // Apply stylesheets
	        scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());

	        // Configure the stage
	        primaryStage.setTitle("Reports GUI");
	        primaryStage.setScene(scene);

	        // Show the stage
	        primaryStage.show();
	    } catch (IOException e) {
	        System.err.println("Error: Could not load ReportsGUI FXML file");
	        e.printStackTrace();
	        // Optionally, display an error message to the user
	    }
	}

    /**
     * Opens the add subscriber page when the add subscriber button is clicked.
     * 
     * @param event The ActionEvent triggered by the add subscriber button.
     * @throws IOException If there is an error loading the add subscriber page.
     */

	public void getAddSubBtn(ActionEvent event) throws IOException{
		FXMLLoader loader = new FXMLLoader();
		((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary window
        Stage primaryStage = new Stage();
        Pane root = loader.load(getClass().getResource("/gui/AddSubscriberGUIController.fxml").openStream());

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
        primaryStage.setTitle("Add new subscriber");

        primaryStage.setScene(scene);
        primaryStage.show();
	}
	
    /**
     * Opens the librarian return book page when the return book button is clicked.
     * 
     * @param event The ActionEvent triggered by the return book button.
     * @throws IOException If there is an error loading the return book page.
     */
	public void returnBookbttn(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary window
        Stage primaryStage = new Stage();
        Pane root = loader.load(getClass().getResource("/gui/LibrarianReturnGUI.fxml").openStream());

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
        primaryStage.setTitle("Librarian Return GUI");

        primaryStage.setScene(scene);
        primaryStage.show();
	}
	
    /**
     * Opens the orders page when the orders button is clicked.
     * 
     * @param event The ActionEvent triggered by the orders button.
     * @throws IOException If there is an error loading the orders page.
     */
	public void getOrders (ActionEvent event) throws IOException {
		// Hiding primary window
	    ((Node) event.getSource()).getScene().getWindow().hide();
	    
	    // Loading FXML and setting up the new stage
	    FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/OrdersToTake.fxml"));
	    Parent root = loader.load();
	    
	    Scene scene = new Scene(root);
	    scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
	    
	    Stage primaryStage = new Stage();
	    primaryStage.setTitle("Waiting Orders");
	    primaryStage.setScene(scene);
	    primaryStage.show();
	}
	

    /**
     * Opens the messages page when the messages button is clicked.
     * 
     * @param event The ActionEvent triggered by the messages button.
     * @throws IOException If there is an error loading the messages page.
     */
	public void getMessages(ActionEvent event) throws IOException {
		// Hiding primary window
	    ((Node) event.getSource()).getScene().getWindow().hide();
	    
	    // Loading FXML and setting up the new stage
	    FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/LibrarianMessages.fxml"));
	    Parent root = loader.load();
	    
	    Scene scene = new Scene(root);
	    scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
	    
	    Stage primaryStage = new Stage();
	    primaryStage.setTitle("Messages");
	    primaryStage.setScene(scene);
	    primaryStage.show();
	}
	
}