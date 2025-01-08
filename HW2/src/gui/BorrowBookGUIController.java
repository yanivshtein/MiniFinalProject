package gui;

import java.io.IOException;

import client.BorrowBookUI;
import client.ChatClient;
import client.SearchBookUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class BorrowBookGUIController {
	
	@FXML
	private TextField id = null;
	
    @FXML
    private DialogPane msg = null;
    
	@FXML
	private TextField bookName = null;
	
	@FXML
	private Button borrow = null;
	
	@FXML
	private Button exit = null;
	
	@FXML
	private Button return1 = null;

	
	
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/gui/BorrowBookGUIController.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/BorrowBookGUIController.css").toExternalForm());
        primaryStage.setTitle("Borrowing a book");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public void borrowBtn(ActionEvent event)  {
    	 int subscriberId = Integer.parseInt(id.getText());

    	BorrowBookUI.chat.acceptBorrowBook(subscriberId);
    	BorrowBookUI.chat.acceptSearchBook(14,bookName.getText());
    	if(ChatClient.bool==false) {
    		msg.setContentText("Subscriber doesn't exist");
    	}
    	else {
        	if(ChatClient.bookAvailability== -1) {
        		msg.setContentText("The book is not in the library");
        	}
        	else if(ChatClient.bookAvailability == 0) {
        		msg.setContentText("The book is in the library but currently out of stock");
        	}
        	else if(ChatClient.bookAvailability >0) {
            	BorrowBookUI.chat.acceptSearchBook(16,bookName.getText());
            	BorrowBookUI.chat.acceptAddToActivityHistoryController(17,subscriberId,bookName.getText());
        		msg.setContentText("The book is available. Copies left: " + (ChatClient.bookAvailability-1) );
        	}
        	else {
        		msg.setContentText("ERROR" );
        	}
    		
    	}

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
	public void getExitBtn(ActionEvent event) throws IOException {
		System.out.println("Exit client");
		System.exit(0);
	}
}