package gui;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import client.ChatClient;
import client.ClientConsole;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
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
	private RadioButton radio_sub = null;

	@FXML
	private RadioButton radio_lib = null;
	
	
	@FXML
	private TextField userName = null;
	
	@FXML
	private TextField password = null;
	
	
	
	@FXML
	private DialogPane alertMsg = null;
	
	private String user = "";

	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/gui/ClientGUILogin.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/ClientGUILogin.css").toExternalForm());
		primaryStage.setTitle("Login Screen");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	// This method is called on button click
    public void getEnterBtn(ActionEvent event) throws IOException, InterruptedException {
        FXMLLoader loader = new FXMLLoader();

        email = userName.getText();
        passwordString = password.getText();
        
        if (email.trim().isEmpty() || email.trim().isEmpty()) {

            System.out.println("You must enter an email and password");
        } else {
        	if(user.equals("Sub")) {
        		ClientGUIConnectionController.chat.acceptLogin("searchSub", email,passwordString);
        		if (ChatClient.bool==false) {
                    alertMsg.setContentText("The ID does not exist!");
                }else {
                	System.out.println("Subscriber ID Found");
                    ((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary window
                    Stage primaryStage = new Stage();
                    Pane root = loader.load(getClass().getResource("/gui/ClientGUIHomePage.fxml").openStream());

                    Scene scene = new Scene(root);
                    scene.getStylesheets().add(getClass().getResource("/gui/ClientGUIHomePage.css").toExternalForm());
                    primaryStage.setTitle("Client Second GUI");

                    primaryStage.setScene(scene);
                    primaryStage.show();
                }
        	}
        	else if(user.equals("Lib")) {
        		ClientGUIConnectionController.chat.acceptLogin("searchLib", email, passwordString);
        		if (ChatClient.bool==false) {
                    alertMsg.setContentText("The ID does not exist!");
                }else {
                	System.out.println("Librarian ID Found");
                    ((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary window
                    Stage primaryStage = new Stage();
                    Pane root = loader.load(getClass().getResource("/gui/ClientGUIHomePage.fxml").openStream());

                    Scene scene = new Scene(root);
                    scene.getStylesheets().add(getClass().getResource("/gui/ClientGUIHomePage.css").toExternalForm());
                    primaryStage.setTitle("Client Second GUI");

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
    
	public void getExitBtn(ActionEvent event) {
		System.out.println("exit");
		System.exit(0);
	}
	

}