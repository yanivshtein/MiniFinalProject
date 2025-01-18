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
	

		
	public void getExitBtn(ActionEvent event) throws IOException {
		System.out.println("Exit client");
		System.exit(0);
	}
	
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/gui/LibrarianGUIHomePageController.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
        primaryStage.setTitle("Librarian watch and update GUI");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
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