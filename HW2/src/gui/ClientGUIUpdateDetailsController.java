package gui;

import java.io.IOException;
import java.util.ArrayList;

import client.ChatClient;
import client.ClientUI;
import common.Subscriber1;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;


public class ClientGUIUpdateDetailsController {
	@FXML
	private Button show = null;
	@FXML
	private Button update = null;
	@FXML
	private Button exitBtn = null;
	@FXML
	private Button view = null;
	@FXML
	private TextField secondID = null;
	
	@FXML
	private TextField secondName = null;

	@FXML
	private TextField secondPhoneLeft = null;

	@FXML
	private TextField secondHistory = null;

	@FXML
	private TextField secondEmailLeft = null;

	@FXML
	private TextField secondPhoneRight = null;

	@FXML
	private TextField secondEmailRight = null;
	
	@FXML
	private DialogPane afterUpdate = null;
	
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/gui/ClientGUIHomePage.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/ClientGUIHomePage.css").toExternalForm());
		primaryStage.setTitle("Client GUI");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public void getSelectBtn(ActionEvent event) throws IOException{
		ClientGUIConnectionController.chat.accept("select", ClientGUILoginController.id, "", ""); //retrieve Subscriber info
		Subscriber1 sub = ChatClient.s1; 
		secondID.setText(String.valueOf(sub.getSubscriber_id())); //cast from int to string
		secondName.setText(sub.getSubscriber_name());
		secondHistory.setText(String.valueOf(sub.getDetailed_subscription_history()));
		secondPhoneLeft.setText(sub.getSubscriber_phone_number());
		secondEmailLeft.setText(sub.getSubscriber_email());
	}
	
	
	public void getUpdatedBtn(ActionEvent event) throws IOException{
		afterUpdate.setContentText("Update sent");
		ClientGUIConnectionController.chat.accept("update", ClientGUILoginController.id, secondPhoneRight.getText(), secondEmailRight.getText());
	}
	
	public void getExitBtn(ActionEvent event) throws IOException {
		System.out.println("Exit client");
		System.exit(0);
	}
}
