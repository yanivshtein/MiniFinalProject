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
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * Controller class for handling subscriber orders to be taken.
 * Allows librarians to view and manage book orders for specific subscribers.
 */
public class OrdersToTakeController {

	private int ID;
	private String selectedBook;

	@FXML
	private TextField subID;

	@FXML
	private ListView<String> ordersListView;

	@FXML
	private Label msg;

	Alert alert = new Alert(Alert.AlertType.WARNING);
	
	/**
     * Fetches and displays the list of orders for the specified subscriber ID.
     *
     * @param event Action event triggered by clicking the "Show Orders" button.
     * @throws IOException if an error occurs during the process.
     */
	public void getShowOrders(ActionEvent event) throws IOException {		
		if(subID.getText().isEmpty()) {
            alert.setTitle("Missing Field");
            alert.setContentText("Please fill an ID");
            alert.showAndWait();
            return;
		}
		else {
			try {
				ID = Integer.parseInt(subID.getText());
			} catch (NumberFormatException e) {
				alert.setTitle("Error");
	            alert.setContentText("You must enter a valid number");
	            alert.showAndWait();
	            return;
			}
		}
		ClientGUIConnectionController.chat.acceptBorrowBook(ID);
		if(ChatClient.bool == false) {
            alert.setTitle("Error");
            alert.setContentText("The subscriber does not exist");
            alert.showAndWait();
            return;
		}
		ClientGUIConnectionController.chat.acceptFromController(27, ID, "");
		ArrayList<String> books = ChatClient.orders;
		if (books.isEmpty()) {
			 alert.setTitle("No orders");
	         alert.setContentText("No waiting orders for this subscriber");
	         alert.showAndWait();
	         return;
		}
		ordersListView.getItems().addAll(books);

		// Add double-click event handler
		ordersListView.setOnMouseClicked(this::handleDoubleClick); // call handleDoubleClick method
	}

	/**
     * Handles the double-click event on a book in the list view.
     *
     * @param event Mouse event triggered by double-clicking a book in the list.
     */
	private void handleDoubleClick(MouseEvent event) {
		if (event.getClickCount() == 2) {
			selectedBook = ordersListView.getSelectionModel().getSelectedItem();
			if (selectedBook != null) {
				msg.setText("Book selected: " + selectedBook + ", press 'Borrow' to send");
			}
		}
	}

	/**
     * Borrows the selected book for the subscriber and updates the system.
     *
     * @param event Action event triggered by clicking the "Borrow" button.
     * @throws IOException if an error occurs during the process.
     */
	public void getBorrowBtn(ActionEvent event) throws IOException {
		if (selectedBook==null) {
			alert.setTitle("Not selected");
	        alert.setContentText("No book has been selected");
	        alert.showAndWait();
	        return;
		}
		ClientGUIConnectionController.chat.acceptAddToActivityHistoryController(17, ID, selectedBook);
		msg.setText("Great! The book: " + selectedBook + " has been borrowed");
		ClientGUIConnectionController.chat.acceptFromController(28, ID, selectedBook);
	}
	
	 /**
     * Returns to the librarian home page.
     *
     * @param event Action event triggered by clicking the "Return" button.
     * @throws IOException if an error occurs while loading the home page.
     */
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
	 
	 /**
     * Exits the application.
     *
     * @param event Action event triggered by clicking the "Exit" button.
     * @throws IOException if an error occurs during the process.
     */
	 public void getExitBtn(ActionEvent event) throws IOException {
	        System.exit(0);
	    }
}
