package gui;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;

public class LibrarianGUIHomePageController {
	

	public static String BringLibName;
	
	@FXML
	private Label librarianName = null;
	
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
	private void initialize() {
		librarianName.setText(ChatClient.lib.getLibrarian_name());
		BringLibName = librarianName.getText();

	}
		
	public void getExitBtn(ActionEvent event) throws IOException {
		System.out.println("Exit client");
		System.exit(0);
	}
	
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/gui/LibrarianGUIHomePageController.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/LibrarianGUIHomePageController.css").toExternalForm());
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
        scene.getStylesheets().add(getClass().getResource("/gui/ClientGUILogin.css").toExternalForm());
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
        scene.getStylesheets().add(getClass().getResource("/gui/BorrowBookGUIController.css").toExternalForm());
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
        scene.getStylesheets().add(getClass().getResource("/gui/SearchBookGUIController.css").toExternalForm());
        primaryStage.setTitle("Search a book");

        primaryStage.setScene(scene);
        primaryStage.show();
	}
	
	
	
	
	public void getLibrarianWatchAndUpdateBtn(ActionEvent event) throws IOException {
	    try {
	        ((Node) event.getSource()).getScene().getWindow().hide(); // Hide the current window

	        // Load the LibrarianWatchAndUpdateGUI FXML
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/LibrarianWatchAndUpdateGUI.fxml"));
	        Parent root = loader.load();

	        // Set up the new stage
	        Stage primaryStage = new Stage();
	        Scene scene = new Scene(root);

	        // Apply stylesheets
	        scene.getStylesheets().add(getClass().getResource("/gui/LibrarianWatchAndUpdateGUI.css").toExternalForm());

	        // Configure the stage
	        primaryStage.setTitle("Librarian Watch and Update GUI");
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
	        scene.getStylesheets().add(getClass().getResource("/gui/ReportsGUI.css").toExternalForm());

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
        scene.getStylesheets().add(getClass().getResource("/gui/AddSubscriberGUIController.css").toExternalForm());
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
        scene.getStylesheets().add(getClass().getResource("/gui/LibrarianReturnGUI.css").toExternalForm());
        primaryStage.setTitle("Librarian Return GUI");

        primaryStage.setScene(scene);
        primaryStage.show();
	}
	
	
}