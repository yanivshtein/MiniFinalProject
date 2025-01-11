package gui;

import java.io.IOException;
import java.util.ArrayList;

import client.ChatClient;
import client.SearchBookUI;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SubscriberOrderUIController {

    @FXML
    private Label title = null;

    @FXML
    private TextField bookName = null;

    @FXML
    private DialogPane errorMsg = null;
    
    @FXML
    private DialogPane errorMsg2 = null;

    @FXML
    private Button sendOrderBtn = null;

    @FXML
    private Button returnBtn = null;

    @FXML
    private Button exitBtn = null;
    
    @FXML
    private ListView<String> booksListView;

    private String bookNameGot;

    private ObservableList<String> booksData;

    /*public void initialize() {
        loadBooks();
        setupAutoComplete();
    }*/
    
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
        int subID = ChatClient.subID;
        bookNameGot = bookName.getText();
        
        if (bookNameGot.isEmpty()) {
            errorMsg.setContentText("Oops! 😞 You must enter a book name.");
            return;
        }
        
        // check if subscriber's status is frozen
        ClientGUIConnectionController.chat.acceptFromController(5, subID, "");
        if (ChatClient.isFrozen == true) {
            errorMsg.setContentText("Uh-oh! 😬 Your account is FROZEN!");
            return;
        }
        
        ClientGUIConnectionController.chat.acceptFromController(6, 0, bookNameGot);
        if (ChatClient.isExist== false) {
        	errorMsg.setContentText("Sorry! 📚 we dont have this book in our Library");
        	return;
        }
        if (ChatClient.isAvailable == true) { // which means there is an available copy of the book -> cant order
            errorMsg.setContentText("Yey! 📚 An available copy of this book already exists in the library.");
            errorMsg2.setContentText("Go borrow it!");
            return;
        }
        
        // add column in the Orders table in the DB and in the activityhistory
        ClientGUIConnectionController.chat.acceptFromController(7, subID, bookNameGot);
        // check if the number of copies of the book already been ordered
        if (ChatClient.isCan == false) {
            errorMsg.setContentText("Whoops! 😅 The number of orders and copies are equal, so you can't place another order.");
            return;
        }
        errorMsg.setContentText("Awesome! 🎉 You're all set! Your order has been successfully placed!");
    }

    public void getReturnBtn(ActionEvent event) throws IOException {
	    // Close the current window
	    ((Node) event.getSource()).getScene().getWindow().hide();
	    
	    // Load the ClientGUIHomePage FXML
	    FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ClientGUIHomePage.fxml"));
	    Parent root = loader.load();
	    
	    Scene scene = new Scene(root);
	    scene.getStylesheets().add(getClass().getResource("/gui/ClientGUIHomePage.css").toExternalForm());
	    
	    Stage primaryStage = new Stage();
	    primaryStage.setTitle("Client Home Page");
	    primaryStage.setScene(scene);
	    primaryStage.show();
	}
    
    public void getExitBtn(ActionEvent event) throws IOException {
        System.exit(0);
    }
}