package gui;



import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import client.ChatClient;
import client.ClientUI;
import client.LibrarianUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
	Label alertMsg = null;
	
	@FXML
	private Label successMsg = null;
	
	private Alert alertMessege = new Alert(AlertType.NONE);

	
	
	
	
	
	public void sendButton(ActionEvent event) {		// method that sends information to the controller to return the book to the library
		
		if(bookArriveDate ==null && deadline == null) {
			alertMessege.setContentText("Error need to check if exist borrow first");	
		 	alertMessege.setAlertType(AlertType.ERROR);
			return;
		}
		if(successMsg!=null) {
			successMsg=null;
		}
		
		String BorrowerId=subscriberId.getText();
		String BookName=bookName.getText();
		
		// get current time in a format of yyyy-MM-dd
		LocalDateTime localDate=LocalDateTime.now();
		DateTimeFormatter dfm= DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String formattedCurrentDate = localDate.format(dfm);
		String deadlineString= deadline.getText();
		
		int currentDateAsInt=0;
		int deadlineDateAsInt=0;
		
		formattedCurrentDate=formattedCurrentDate.replace("-", "");
		deadlineString= deadlineString.replace("-","");
		System.out.println("formatted and replace local date is:"+formattedCurrentDate);
		System.out.println("formatted and replace deadline is:"+deadlineString);
		try {
			currentDateAsInt = Integer.parseInt(formattedCurrentDate);
			deadlineDateAsInt = Integer.parseInt(deadlineString);
		} catch (NumberFormatException e) {
//			alertMessege.setContentText("Error need to check if exist borrow first");	
//		 	alertMessege.setAlertType(AlertType.ERROR);
			e.getStackTrace();
		}
		
		
		if (currentDateAsInt<deadlineDateAsInt) {
			
			LibrarianUI.chat.returnBook_accept("INSERT", BorrowerId, BookName,false,false);
			
			
		}
		else if(currentDateAsInt-deadlineDateAsInt<7) {
			LibrarianUI.chat.returnBook_accept("INSERT", BorrowerId, BookName,true,false);

			
		}
		else if(currentDateAsInt-deadlineDateAsInt>=7) {
			LibrarianUI.chat.returnBook_accept("INSERT", BorrowerId, BookName,true,true);

			
		}
		if (ChatClient.bool==false) {
			alertMessege.setContentText("Error need to check if exist borrow first");	
		 	alertMessege.setAlertType(AlertType.ERROR);
			return;
			
		}
		else {
			successMsg.setText("Return operation successfully finished!");
		}
	}
	
	
	public void checkBttn(ActionEvent event) {		// method that get information from the data the controller to return the book to the library
		
		
		String actionDate = null;
		String Deadline = null;
		String BorrowerId=subscriberId.getText();
		String BookName=bookName.getText();
		
		if(BorrowerId ==null || BookName == null) {
			alertMessege.setContentText("Error you didn't write both Subscriber's ID and Book's name");	
		 	alertMessege.setAlertType(AlertType.ERROR);
			return;
		}
		BorrowerId=BorrowerId.trim();
		BookName=BookName.trim();
		int index =0;
		
		if(alertMsg!=null)		// if the text in Label is currently visible 
			alertMsg=null;
		
		// check in the database if exist a borrow with the same borrower ID and book name
		LibrarianUI.chat.returnBook_accept("EXIST", BorrowerId, BookName,false,false);
		
		// if there isn't any row that match, then show in label.
//		if (ChatClient.bool==false) {
//			alertMsg= new Label();
//			alertMsg.setText("The Borrow does not exist!");
//			return;
//		}
		
		// if there is a match then select the borrow date and deadline.
		LibrarianUI.chat.returnBook_accept("SELECT DATE",BorrowerId,BookName,false,false);
		
		for (String date : ChatClient.ActionDateAndDeadline) {	// get action date and deadline
			if(index ==0)
				actionDate = date;
			else if(index ==1)
				Deadline = date;
			
			index++;
		}
		// show the borrow date and deadline in the labels
		bookArriveDate.setText(actionDate);		// set labels to show them in GUI
		deadline.setText(Deadline);
	}
	
	
	public void exitButton(ActionEvent event) {
		System.out.println("Exit return window");
		System.exit(0);
	}
}
