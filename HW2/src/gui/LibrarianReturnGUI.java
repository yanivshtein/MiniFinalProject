package gui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import client.ChatClient;
import client.ClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

public class LibrarianReturnGUI {

	
	@FXML
	private TextField subscriberId=null;
	
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
	private Label artMsg = null;
	
	@FXML
	private Label sendMsg = null;
	
	private Alert alertMessege = new Alert(AlertType.NONE);

	
	
	
	
	
	public void sendButton(ActionEvent event) {		// method that sends information to the controller to return the book to the library
		
		if(bookArriveDate ==null || deadline == null) {
			alertMessege.setContentText("Error need to check if exist borrow first");	
		 	alertMessege.setAlertType(AlertType.ERROR);
		 	alertMessege.show();
			return;
		}
		if(sendMsg!=null) {
			sendMsg.setText("");;
		}
		
		String BorrowerId=subscriberId.getText();
		String BookName=bookName.getText();
		
		// check if the book has already returned by the subscriber
		ClientGUIConnectionController.chat.returnBook_accept("CHECK_BOOK_RETURNED", BorrowerId, BookName, null, null, null);
		
		// if the book already returned then show message 
		if (ChatClient.bool==true) {
			//sendMsg.setText("Book has already returned!");
			showLabelTextForDuration(sendMsg, "Book has already returned!", 3000); // Show text for 3 seconds

			return;
		}
		// get current time in a format of yyyy-MM-dd
		LocalDate currentDate=LocalDate.now();
		DateTimeFormatter dateFormatter= DateTimeFormatter.ofPattern("yyyy-MM-dd");
//		String formattedCurrentDate = localDate.format(dateFormatter);
		String deadlineString= deadline.getText();
		LocalDate deadlineDate=LocalDate.parse(deadlineString, dateFormatter);
		Period difference = Period.between( deadlineDate,currentDate);
		
		System.out.println("formatted and replace local date is:"+currentDate);
		System.out.println("formatted and replace deadline is:"+deadlineString);
		
		try {
			
			long daysLate = currentDate.toEpochDay()- deadlineDate.toEpochDay();
		
		
		if (daysLate<=0) {
			
			ClientGUIConnectionController.chat.returnBook_accept("INSERT", BorrowerId, BookName,false,false,difference);
			
			
		}
		else if(daysLate<7) {
			
			ClientGUIConnectionController.chat.returnBook_accept("INSERT", BorrowerId, BookName,true,false,difference);

			
		}
		else if(daysLate>=7) {
			ClientGUIConnectionController.chat.returnBook_accept("INSERT", BorrowerId, BookName,true,true,difference);

			
		}
		if (ChatClient.bool==false) {
			alertMessege.setContentText("Error need to check if exist borrow first");	
		 	alertMessege.setAlertType(AlertType.ERROR);
		 	alertMessege.show();
			return;
			
		}
		
		else {
			showLabelTextForDuration(sendMsg, "Return operation successfully finished!", 3000); // Show text for 3 seconds
		}

		}catch (DateTimeParseException e) {
//			alertMessege.setContentText("Error need to check if exist borrow first");	
//		 	alertMessege.setAlertType(AlertType.ERROR);
			e.getStackTrace();
		}
	}
	
	public void showLabelTextForDuration(Label label, String text , int durationInMillis) {
		
		label.setText(text);
		
		Timeline timeline= new Timeline(new KeyFrame(Duration.millis(durationInMillis),event -> label.setText("")));
		timeline.setCycleCount(1);
		timeline.play();
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
		
		if(artMsg!=null)		// if the text in Label is currently visible 
			artMsg.setText("");
		
		// check in the database if exist a borrow with the same borrower ID and book name
		ClientGUIConnectionController.chat.returnBook_accept("EXIST", BorrowerId, BookName,false,false,null);
		
		// if there isn't any row that match, then show in label.
		if (ChatClient.bool==false) {
			
			bookArriveDate.setText("");		// set labels to show them in GUI
			deadline.setText("");
			artMsg.setText("The Borrow does not exist!");
			showLabelTextForDuration(sendMsg, "The Borrow does not exist!", 3000); // Show text for 3 seconds

			return;
		}
		
		// if there is a match then select the borrow date and deadline.
		ClientGUIConnectionController.chat.returnBook_accept("SELECT DATE",BorrowerId,BookName,false,false,null);
		
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
