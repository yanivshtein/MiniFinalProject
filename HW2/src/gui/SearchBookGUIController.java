package gui;

import java.io.IOException;
import java.util.ArrayList;

import client.ChatClient;
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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Controller class for the "Search Book" GUI.
 * Allows users to search for books by name, author, or genre and displays the search results.
 */
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

	/**
     * Initializes the controller by loading books, setting up autocomplete for book search,
     * and configuring event handlers for user interaction.
     */
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

	/**
     * Loads all available books into the list view by requesting data from the server.
     */
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
	/**
     * Sets up autocomplete functionality for the book search fields.
     * Filters the list view based on user input in the text fields.
     */
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

	/**
     * Handles the action of selecting the "Author" radio button.
     * Ensures only one radio button can be selected at a time.
     *
     * @param event the action event triggered by clicking the "Author" radio button.
     */
	public void authorRadioButton(ActionEvent event) {
		genere.setSelected(false);
	}

	/**
     * Handles the action of selecting the "Genre" radio button.
     * Ensures only one radio button can be selected at a time.
     *
     * @param event the action event triggered by clicking the "Genre" radio button.
     */
	public void genreRadioButton(ActionEvent event) {
		authorName.setSelected(false);
	}

	/**
     * Starts the "Search Book" GUI.
     *
     * @param primaryStage the primary stage for the application.
     * @throws Exception if an error occurs while loading the FXML file or initializing the scene.
     */
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/gui/SearchBookGUIController.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
		primaryStage.setTitle("Search a book");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	 /**
     * Handles the action of the "Search" button.
     * Validates the book name field and checks the availability of the specified book.
     *
     * @param event the action event triggered by clicking the "Search" button.
     */
	public void searchBtn(ActionEvent event) {
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

	/**
     * Handles the action for the "Exit" button.
     * Closes the application.
     *
     * @param event the action event triggered by clicking the "Exit" button.
     * @throws IOException if an error occurs during the exit process.
     */
	public void getExitBtn(ActionEvent event) throws IOException {
		System.out.println("Exit client");
		System.exit(0);
	}

	/**
     * Handles the action for the "Return" button.
     * Navigates back to the appropriate home page based on the user's role.
     *
     * @param event the action event triggered by clicking the "Return" button.
     * @throws IOException if an error occurs while loading the FXML file.
     */
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
		primaryStage.setTitle("Subscriber home page");

		primaryStage.setScene(scene);
		primaryStage.show();
	}

	}
}