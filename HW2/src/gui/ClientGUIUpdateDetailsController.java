package gui;

import java.io.IOException;
import java.util.ArrayList;

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


public class ClientGUIUpdateDetailsController {

	@FXML
	private Button update = null;
	@FXML
	private Button exitBtn = null;

	@FXML
	private Button Return = null;
	

	@FXML
	private TextField Phone = null;


	@FXML
	private TextField Email = null;

	
	@FXML
	private DialogPane afterUpdate = null;
	
	@FXML
	private Label IdName = null;
	
	@FXML
	private Label subName = null;
	
	
	
	@FXML
	private void initialize() {
		ClientGUIConnectionController.chat.accept("select", ClientGUILoginController.id, "", ""); //retrieve Subscriber info
		Subscriber1 sub = ChatClient.s1; 
		IdName.setText(ClientGUILoginController.id);
		subName.setText(sub.getSubscriber_name());
		Phone.setText(sub.getSubscriber_phone_number());
		Email.setText(sub.getSubscriber_email());
	} 
	
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/gui/ClientGUIHomePage.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/ClientGUIHomePage.css").toExternalForm());
		primaryStage.setTitle("Client GUI");
		primaryStage.setScene(scene);
		primaryStage.show();
	}


	public void getUpdatedBtn(ActionEvent event) throws IOException{
		afterUpdate.setContentText("Updated");
		ClientGUIConnectionController.chat.accept("update", ClientGUILoginController.id, Phone.getText(), Email.getText());
	}
	
	public void getExitBtn(ActionEvent event) throws IOException {
		System.out.println("Exit client");
		System.exit(0);
	}
	public void getReturnBtn(ActionEvent event) throws IOException{
		FXMLLoader loader = new FXMLLoader();
		((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary window
        Stage primaryStage = new Stage();
        Pane root = loader.load(getClass().getResource("/gui/ClientGUIHomePage.fxml").openStream());

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/ClientGUIHomePage.css").toExternalForm());
        primaryStage.setTitle("Home page");

        primaryStage.setScene(scene);
        primaryStage.show();
	}
}