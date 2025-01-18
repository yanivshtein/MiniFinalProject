package gui;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import client.ChatClient;
import client.ClientConsole;
import client.ClientUI;
import common.Librarian;
import common.Subscriber1;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import server.EchoServer;
import server.ServerUI;

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

	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/gui/ClientGUILogin.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
		primaryStage.setTitle("Login Screen");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	// This method is called on button click
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
                	System.out.println("Subscriber ID Found");
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
                	System.out.println("Librarian ID Found");
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

    public void getRadioSubBtn(ActionEvent event) {
    	radio_lib.setSelected(false);
    	user = "Sub";
    }
    
    
    public void getRadioLibBtn(ActionEvent event) {
    	radio_sub.setSelected(false);
    	user = "Lib";
    }
    
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
    
	public void getExitBtn(ActionEvent event) {
		System.out.println("exit");
		System.exit(0);
	}
	

}