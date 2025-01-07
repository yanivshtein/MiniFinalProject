package gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import client.AddSubscriberUI;
import client.ChatClient;
import client.SearchBookUI;
import javafx.application.Platform;
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
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class SearchBookGUIController {
	@FXML
	private Button search = null;
	
	@FXML
	private TextField bookName = null;
	
    @FXML
    private DialogPane errorMsg = null;
    
	@FXML
	private Button return1 = null;
	
	@FXML
	private Button exit = null;
	 
	@FXML
    private ListView<String> booksListView;

	 
	    public void initialize() {
	        loadBooks();
	    }
	    private void loadBooks() {
	        SearchBookUI.chat.acceptAllTheBooks(18); // Request the books from the server
	        
	        ArrayList<String> bookNames = ChatClient.allbooks;
	        System.out.println(ChatClient.allbooks +" search");      
	        System.out.println(bookNames +" search");

	        if (bookNames == null || bookNames.isEmpty()) {
	            System.out.println("No books found."); // Optionally display this in the UI
	            booksListView.setItems(FXCollections.observableArrayList("No books available"));
	            return;
	        }

	        ObservableList<String> booksData = FXCollections.observableArrayList(bookNames);
	        booksListView.setItems(booksData);
	    }
    
	
	
	
	
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/gui/SearchBookGUIController.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/SearchBookGUIController.css").toExternalForm());
        primaryStage.setTitle("Search a book");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public void searchBtn(ActionEvent event)  {
    	
    	SearchBookUI.chat.acceptSearchBook(14,bookName.getText());
    	if(ChatClient.bookAvailability== -1) {
    		errorMsg.setContentText("The book is not in the library");
    	}
    	else if(ChatClient.bookAvailability == 0) {
    		errorMsg.setContentText("The book is in the library but currently out of stock");
    	}
    	else if(ChatClient.bookAvailability >0) {
    		errorMsg.setContentText("The book is available. Copies left: " + ChatClient.bookAvailability );
    	}
    	else {
    		errorMsg.setContentText("ERROR" );
    	}
    	
       
        
    }
	public void getExitBtn(ActionEvent event) throws IOException {
		System.out.println("Exit client");
		System.exit(0);
	}
	public void getReturnBtn(ActionEvent event) throws IOException{
		FXMLLoader loader = new FXMLLoader();
		((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary window
        Stage primaryStage = new Stage();
        Pane root = loader.load(getClass().getResource("/gui/LibrarianGUIHomePageController.fxml").openStream());

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/LibrarianGUIHomePageController.css").toExternalForm());
        primaryStage.setTitle("Labrarian home page");

        primaryStage.setScene(scene);
        primaryStage.show();
	}


}
