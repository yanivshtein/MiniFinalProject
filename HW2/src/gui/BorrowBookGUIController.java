package gui;

import java.io.IOException;
import client.ChatClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Controller for the Borrow Book GUI. 
 * Handles user interactions for borrowing books from the library system.
 */
public class BorrowBookGUIController {

    @FXML
    private TextField id = null; // TextField for subscriber ID input

    @FXML
    private TextField barCode = null; // TextField for book barcode input

    @FXML
    private Label msg = null; // Label to display status or error messages

    @FXML
    private TextField bookName = null; // TextField for book name input

    @FXML
    private Button borrow = null; // Button to trigger the borrow action

    @FXML
    private Button exit = null; // Button to exit the application

    @FXML
    private Button return1 = null; // Button to return to the librarian home page

    @FXML
    private Button barcode = null; // Button to handle barcode search

    /**
     * Initializes and starts the Borrow Book GUI.
     * 
     * @param primaryStage The primary stage for this scene.
     * @throws Exception If an error occurs during scene loading.
     */
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/gui/BorrowBookGUIController.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
        primaryStage.setTitle("Borrowing a book");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Handles the borrow button click event. Validates input fields and performs 
     * borrowing logic based on the subscriber and book availability.
     * 
     * @param event The event triggered by clicking the borrow button.
     */
    public void borrowBtn(ActionEvent event) {
        int subscriberId = -1;
        Alert alert = new Alert(Alert.AlertType.WARNING);

        // Validate input fields
        if (id.getText().isEmpty() || bookName.getText().isEmpty()) {
            alert.setTitle("Missing Fields");
            alert.setHeaderText("Some fields are missing");
            alert.setContentText("Please fill in the required fields.");
            alert.showAndWait();
            return;
        } else {
            try {
                subscriberId = Integer.parseInt(id.getText());
            } catch (NumberFormatException e) {
                alert.setTitle("Invalid Input");
                alert.setHeaderText("Please enter a valid number.");
                alert.setContentText("Subscriber ID must be a valid integer.");
                alert.showAndWait();
                return;
            }
        }

        // Perform borrow logic
        ClientGUIConnectionController.chat.acceptBorrowBook(subscriberId);
        ClientGUIConnectionController.chat.acceptSearchBook(14, bookName.getText());

        // Handle various error conditions
        if (!ChatClient.bool) {
            alert.setTitle("Error");
            alert.setContentText("The subscriber does not exist.");
            alert.showAndWait();
        } else if ("frozen".equals(ChatClient.statusSub)) {
            alert.setTitle("Error");
            alert.setContentText("The subscription is frozen. Borrowing is not allowed.");
            alert.showAndWait();
        } else if (ChatClient.bookAvailability == -1) {
            alert.setTitle("Error");
            alert.setContentText("The book is not in the library.");
            alert.showAndWait();
        } else if (ChatClient.bookAvailability == 0) {
            alert.setTitle("Error");
            alert.setContentText("The book is currently out of stock.");
            alert.showAndWait();
        } else if (ChatClient.bookAvailability > 0) {
            ClientGUIConnectionController.chat.acceptSearchBook(16, bookName.getText());
            ClientGUIConnectionController.chat.acceptAddToActivityHistoryController(17, subscriberId, bookName.getText());
            msg.setText("The book is available. Copies left: " + (ChatClient.bookAvailability - 1));
        } else {
            msg.setText("ERROR");
        }
    }

    /**
     * Handles the return button click event. Returns to the librarian home page.
     * 
     * @param event The event triggered by clicking the return button.
     * @throws IOException If an error occurs during scene transition.
     */
    public void getReturnBtn(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        ((Node) event.getSource()).getScene().getWindow().hide(); // Hide current window
        Stage primaryStage = new Stage();
        Pane root = loader.load(getClass().getResource("/gui/LibrarianGUIHomePageController.fxml").openStream());

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
        primaryStage.setTitle("Librarian home page");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Handles the barcode button click event. Searches for a book by barcode.
     * 
     * @param event The event triggered by clicking the barcode button.
     */
    public void BarCodeBtn(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        try {
            msg.setText("");
            bookName.setText("");
            int bookId = Integer.parseInt(barCode.getText());
            ClientGUIConnectionController.chat.acceptBarCode(bookId);

            if (ChatClient.bookName.isEmpty()) {
                alert.setTitle("Error");
                alert.setContentText("The barcode does not exist in the database.");
                alert.showAndWait();
            } else {
                bookName.setText(ChatClient.bookName);
            }
        } catch (NumberFormatException e) {
            alert.setTitle("Error");
            alert.setContentText("Please enter a valid barcode number.");
            alert.showAndWait();
        }
    }

    /**
     * Handles the exit button click event. Exits the application.
     * 
     * @param event The event triggered by clicking the exit button.
     * @throws IOException If an error occurs during exit.
     */
    public void getExitBtn(ActionEvent event) throws IOException {
        System.out.println("Exit client");
        System.exit(0);
    }
}
