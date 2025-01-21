package gui;


import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Observable;

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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LibrarianReturnGUI {

	
	@FXML
	private TextField subscriberId=null;
	
	@FXML
	private TextField bookID=null;
	
	@FXML
	private TextField getSubscribersId=null;
	
	@FXML
	private TextField bookName=null;
	
	@FXML
	private Label bookArriveDate=null;
	
	@FXML
	private Label deadline=null;
	
	@FXML
	private Button sendButton=null;
	
	@FXML
	private Button exitButton=null;
	
	@FXML
	private Button scanButton=null;
	
	@FXML
	private Button BorrowHistoryIdButton=null;
	
	
	@FXML
	private Label sendMsg = null;
	
	@FXML
	private ListView<String> ShowBorrowedBooks = null;
	
	private Alert alertMessege = new Alert(AlertType.NONE);

	private String BookName;
	
	private ArrayList<String> borrowersBorrowedBooks;
	
	private ObservableList<String> BooksNames;
	
	public void initialize() {
		showAllBooks();
		
		
	}
	
	
	
	public void sendButton(ActionEvent event) {		// method that sends information to the controller to return the book to the library
		LocalDate deadlineDate;
		 String errorType;
		
		errorType=checkBttn();
		
		switch (errorType) {
		case "Subscriber's ID or Barcode is missing":
			alertMessege.setContentText("You must provide both the Subscriber's ID and the Barcode.");
	        alertMessege.setAlertType(AlertType.ERROR);
	        alertMessege.show(); // Show the error alert
			return;
		
		case "Borrow does not exist":
			alertMessege.setContentText("The Borrow does not exist in the library's database!.");
	        alertMessege.setAlertType(AlertType.ERROR);
	        alertMessege.show(); // Show the error alert
			return;
		case "Action Date And deadline is NULL":
			alertMessege.setContentText("Action Date and deadline is null");
	        alertMessege.setAlertType(AlertType.ERROR);
	        alertMessege.show(); // Show the error alert
	        return;
		default:	// if the check is successfully finished
			break;
		}
		

		if(sendMsg!=null) {
			sendMsg.setText("");;
		}
		
		String BorrowerId=subscriberId.getText();
		
		
		BookName = ChatClient.bookName;
		System.out.println("book name is: "+BookName);
		// get current time in a format of yyyy-MM-dd
		LocalDate currentDate=LocalDate.now();
		DateTimeFormatter dateFormatter= DateTimeFormatter.ofPattern("yyyy-MM-dd");

		String deadlineString= deadline.getText();
		
		if (deadlineString == null || deadlineString.isEmpty()) {
		    alertMessege.setContentText("Deadline is missing or invalid.");
		    alertMessege.setAlertType(AlertType.ERROR);
		    alertMessege.show();
		    return;
		}
		
		try {
			deadlineDate = LocalDate.parse(deadlineString, dateFormatter);
		} catch (DateTimeParseException e) {
			e.printStackTrace();
		    return;
		}		Period difference = Period.between( deadlineDate,currentDate);
		
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

		
			if(ChatClient.isFrozen) {	
				showLabelTextForDuration(sendMsg, "Return operation successfully finished!", 3000); // Show text for 3 seconds
				alertMessege.setContentText("The subscriber’s status card has been frozen");	
				alertMessege.setAlertType(AlertType.INFORMATION);
				alertMessege.show();
			}
		
			else {
			showLabelTextForDuration(sendMsg, "Return operation successfully finished!", 3000); // Show text for 3 seconds
			}

		}catch (DateTimeParseException e) {

			e.getStackTrace();
		}
		

	}
	
	public void showLabelTextForDuration(Label label, String text , int durationInMillis) {
		
		label.setText(text);
		
		Timeline timeline= new Timeline(new KeyFrame(Duration.millis(durationInMillis),event -> label.setText("")));
		timeline.setCycleCount(1);
		timeline.play();
	}
	public String checkBttn() {		// method that get information from the data the controller to return the book to the library
		
		
		String actionDate = null;
		String Deadline = null;
		String BorrowerId;
		String BookID;
		
		if (subscriberId == null || bookID == null || 
		        subscriberId.getText().trim().isEmpty() || bookID.getText().trim().isEmpty()) {
		        return "Subscriber's ID or Barcode is missing"; // Stop further execution
		}
		BorrowerId = subscriberId.getText().trim();
	    BookID = bookID.getText().trim();

		int index =0;
		
		
		
		// check in the database if exist a borrow with the same borrower ID and book name
		ClientGUIConnectionController.chat.returnBook_accept("EXIST", BorrowerId, BookID,false,false,null);
		
		// if there isn't any row that match, then return.
		if (ChatClient.bool==false) {
			
			bookArriveDate.setText("");		// set labels to show them in GUI
			deadline.setText("");
			
			//isChecked = false;
			return "Borrow does not exist";
		}
		

		BookName = ChatClient.bookName;
		// if there is a match then select the borrow date and deadline.
		ClientGUIConnectionController.chat.returnBook_accept("SELECT DATE",BorrowerId,BookName,false,false,null);
		

		
		for (String date : ChatClient.ActionDateAndDeadline) {	// get action date and deadline
			if(index ==0)
				actionDate = date;
			else if(index ==1)
				Deadline = date;
			
			index++;
		}
		
		//isChecked = true;
		// show the borrow date and deadline in the labels
		bookArriveDate.setText(actionDate);		// set labels to show them in GUI
		deadline.setText(Deadline);
		return "Check Successfully Finished";
	}
	
	
	public void retButton(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary window
        Stage primaryStage = new Stage();
        Pane root = loader.load(getClass().getResource("/gui/LibrarianGUIHomePageController.fxml").openStream());

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/LibrarianGUIHomePageController.css").toExternalForm());
        primaryStage.setTitle("Librarian home page");

        primaryStage.setScene(scene);
        primaryStage.show();
	}
	
	public void viewBorrowersHistoryButton(ActionEvent event) {
		
		if (getSubscribersId == null || getSubscribersId.getText().trim().isEmpty()) {
			alertMessege.setContentText("Enter Subscriber's ID.");
	        alertMessege.setAlertType(AlertType.ERROR);
	        ShowBorrowedBooks.getItems().clear();
	        alertMessege.show(); // Show the error alert
	        return; // Stop further execution
		}
		
		ClientGUIConnectionController.chat.accept("select", getSubscribersId.getText(), "", "");
		
		if(ChatClient.s1.getSubscriber_id()==0) {
			alertMessege.setContentText("There is no subscriber with this ID in the database.");
	        alertMessege.setAlertType(AlertType.ERROR);
	        ShowBorrowedBooks.getItems().clear();
	        alertMessege.show(); // Show the error alert
	        return; // Stop further execution
		}
		 ClientGUIConnectionController.chat.returnBook_accept("SELECT_CURRENT_BORROWED_BOOKS_BY_ID", getSubscribersId.getText(), "", null, null, null);
		
		  
    	 if(ChatClient.subCurrentBorrowedBooks.isEmpty()) {
    		 
    		alertMessege.setContentText("No currently borrowed books found.");	
    		alertMessege.setAlertType(AlertType.ERROR);
    		ShowBorrowedBooks.getItems().clear();
    		alertMessege.show();
    		return;
    			
    	 }
    	 borrowersBorrowedBooks = ChatClient.subCurrentBorrowedBooks;
    	 BooksNames = FXCollections.observableArrayList(borrowersBorrowedBooks);
 		ShowBorrowedBooks.setItems(BooksNames);
    	 
	} 

	
	
	// barcode button method
	
	public void barcodeButton (ActionEvent event) {
		int bookId=0;
		try {
			bookId = Integer.parseInt(bookID.getText());
		} catch (NumberFormatException e) {
			alertMessege.setContentText("Please scan the Barcode's book.");	
    		alertMessege.setAlertType(AlertType.ERROR);
    		alertMessege.show();
    		return;
		}
		
		ClientGUIConnectionController.chat.acceptBarCode(bookId);
		if(ChatClient.bookName.equals("")) {
			
			alertMessege.setContentText("The Barcode does not exist in the library’s database.");	
    		alertMessege.setAlertType(AlertType.ERROR);
    		alertMessege.show();
    		return;
		}
    	else {
    		bookName.setText(ChatClient.bookName);
    	}
	}
	
	private void showAllBooks() {
		
		ClientGUIConnectionController.chat.acceptAllTheBooks(18);
		borrowersBorrowedBooks = ChatClient.allbooks;
		
		if(borrowersBorrowedBooks==null || borrowersBorrowedBooks.isEmpty()) {
			BooksNames = FXCollections.observableArrayList("No Books currently available");
			ShowBorrowedBooks.setItems(BooksNames);
		}
		BooksNames = FXCollections.observableArrayList(borrowersBorrowedBooks);
		ShowBorrowedBooks.setItems(BooksNames);
	}
	
	public void getExitBtn(ActionEvent event) throws IOException {
		System.exit(0);
	}
	
}