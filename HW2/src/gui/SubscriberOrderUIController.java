package gui;

import java.io.IOException;

import client.ChatClient;
import client.ClientUI;
import client.OrderUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SubscriberOrderUIController {

	@FXML
	private Label title = null;

	@FXML
	private TextField bookName = null;

	@FXML
	private DialogPane errorMsg = null;

	@FXML
	private Button sendOrderBtn = null;

	@FXML
	private Button returnBtn = null;

	@FXML
	private Button exitBtn = null;

	private String bookNameGot;
	
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/gui/SubscriberOrderUI.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/SubscriberOrderUI.css").toExternalForm());
		primaryStage.setTitle("Orders");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public void getSendBtn(ActionEvent event) throws IOException {
		String subID = ClientGUILandController.id;
		bookNameGot = bookName.getText();
		if (bookNameGot.isEmpty()) {
			errorMsg.setContentText("Oops! 😞 You must enter a book name.");
			return; // exit the method
		}
		// check if subscriber's status is frozen
		OrderUI.chat.acceptFromOrderController(5, subID, ""); //OrderUI needs to be changed to ClientUI !!!!!!!!!!!!!!!!
		if (ChatClient.isFrozen == true) {
			errorMsg.setContentText("Uh-oh! 😬 Your account is FROZEN!");
			return; // exit the method
		}
		OrderUI.chat.acceptFromOrderController(6, "", bookNameGot);
		if (ChatClient.isExist==false) { //which means there is not a book like this in our library
			errorMsg.setContentText("Oops! :( We dont have this book in our library.");
			return;
		}
		if (ChatClient.isAvailable == true) { // which means there is an available copy of the book -> cant order
			errorMsg.setContentText("Sorry! 📚 An available copy of this book already exists in the library.");
			return; // exit the method
		}
		//add column in the Orders table in the DB and in the activityhistory:
		OrderUI.chat.acceptFromOrderController(7, subID, bookNameGot);
		//check if the number of copys of the book already been ordered, if yes -> cant execute order
		//if no -> add the order to the 'order' table and into the 'activityhistory' table
		if (ChatClient.isCan==false) {
			errorMsg.setContentText("Whoops! 😅 The number of orders and copies are equal, so you can't place another order.");
			return;
		}
		errorMsg.setContentText("Awesome! 🎉 You're all set! Your order has been successfully placed!");				
	}
	public void getExitBtn(ActionEvent event) throws IOException {
		System.exit(0);
	}
}
