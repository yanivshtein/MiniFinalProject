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
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class SubscriberOrderUIController {

    @FXML
    private Label title = null;

    @FXML
    private TextField bookName = null;

    @FXML
    private DialogPane errorMsg = null;
    
    @FXML
    private Button sendOrderBtn = null;

    @FXML
    private Button returnBtn = null;

    @FXML
    private Button exitBtn = null;
    
    @FXML
    private ListView<String> booksListView;

    private String bookNameGot;
    private String selectedBook;
    int subID = ChatClient.sub1.getSubscriber_id();
    private ObservableList<String> booksData;

    public void initialize() {
        loadBooks();
        setupAutoComplete();
        booksListView.setOnMouseClicked(event -> handleDoubleClick(event));
    }
    
    private void handleDoubleClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            selectedBook = booksListView.getSelectionModel().getSelectedItem();
            if (selectedBook != null) {            	
            	errorMsg.setContentText("You have selected the Book: " + selectedBook + ", press 'Place Order' to send");
            }
        }
  }
    
    private void loadBooks() {    	
        // Request the books from the server
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
    }
    
    public void getSendBtn(ActionEvent event) throws IOException {
    	Alert alert = new Alert(Alert.AlertType.WARNING);
    	if (selectedBook!=null) {
    		bookNameGot=selectedBook;
    	}
    	else
    		bookNameGot = bookName.getText();

        if (bookNameGot==null || bookNameGot.isEmpty()) {
        	alert.setTitle("Missing Field");
            alert.setContentText("Oops! 😞 You must enter a book name.");
            alert.showAndWait();
            return;	 
        }
        
        // check if subscriber's status is frozen
        ClientGUIConnectionController.chat.acceptFromController(5, subID, "");
        if (ChatClient.isFrozen == true) {
        	alert.setTitle("Frozen Account");
            alert.setContentText("Uh-oh! 😬 Your account is FROZEN!");
            alert.showAndWait();
            return;	 
        }
        
        ClientGUIConnectionController.chat.acceptFromController(6, 0, bookNameGot);
        if (ChatClient.isExist== false) {
        	alert.setTitle("Not exists book");
            alert.setContentText("Sorry! 📚 we dont have this book in our Library");
            alert.showAndWait();
            return;	
        }
        if (ChatClient.isAvailable == true) { // which means there is an available copy of the book -> cant order
        	alert.setTitle("Available copy exists");
            alert.setContentText("Go borrow it! 📚 An available copy of this book already exists in the library.");
            alert.showAndWait();
            return;	
        }
        
        // add column in the Orders table in the DB and in the activityhistory
        ClientGUIConnectionController.chat.acceptFromController(7, subID, bookNameGot);
        // check if the number of copies of the book already been ordered
        if (ChatClient.isCan == false) {
        	alert.setTitle("Number of orders equals copys");
            alert.setContentText("Whoops! 😅 The number of orders and copies are equal, so you can't place another order.");
            alert.showAndWait();
            return;	
        }
        errorMsg.setContentText("Awesome! 🎉 You're all set! Your order has been successfully placed!");
    }

    public void getReturnBtn(ActionEvent event) throws IOException {
	    // Close the current window
	    ((Node) event.getSource()).getScene().getWindow().hide();
	    
	    // Load the ClientGUIHomePage FXML
	    FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ClientGUIHomePageController.fxml"));
	    Parent root = loader.load();
	    
	    Scene scene = new Scene(root);
	    scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
	    
	    Stage primaryStage = new Stage();
	    primaryStage.setTitle("Client Home Page");
	    primaryStage.setScene(scene);
	    primaryStage.show();
	}
    
    public void getExitBtn(ActionEvent event) throws IOException {
        System.exit(0);
    }
}