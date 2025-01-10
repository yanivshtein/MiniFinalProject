package gui;

import java.io.IOException;
import client.ChatClient;
import client.ClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
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
    private Button sendOrderBtn = null;

    @FXML
    private Button returnBtn = null;

    @FXML
    private Button exitBtn = null;

    private String bookNameGot;
    
    /*public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/gui/SubscriberOrderUI.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/SubscriberOrderUI.css").toExternalForm());
        primaryStage.setTitle("Orders");
        primaryStage.setScene(scene);
        primaryStage.show();
    }*/

    public void getSendBtn(ActionEvent event) throws IOException {
        int subID = ChatClient.subID;
        bookNameGot = bookName.getText();
        
        if (bookNameGot.isEmpty()) {
            errorMsg.setContentText("Oops! 😞 You must enter a book name.");
            return;
        }
        
        // check if subscriber's status is frozen
        ClientGUIConnectionController.chat.acceptFromOrderController(5, subID, "");
        if (ChatClient.isFrozen == true) {
            errorMsg.setContentText("Uh-oh! 😬 Your account is FROZEN!");
            return;
        }
        
        ClientGUIConnectionController.chat.acceptFromOrderController(6, 0, bookNameGot);
        if (ChatClient.isExist== false) {
        	errorMsg.setContentText("Sorry! 📚 we dont have this book in our Library");
        	return;
        }
        if (ChatClient.isAvailable == true) { // which means there is an available copy of the book -> cant order
            errorMsg.setContentText("Sorry! 📚 An available copy of this book already exists in the library.");
            return;
        }
        
        // add column in the Orders table in the DB and in the activityhistory
        ClientGUIConnectionController.chat.acceptFromOrderController(7, subID, bookNameGot);
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