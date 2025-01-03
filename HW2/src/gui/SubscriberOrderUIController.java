package gui;

import java.io.IOException;

import client.ChatClient;
import client.ClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

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

	public void getSendBtn(ActionEvent event) throws IOException {
		bookNameGot = bookName.getText();
		// check if subscriber's status is frozen
		ClientGUIConnectionController.chat.acceptFromOrderController(5, ClientGUILoginController.id, "");
		if (ChatClient.isFrozen == true) {
			errorMsg.setContentText("Account is FROZEN!");
			return; // exit the method
		}
		ClientGUIConnectionController.chat.acceptFromOrderController(6, bookNameGot, "");
		if (ChatClient.isAvailable == true) { // which means there is an available copy of the book -> cant order
			errorMsg.setContentText("Available copy of this book exists in the library!");
			return; // exit the method
		}
		//send to reader's card the order
		//add column in the Orders table in the DB:
		ClientGUIConnectionController.chat.acceptFromOrderController(7, ClientGUILoginController.id, bookNameGot);
		//check if the number of copys of the book already been ordered, if yes -> cant execute order
		if (ChatClient.isCan==false) {
			errorMsg.setContentText("The number of orders and copys equals -> can't order");
			return;
		}
		errorMsg.setContentText("The order has been addded");

	}
}
