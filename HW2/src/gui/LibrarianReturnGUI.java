package gui;


import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

/**
 * Controller for the Librarian Return GUI.
 * Manages book return processes, including validation and updates.
 */
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
	private Button checkButton=null;
	
	@FXML
	private Button scanButton=null;
	
	@FXML
	private Button BorrowHistoryIdButton=null;
	
	
	@FXML
	private Label artMsg = null;
	
	@FXML
	private Label sendMsg = null;
	
	@FXML
	private TextArea ShowHistory = null;
	
	private Alert alertMessege = new Alert(AlertType.NONE);

	private String BookName;
	
	private boolean isChecked = false;
	
	private ArrayList<String> borrowersBorrowHistory;
	
	/**
     * Sends the book return information to the controller.
     * Validates the process before sending.
     */
	public void sendButton(ActionEvent event) {		// method that sends information to the controller to return the book to the library				 
		if (!isChecked) {
	        alertMessege.setContentText("You must check if the borrow exists before returning the book.");
	        alertMessege.setAlertType(AlertType.ERROR);
	        alertMessege.show();
	        return;
	    }
		
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
		
		
		// check if the book has already returned by the subscriber
		ClientGUIConnectionController.chat.returnBook_accept("CHECK_BOOK_RETURNED", BorrowerId, BookName, null, null, null);
		
		// if the book already returned then show message 
		if (ChatClient.bool==true) {
			//sendMsg.setText("Book has already returned!");
			showLabelTextForDuration(sendMsg, "Book has already returned!", 3000); // Show text for 3 seconds

			return;
		}
				
		BookName = ChatClient.bookName;
		System.out.println("book name is: "+BookName);
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
//			alertMessege.setContentText("Error need to check if exist borrow first");	
//		 	alertMessege.setAlertType(AlertType.ERROR);
			e.getStackTrace();
		}
		
//		isChecked = false;
		//checkButton.setDisable(true);
	}
	
	/**
	 * This method displays a given text on a label for a specified duration, after which the label text is cleared.
	 * @param label The Label control where the text will be displayed.
	 * @param text The text to display on the label.
	 * @param durationInMillis The duration for which the text should be displayed, in milliseconds.
	 */
	public void showLabelTextForDuration(Label label, String text , int durationInMillis) {
		
		label.setText(text);
		
		Timeline timeline= new Timeline(new KeyFrame(Duration.millis(durationInMillis),event -> label.setText("")));
		timeline.setCycleCount(1);
		timeline.play();
	}
	
	/**
     * Validates if a borrow record exists in the database.
     */
	public void checkBttn(ActionEvent event) {		// method that get information from the data the controller to return the book to the library			
		String actionDate = null;
		String Deadline = null;
		String BorrowerId;
		String BookID;
		
		if (subscriberId == null || bookID == null || 
		        subscriberId.getText().trim().isEmpty() || bookID.getText().trim().isEmpty()) {
		        alertMessege.setContentText("You must provide both the Subscriber's ID and the Barcode.");
		        alertMessege.setAlertType(AlertType.ERROR);
		        alertMessege.show(); // Show the error alert
		        return; // Stop further execution
		}
		BorrowerId = subscriberId.getText().trim();
	    BookID = bookID.getText().trim();

		int index =0;
		
		if(artMsg!=null)		// if the text in Label is currently visible 
			artMsg.setText("");
		
		// check in the database if exist a borrow with the same borrower ID and book name
		ClientGUIConnectionController.chat.returnBook_accept("EXIST", BorrowerId, BookID,false,false,null);
		
		// if there isn't any row that match, then show in label.
		if (ChatClient.bool==false) {
			
			bookArriveDate.setText("");		// set labels to show them in GUI
			deadline.setText("");
			
			showLabelTextForDuration(artMsg, "The Borrow does not exist!", 3000); // Show text for 3 seconds
			isChecked = false;
			return;
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
		
		isChecked = true;
		// show the borrow date and deadline in the labels
		bookArriveDate.setText(actionDate);		// set labels to show them in GUI
		deadline.setText(Deadline);
	}
	
	/**
	 * This method handles the action of returning to the librarian's home page.
	 * @param event The ActionEvent triggered by the button click.

	 * @throws IOException If an I/O error occurs during the loading of the FXML or CSS files.
	 */
	public void retButton(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary window
        Stage primaryStage = new Stage();
        Pane root = loader.load(getClass().getResource("/gui/LibrarianGUIHomePageController.fxml").openStream());

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
        primaryStage.setTitle("Librarian home page");

        primaryStage.setScene(scene);
        primaryStage.show();
	}
	
	/**
     * Displays a subscriber's borrowing history.
     */
	public void viewBorrowersHistoryButton(ActionEvent event) {
		StringBuilder history = new StringBuilder();
		
		 int subscriberId = Integer.parseInt(getSubscribersId.getText());

    	 ClientGUIConnectionController.chat.acceptBorrowBook(subscriberId);
		
    	 if(!ChatClient.bool) {
    		 
    		alertMessege.setContentText("The subscriber ID does not exist in the library’s database.");	
    		alertMessege.setAlertType(AlertType.ERROR);
    		alertMessege.show();
    		return;
    			
    	 }
    	 
    	 
		ClientGUIConnectionController.chat.accept("select", getSubscribersId.getText(), "", "");
        Subscriber1 sub = ChatClient.s1;
        history.append("Subscriber's ID:  " + String.valueOf(sub.getSubscriber_id())+"\n");
        history.append("Subscriber's Name:  " + sub.getSubscriber_name()+"\n");
        history.append("Subscriber's Phone:  " + sub.getSubscriber_phone_number()+"\n");
        history.append("Subscriber's Email:  " + sub.getSubscriber_email()+"\n");
        history.append("Subscriber's Status:  " + sub.getSub_status()+"\n\n");
        
		if (getSubscribersId.getText().trim().isEmpty()) {
		        alertMessege.setContentText("You must provide the Subscriber's ID.");
		        alertMessege.setAlertType(AlertType.ERROR);
		        alertMessege.show(); // Show the error alert
		        return; // Stop further execution
		}				
		ClientGUIConnectionController.chat.accept("watch borrow history", getSubscribersId.getText(), "", "");			
		borrowersBorrowHistory = ChatClient.borrowHistory;		
		if(borrowersBorrowHistory.isEmpty()) {	
			alertMessege.setContentText("There is no borrow history.");	
		 	alertMessege.setAlertType(AlertType.INFORMATION);
		 	alertMessege.show();
		}
		
		for(String line: borrowersBorrowHistory) {
			history.append("***********************************"
					+ "*********************************************"
					+ "*********************************************"
					+ "**********\n\n");
			history.append(line);
			history.append("\n\n");
		}
		history.append("***********************************"
				+ "*********************************************"
				+ "*********************************************"
				+ "**********");
		ShowHistory.setText(history.toString());
//		checkButton.setVisible(isChecked);
	}
	
	//when in the barcode the book was scanned we enable the button
	
	/**
     * Updates the book name field based on the barcode scan.
     */
	public void barcodeButton (ActionEvent event) {
		int bookId = Integer.parseInt(bookID.getText());
		ClientGUIConnectionController.chat.acceptBarCode(bookId);
		if(ChatClient.bookName.equals("")) {
			showLabelTextForDuration(artMsg, "The Borrow does not exist!", 4000); // Show text for 4 seconds
		}
    	else {
    		bookName.setText(ChatClient.bookName);
    	}
	}
}


