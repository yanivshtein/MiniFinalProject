package gui;

import java.io.IOException;
import java.util.ArrayList;
import client.ChatClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class OrdersToTakeController {

	private int ID;
	private String selectedBook;

	@FXML
	private TextField subID;

	@FXML
	private ListView<String> ordersListView;

	@FXML
	private DialogPane msg1;

	@FXML
	private DialogPane msg2;

	public void getShowOrders(ActionEvent event) throws IOException {
		if (subID.getText().trim().isEmpty()) {
			msg1.setContentText("You must enter an ID");
			return;
		}
		try {
			ID = Integer.parseInt(subID.getText());
		} catch (NumberFormatException e) {
			msg1.setContentText("You must enter a number");
		}
		msg1.setContentText("");
		ClientGUIConnectionController.chat.acceptFromController(27, ID, "");
		ArrayList<String> books = ChatClient.orders;
		ordersListView.getItems().addAll(books);

		// Add double-click event handler
		ordersListView.setOnMouseClicked(this::handleDoubleClick); // call handleDoubleClick method
	}

	private void handleDoubleClick(MouseEvent event) {
		if (event.getClickCount() == 2) {
			selectedBook = ordersListView.getSelectionModel().getSelectedItem();
			if (selectedBook != null) {
				msg2.setContentText("Book selected: " + selectedBook + ", press 'Borrow' to send");
			}
		}
	}

	public void getBorrowBtn(ActionEvent event) throws IOException {
		ClientGUIConnectionController.chat.acceptAddToActivityHistoryController(17, ID, selectedBook);
		msg2.setContentText("Great! The book: " + selectedBook + " has been borrowed");
		ClientGUIConnectionController.chat.acceptFromController(28, ID, selectedBook);
	}
	
	 public void getReturnBtn(ActionEvent event) throws IOException {
		    // Close the current window
		    ((Node) event.getSource()).getScene().getWindow().hide();
		    
		    // Load the ClientGUIHomePage FXML
		    FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/LibrarianGUIHomePageController.fxml"));
		    Parent root = loader.load();
		    
		    Scene scene = new Scene(root);
		    scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
		    
		    Stage primaryStage = new Stage();
		    primaryStage.setTitle("Librarian Home Page");
		    primaryStage.setScene(scene);
		    primaryStage.show();
		}
	 
	 public void getExitBtn(ActionEvent event) throws IOException {
	        System.exit(0);
	    }
}
