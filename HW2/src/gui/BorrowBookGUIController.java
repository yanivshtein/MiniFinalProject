package gui;


import java.io.IOException;
import java.util.List;
import client.ChatClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class BorrowBookGUIController {
	
	@FXML
	private TextField id = null;
	
	@FXML
	private TextField barCode = null;
	
    @FXML
    private Label msg = null;
    
	@FXML
	private TextField bookName = null;
	
	@FXML
	private Button borrow = null;
	
	@FXML
	private Button exit = null;
	
	@FXML
	private Button return1 = null;
	
	@FXML
	private Button barcode = null;

	
	
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/gui/BorrowBookGUIController.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
        primaryStage.setTitle("Borrowing a book");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    

    public void borrowBtn(ActionEvent event) {
        int subscriberId = -1;
        Alert alert = new Alert(Alert.AlertType.WARNING);
        
        if(id.getText().isEmpty() || bookName.getText().isEmpty()) {
            alert.setTitle("Missing Fields");
            alert.setHeaderText("Some fields are missing");
            alert.setContentText("Please fill in the following fields");
            alert.showAndWait();
            return;
        } else {
            try {
                subscriberId = Integer.parseInt(id.getText());
            } catch (NumberFormatException e) {
                alert.setTitle("Invalid Input");
                alert.setHeaderText("Please enter a valid number.");
                alert.setContentText("The input must be a valid integer.");
                alert.showAndWait();
                return;
            }
        }

        ClientGUIConnectionController.chat.acceptBorrowBook(subscriberId);
        ClientGUIConnectionController.chat.acceptSearchBook(14, bookName.getText());
        
        if(ChatClient.bool == false) {
            alert.setTitle("Error");
            alert.setContentText("The subscriber does not exist");
            alert.showAndWait();
            return;
        } else {
            if(ChatClient.statusSub.equals("frozen")) {
                alert.setTitle("Error");
                alert.setContentText("The subscription is frozen, it is not possible to make the borrowing");
                alert.showAndWait();
                return;
            } else if(ChatClient.bookAvailability == -1) {
                alert.setTitle("Error");
                alert.setContentText("The book is not in the library");
                alert.showAndWait();
                return;
            } else if(ChatClient.bookAvailability == 0) {
                alert.setTitle("Error");
                alert.setContentText("The book is in the library but currently out of stock");
                alert.showAndWait();
                return;
            } else if(ChatClient.bookAvailability > 0) {
                ClientGUIConnectionController.chat.acceptSearchBook(16, bookName.getText());
                ClientGUIConnectionController.chat.acceptAddToActivityHistoryController(17, subscriberId, bookName.getText());
                msg.setText("The book is available. Copies left: " + (ChatClient.bookAvailability - 1));
            } else {
                msg.setText("ERROR");
            }
        }
    }


    public void getReturnBtn(ActionEvent event) throws IOException{
		FXMLLoader loader = new FXMLLoader();
		((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary window
        Stage primaryStage = new Stage();
        Pane root = loader.load(getClass().getResource("/gui/LibrarianGUIHomePageController.fxml").openStream());

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
        primaryStage.setTitle("Labrarian home page");

        primaryStage.setScene(scene);
        primaryStage.show();
	}
    
    
    public void BarCodeBtn(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
    	try {
    		msg.setText("");
    		bookName.setText("");
    		int bookId = Integer.parseInt(barCode.getText());
    		ClientGUIConnectionController.chat.acceptBarCode(bookId);
        	if(ChatClient.bookName.equals("")) {
                alert.setTitle("Error");
                alert.setContentText("The barcode does not exist in the database");
                alert.showAndWait();
                return;
        	}else {
        		
        		bookName.setText(ChatClient.bookName);
        	}
    	}catch(NumberFormatException e) {
            alert.setTitle("Error");
            alert.setContentText("Please enter a valid barcode number");
            alert.showAndWait();
    		return;
    	}
    	
    	
    }
	public void getExitBtn(ActionEvent event) throws IOException {
		System.out.println("Exit client");
		System.exit(0);
	}
}