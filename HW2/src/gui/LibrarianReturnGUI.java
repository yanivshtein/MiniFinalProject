package gui;

import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

import client.ChatClient;
import client.ClientUI;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class LibrarianReturnGUI {

	
	@FXML
	TextField subscriberId=null;
	
	@FXML
	TextField bookName=null;
	
	@FXML
	Label bookArriveDate=null;
	
	@FXML
	Label deadline=null;
	
	@FXML
	Button sendButton=null;
	
	@FXML
	Button checkButton=null;
	
	@FXML
	Button exitButton=null;
	
//	@FXML
//	Button send;
	
	SimpleDateFormat time= new SimpleDateFormat("dd-MM-yyyy");
	
	
	
	public void sendButton(ActionEvent event) {		// method that sends information to the controller to return the book to the library
		
		
	}
	
	
	public void checkButton(ActionEvent event) {		// method that get information from the data the controller to return the book to the library
		
		//ClientUI.chat.accept("select", subscriberId.getText(), j, null);
	}
	
	
	public void exitButton(ActionEvent event) {
		System.out.println("Exit return window");
		System.exit(0);
	}
}
