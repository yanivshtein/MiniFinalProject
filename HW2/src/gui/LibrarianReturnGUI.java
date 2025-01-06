package gui;


import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

import client.ChatClient;
import client.ClientUI;
import client.LibrarianUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

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
	
	@FXML
	private Label alertMsg = null;
	

	
	
	
	
	
	public void sendButton(ActionEvent event) {		// method that sends information to the controller to return the book to the library
		
		
	}
	
	
	public void checkBttn(ActionEvent event) {		// method that get information from the data the controller to return the book to the library
		
		String actionDate = null;
		String Deadline = null;
		String BorrowerId=subscriberId.getText();
		String BookName=bookName.getText();
		int index =0;
		LibrarianUI.chat.borrowBook_accept("EXIST", BorrowerId, BookName);
		
		if (ChatClient.bool==false) {
			alertMsg.setText("The Borrow does not exist!");
			return;
		}
		LibrarianUI.chat.borrowBook_accept("SELECT DATE",BorrowerId,BookName);
		
		for (String date : ChatClient.ActionDateAndDeadline) {
			if(index ==0)
				actionDate = date;
			else if(index ==1)
				Deadline = date;
			
			index++;
		}
		
		bookArriveDate.setText(actionDate);
		deadline.setText(Deadline);
	}
	
	
	public void exitButton(ActionEvent event) {
		System.out.println("Exit return window");
		System.exit(0);
	}
}
