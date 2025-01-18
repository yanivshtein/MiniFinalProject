package gui;

import java.io.IOException;
import java.util.ArrayList;

import client.ChatClient;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class SearchBookGUIController {
	@FXML
	private Button search = null;

	@FXML
	private TextField bookName = null;

	@FXML
	private Label errorMsg = null;

	@FXML
	private Button return1 = null;

	@FXML
	private Button exit = null;

	@FXML
	private ListView<String> booksListView;

	@FXML
	private TextField author_genere = null;

	@FXML
	private RadioButton authorName = null;

	@FXML
	private RadioButton genere = null;

	private ObservableList<String> booksData;

	public void initialize() {
		loadBooks();
		setupAutoComplete();
	
	    booksListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
	        if (newValue != null) {  
	            bookName.setText(newValue);            
	        }
	    });
	   
	    bookName.setOnKeyPressed(event -> {
	        switch (event.getCode()) {
	            case BACK_SPACE:
	            case DELETE:
	            	booksListView.getSelectionModel().clearSelection();
	                break;
	            default:
	                break;
	        }
	    });
	}

	private void loadBooks() {
		ClientGUIConnectionController.chat.acceptAllTheBooks(18);
		ArrayList<String> bookNames = ChatClient.allbooks;
		if (bookNames == null || bookNames.isEmpty()) {
			booksData = FXCollections.observableArrayList("No books available");
			booksListView.setItems(booksData);
			return;
		}

		booksData = FXCollections.observableArrayList(bookNames);
		booksListView.setItems(booksData);
	}

	private void setupAutoComplete() {

		bookName.textProperty().addListener((observable, oldValue, newValue) -> {
			genere.setSelected(false);
			authorName.setSelected(false);
			if (newValue == null || newValue.isEmpty()) {
				booksListView.setItems(booksData);
			} else {

				String lowerCaseFilter = newValue.toLowerCase();
				ObservableList<String> filteredBooks = FXCollections.observableArrayList();
				for (String book : booksData) {
					if (book.toLowerCase().contains(lowerCaseFilter)) {
						filteredBooks.add(book);
					}
				}
				booksListView.setItems(filteredBooks);
				
			}
		});
		author_genere.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == null || newValue.isEmpty()) {
				booksListView.setItems(booksData);
			} else {
				String criteria = "";
				if (authorName.isSelected()) {
					criteria = "author";
				} else if (genere.isSelected()) {
					criteria = "Genre";
				}

				if (criteria.isEmpty()) {
					booksListView.setItems(FXCollections.observableArrayList("Please select an author or genre"));
					return;
				}
				ClientGUIConnectionController.chat.acceptSearchByCriteria(criteria, newValue);

				ArrayList<String> serverResponse = ChatClient.filteredBooks;
				ObservableList<String> filteredBooks;
				if (serverResponse == null || serverResponse.isEmpty()) {
					filteredBooks = FXCollections.observableArrayList("No books found for the given criteria");
				} else {
					filteredBooks = FXCollections.observableArrayList(serverResponse);
				}
				booksListView.setItems(filteredBooks);
			}
		});
	}

	public void authorRadioButton(ActionEvent event) {
		genere.setSelected(false);
	}

	public void genreRadioButton(ActionEvent event) {
		authorName.setSelected(false);
	}

	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/gui/SearchBookGUIController.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
		primaryStage.setTitle("Search a book");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public void searchBtn(ActionEvent event) {
		//ברי תעתיק מפה!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		Alert alert = new Alert(Alert.AlertType.WARNING);
		if(bookName.getText().isEmpty()) {
            alert.setTitle("Missing Field");
            alert.setContentText("Please fill the name of the book");
            alert.showAndWait();
            return;
		}else {
		ClientGUIConnectionController.chat.acceptSearchBook(14, bookName.getText());
		if (ChatClient.bookAvailability == -1) {
            alert.setTitle("Error");
            alert.setContentText("The book is not in the library");
            alert.showAndWait();
            return;
			
		} else if (ChatClient.bookAvailability == 0) {
            alert.setTitle("Error");
            alert.setContentText("The book is in the library but currently out of stock, estimated return date is: "+ChatClient.deadlineDate);
            alert.showAndWait();
            return;
		} else if (ChatClient.bookAvailability > 0) {
			errorMsg.setText("The book is available on shelf A. Copies left: " + ChatClient.bookAvailability);
		} else {
			errorMsg.setText("ERROR");
		}
		}
	}

	public void getExitBtn(ActionEvent event) throws IOException {
		System.out.println("Exit client");
		System.exit(0);
	}

	public void getReturnBtn(ActionEvent event) throws IOException {
		if (ChatClient.lib ==null && ChatClient.sub1==null) {
			FXMLLoader loader = new FXMLLoader();
			((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary window
			Stage primaryStage = new Stage();
			Pane root = loader.load(getClass().getResource("/gui/ClientGUILogin.fxml").openStream());

			Scene scene = new Scene(root);
			scene.getStylesheets()
					.add(getClass().getResource("/gui/AppCss.css").toExternalForm());
			primaryStage.setTitle("Login");

			primaryStage.setScene(scene);
			primaryStage.show();
		}
		if (ChatClient.lib != null) {
			FXMLLoader loader = new FXMLLoader();
			((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary window
			Stage primaryStage = new Stage();
			Pane root = loader.load(getClass().getResource("/gui/LibrarianGUIHomePageController.fxml").openStream());

			Scene scene = new Scene(root);
			scene.getStylesheets()
					.add(getClass().getResource("/gui/AppCss.css").toExternalForm());
			primaryStage.setTitle("Librarian home page");

			primaryStage.setScene(scene);
			primaryStage.show();
		}
	if(ChatClient.sub1 != null) {
		FXMLLoader loader = new FXMLLoader();
		((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary window
		Stage primaryStage = new Stage();
		Pane root = loader.load(getClass().getResource("/gui/ClientGUIHomePageController.fxml").openStream());

		Scene scene = new Scene(root);
		scene.getStylesheets()
				.add(getClass().getResource("/gui/AppCss.css").toExternalForm());
		primaryStage.setTitle("Librarian home page");

		primaryStage.setScene(scene);
		primaryStage.show();
	}

	}
}