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
import javafx.scene.control.Alert;
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
	private Label afterUpdate = null;
	
	@FXML
	private Label IdName = null;
	
	@FXML
	private Label subName = null;
	String subId;
	
	
	
	@FXML
	private void initialize() {
	 //retrieve Subscriber info
		subId=Integer.toString(ChatClient.sub1.getSubscriber_id());
		ClientGUIConnectionController.chat.accept("select",subId, "", "");
		IdName.setText(Integer.toString(ChatClient.s1.getSubscriber_id()));
		subName.setText(ChatClient.s1.getSubscriber_name());
		Phone.setText(ChatClient.s1.getSubscriber_phone_number());
		Email.setText(ChatClient.s1.getSubscriber_email());
	} 
	
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/gui/ClientGUIHomePage.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
		primaryStage.setTitle("Client GUI");
		primaryStage.setScene(scene);
		primaryStage.show();
	}


	public void getUpdatedBtn(ActionEvent event) throws IOException{
		String email =Email.getText();
		String phone =Phone.getText();
		   Alert alert = new Alert(Alert.AlertType.WARNING);
		if(email.isEmpty()|| phone.isEmpty()) {
            alert.setTitle("Missing Fields");
            alert.setHeaderText("Some fields are missing");
            alert.setContentText("Please fill in the following fields");
            alert.showAndWait();
            return;	
		}
		ClientGUIConnectionController.chat.accept("update",subId, Phone.getText(), Email.getText());
		afterUpdate.setText("Updated");
	}
	
	public void getExitBtn(ActionEvent event) throws IOException {
		System.out.println("Exit client");
		System.exit(0);
	}
	public void getReturnBtn(ActionEvent event) throws IOException{
		FXMLLoader loader = new FXMLLoader();
		
		((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary window
        Stage primaryStage = new Stage();
        Pane root = loader.load(getClass().getResource("/gui/ClientGUIHomePageController.fxml").openStream());

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
        primaryStage.setTitle("Home page");

        primaryStage.setScene(scene);
        primaryStage.show();
	}
}