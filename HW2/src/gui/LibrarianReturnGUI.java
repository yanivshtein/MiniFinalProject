package gui;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import client.ChatClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

public class LibrarianReturnGUI {

	@FXML
	private TextField subscriberId = null;

	@FXML
	private TextField bookID = null;

	@FXML
	private TextField getSubscribersId = null;

	@FXML
	private TextField bookName = null;

	@FXML
	private Label bookArriveDate = null;

	@FXML
	private Label deadline = null;

	@FXML
	private Button sendButton = null;

	@FXML
	private Button exitButton = null;

	@FXML
	private Button scanButton = null;

	@FXML
	private Button BorrowHistoryIdButton = null;

	@FXML
	private Label sendMsg = null;

	@FXML
	private ListView<String> ShowBorrowedBooks = null;

	private Alert alertMessege = new Alert(AlertType.NONE);

	private String BookName;

	public void sendButton(ActionEvent event) { // method that sends information to the controller to return the book to
												// the library
		LocalDate deadlineDate;
		String errorType;

		errorType = checkBttn();

		switch (errorType) {
		case "Subscriber's ID is missing":
			alertMessege.setContentText("You must provide Subscriber's ID.");
			alertMessege.setAlertType(AlertType.ERROR);
			alertMessege.show(); // Show the error alert
			return;

		case "Borrow does not exist":
			alertMessege.setContentText("The Borrow does not exist in the library's database!");
			alertMessege.setAlertType(AlertType.ERROR);
			alertMessege.show(); // Show the error alert
			return;

		case "Barcode or BookName is missing":
			alertMessege.setContentText("You must provide Barcode or Book name.");
			alertMessege.setAlertType(AlertType.ERROR);
			alertMessege.show(); // Show the error alert
			return;

		default: // if the check is successfully finished
			break;
		}

		String BorrowerId = subscriberId.getText();

		BookName = ChatClient.bookName;
		// get current time in a format of yyyy-MM-dd
		LocalDate currentDate = LocalDate.now();
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		String deadlineString = deadline.getText();

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
		}
		Period difference = Period.between(deadlineDate, currentDate);

		try {

			long daysLate = currentDate.toEpochDay() - deadlineDate.toEpochDay();

			if (daysLate <= 0) {

				ClientGUIConnectionController.chat.returnBook_accept("INSERT", BorrowerId, BookName, false, false,
						difference);

			} else if (daysLate < 7) {

				ClientGUIConnectionController.chat.returnBook_accept("INSERT", BorrowerId, BookName, true, false,
						difference);

			} else if (daysLate >= 7) {
				ClientGUIConnectionController.chat.returnBook_accept("INSERT", BorrowerId, BookName, true, true,
						difference);

			}

			if (ChatClient.isFrozen) {
				showLabelTextForDuration(sendMsg, "Return operation successfully finished!", 5000); // Show text for 5
																									// seconds
				alertMessege.setContentText("The subscriber’s status card has been frozen");
				alertMessege.setAlertType(AlertType.INFORMATION);
				alertMessege.show();
			}

			else {
				showLabelTextForDuration(sendMsg, "Return operation successfully finished!", 5000); // Show text for 5
																									// seconds
			}

		} catch (DateTimeParseException e) {

			e.getStackTrace();
		}

	}

	public void showLabelTextForDuration(Label label, String text, int durationInMillis) {

		label.setText(text);

		Timeline timeline = new Timeline(new KeyFrame(Duration.millis(durationInMillis), event -> label.setText("")));
		timeline.setCycleCount(1);
		timeline.play();
	}

	public String checkBttn() { // method that get information from the data the controller to return the book
								// to the library

		String actionDate = null;
		String Deadline = null;
		String BorrowerId;

		// check if the librarian didn't enter sub ID
		if (subscriberId == null || subscriberId.getText().trim().isEmpty()) {
			return "Subscriber's ID is missing"; // Stop further execution
		}

		// check if the librarian didn't enter barcode or Book name
		if ((bookID == null || bookID.getText().trim().isEmpty())
				&& (bookName == null || bookName.getText().trim().isEmpty())) {
			return "Barcode or BookName is missing"; // Stop further execution
		}

		BorrowerId = subscriberId.getText().trim();

		int index = 0;

		BookName = bookName.getText().toLowerCase();
		// check in the database if exist a borrow with the same borrower ID and book
		// name
		ClientGUIConnectionController.chat.returnBook_accept("EXIST", BorrowerId, BookName, false, false, null);

		// if there isn't any row that match, then return.
		if (ChatClient.bool == false) {

			bookArriveDate.setText(""); // set labels to show them in GUI
			deadline.setText("");

			// isChecked = false;
			return "Borrow does not exist";
		}

		// the book's name from the borrow in the activityhistory table
		BookName = ChatClient.bookName;
		// if there is a match then select the borrow date and deadline.
		ClientGUIConnectionController.chat.returnBook_accept("SELECT DATE", BorrowerId, BookName, false, false, null);

		for (String date : ChatClient.ActionDateAndDeadline) { // get action date and deadline
			if (index == 0)
				actionDate = date;
			else if (index == 1)
				Deadline = date;

			index++;
		}

		// show the borrow date and deadline in the labels
		bookArriveDate.setText(actionDate); // set labels to show them in GUI
		deadline.setText(Deadline);
		return "Check Successfully Finished";
	}

	// return to home page button method
	public void retButton(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary window
		Stage primaryStage = new Stage();
		Pane root = loader.load(getClass().getResource("/gui/LibrarianGUIHomePageController.fxml").openStream());

		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
		primaryStage.setTitle("Home page");

		primaryStage.setScene(scene);
		primaryStage.show();
	}

	// barcode button method

	public void barcodeButton(ActionEvent event) {
		int bookId = 0;
		try {
			bookId = Integer.parseInt(bookID.getText());
		} catch (NumberFormatException e) {
			alertMessege.setContentText("Please scan the Barcode's book.");
			alertMessege.setAlertType(AlertType.ERROR);
			alertMessege.show();
			return;
		}

		ClientGUIConnectionController.chat.acceptBarCode(bookId);
		if (ChatClient.bookName.equals("")) {

			alertMessege.setContentText("The Barcode does not exist in the library’s database.");
			alertMessege.setAlertType(AlertType.ERROR);
			alertMessege.show();
			return;
		} else {
			bookName.setText(ChatClient.bookName);
		}
	}

	public void lostBookBtn(ActionEvent event) {
		String subID = subscriberId.getText();
		String book = bookName.getText();

		if (subID.isEmpty() || !subID.matches("\\d+")) {
			alertMessege.setContentText("Please enter a valid id");
			alertMessege.setAlertType(AlertType.ERROR);
			alertMessege.show();
			return; // Stop further execution if ID is invalid
		}

		if (book.isEmpty()) {
			alertMessege.setContentText("Please enter a book name");
			alertMessege.setAlertType(AlertType.ERROR);
			alertMessege.show();
			return; // Stop further execution if book name is missing
		}

		// Create a confirmation dialog
		Alert confirmationDialog = new Alert(AlertType.CONFIRMATION);
		confirmationDialog.setTitle("Confirmation");
		confirmationDialog.setHeaderText("Confirm Action");
		confirmationDialog.setContentText("Are you sure you want to mark this book as lost?");

		// Show the confirmation dialog and wait for a response
		Optional<ButtonType> result = confirmationDialog.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			// If the user clicks OK, proceed with the action
			ClientGUIConnectionController.chat.lostBook_accept(subID, book);
			if(ChatClient.lostBook.equals("bookAndIDNotExists")){
				alertMessege.setContentText("Subscriber ID and book name are invalid");
				alertMessege.setAlertType(AlertType.ERROR);
				alertMessege.show();
				return; // Stop further execution if book name is missing
			}
			if(ChatClient.lostBook.equals("BorrowNotExist")){
				alertMessege.setContentText("Can't lose book you didn't borrow!");
				alertMessege.setAlertType(AlertType.ERROR);
				alertMessege.show();
				return; // Stop further execution if book name is missing
			}
			if(ChatClient.lostBook.equals("subID")) {
				alertMessege.setContentText("Please enter a valid subscriber ID");
				alertMessege.setAlertType(AlertType.ERROR);
				alertMessege.show();
				return; // Stop further execution if book name is missing
			}
			if(ChatClient.lostBook.equals("bookNotExists")) {
				alertMessege.setContentText("Please enter a valid book name");
				alertMessege.setAlertType(AlertType.ERROR);
				alertMessege.show();
				return; // Stop further execution if book name is missing
			}
			if(ChatClient.lostBook.equals("SubAlreadyFrozen")) {
				showLabelTextForDuration(sendMsg, "Book lost! subscriber account freeze extended for 30 more days!", 5000); // Show text for 5 sec
			}else {
				showLabelTextForDuration(sendMsg, "Book lost! subscriber account frozen for 30 days!", 5000); // Show text for 5 sec
			}
		}
	}

	public void getExitBtn(ActionEvent event) throws IOException {
		System.exit(0);
	}

}