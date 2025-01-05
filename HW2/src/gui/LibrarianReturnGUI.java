package gui;


import javafx.event.ActionEvent;

import client.ChatClient;
import client.ClientUI;
import client.LibrarianUI;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LibrarianReturnGUI {

	
	@FXML
	private TextField borrowerId=null;
	
	@FXML
	private TextField bookName=null;
	
	@FXML
	private Label bookArriveDate=null;
	
	@FXML
	private Label deadline=null;
	
	@FXML
	private Button sendButton=null;
	
	@FXML
	private Button checkButton=null;
	
	@FXML
	private Button exitButton=null;
	
	
	
	@FXML
	private DialogPane alertMsg = null;
	

	
	
	
	
//	public void sendButton(ActionEvent event) {		// method that sends information to the controller to return the book to the library
//		
//		
//	}
	
	
	public void checkBttn(ActionEvent event) {		// method that get information from the data the controller to return the book to the library
		String id,bookN;
		if (borrowerId.getText() == null || borrowerId.getText().isEmpty()) {
	        alertMsg.setContentText("Borrower ID cannot be empty!");
	        return;
	    }
		
		if(bookName.getText()==null || bookName.getText().isEmpty() ) {
			alertMsg.setContentText("Book Name cannot be empty!");
        	return;
		}
		id=borrowerId.getText();
		bookN=bookName.getText();
		System.out.println("borrowerId: "+id);
		System.out.println("book name: "+bookN);
		
		LibrarianUI.chat.borrower_accept("EXIST",borrowerId.getText() , bookName.getText());
		if (ChatClient.bool==false) {
			alertMsg.setContentText("The ID does not exist!");
		}
		else {
			LibrarianUI.chat.borrower_accept("SELECT DATE",borrowerId.getText() , bookName.getText());
			int index=0;
			for(String s: ChatClient.dateTimeSet) {
				if (index==0)
					bookArriveDate.setText(s);
				if(index==1)
					deadline.setText(s);
				index++;
			}
		}
		
	}
	
	//@FXML
    public void exitBttn(ActionEvent event) {
        System.out.println("Exit return window");
        System.exit(0);
    }
}
