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
 * Controller class for updating subscriber details in the client GUI.
 * It allows the user to update their email and phone number and ensures that the inputs are valid.
 */
public class ClientGUIUpdateDetailsController {

    /**
     * The button for updating subscriber details.
     */
    @FXML
    private Button update = null;

    /**
     * The button for exiting the application.
     */
    @FXML
    private Button exitBtn = null;

    /**
     * The button for returning to the home page.
     */
    @FXML
    private Button Return = null;

    /**
     * The text field for entering the phone number.
     */
    @FXML
    private TextField Phone = null;

    /**
     * The text field for entering the email address.
     */
    @FXML
    private TextField Email = null;

    /**
     * The label displaying a message after updating details.
     */
    @FXML
    private Label afterUpdate = null;

    /**
     * The label displaying the subscriber's ID.
     */
    @FXML
    private Label IdName = null;

    /**
     * The label displaying the subscriber's name.
     */
    @FXML
    private Label subName = null;

    /**
     * The ID of the current subscriber.
     */
    String subId;

    /**
     * Initializes the controller by retrieving the subscriber's details
     * and populating the relevant fields with this information.
     */
    @FXML
    private void initialize() {
        subId = Integer.toString(ChatClient.sub1.getSubscriber_id());
        ClientGUIConnectionController.chat.accept("select", subId, "", "");
        IdName.setText(Integer.toString(ChatClient.s1.getSubscriber_id()));
        subName.setText(ChatClient.s1.getSubscriber_name());
        Phone.setText(ChatClient.s1.getSubscriber_phone_number());
        Email.setText(ChatClient.s1.getSubscriber_email());
    }

    /**
     * Starts the client GUI by loading the home page FXML and displaying it.
     *
     * @param primaryStage the main application window.
     * @throws Exception if the FXML file cannot be loaded.
     */
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/gui/ClientGUIHomePage.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
        primaryStage.setTitle("Update subscriber");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Handles the click event for the "Update" button.
     * Validates the email and phone fields, and if valid, sends the updated details.
     * Displays a confirmation message once the update is complete.
     *
     * @param event the action event triggered by the update button.
     * @throws IOException if there is an issue with the server connection.
     */
    public void getUpdatedBtn(ActionEvent event) throws IOException {
        String email = Email.getText();
        String phone = Phone.getText();
        Alert alert = new Alert(Alert.AlertType.WARNING);
        
        if (email.isEmpty() || phone.isEmpty()) {
            alert.setTitle("Missing Fields");
            alert.setHeaderText("Some fields are missing");
            alert.setContentText("Please fill in the following fields");
            alert.showAndWait();
            return;
        }
        
        if (email.indexOf("@") == -1) {
            alert.setTitle("Error");
            alert.setContentText("Please enter a valid email");
            alert.showAndWait();
            return;
        }
        
        String cleanedText = phone.replaceAll("[^\\d]", "");
        if (cleanedText.length() != 10 || !cleanedText.matches("\\d+")) {
            alert.setTitle("Error");
            alert.setContentText("Please enter a valid phone number");
            alert.showAndWait();
            return;
        }
        
        ClientGUIConnectionController.chat.accept("update", subId, Phone.getText(), Email.getText());
        afterUpdate.setText("Updated");
    }

    /**
     * Handles the click event for the "Exit" button.
     * Exits the client application.
     *
     * @param event the action event triggered by the exit button.
     * @throws IOException if there is an issue with exiting.
     */
    public void getExitBtn(ActionEvent event) throws IOException {
        System.out.println("Exit client");
        System.exit(0);
    }

    /**
     * Handles the click event for the "Return" button.
     * Navigates the user back to the home page.
     *
     * @param event the action event triggered by the return button.
     * @throws IOException if there is an issue with loading the home page scene.
     */
    public void getReturnBtn(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        
        ((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary window
        Stage primaryStage = new Stage();
        Pane root = loader.load(getClass().getResource("/gui/ClientGUIHomePageController.fxml").openStream());
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
        primaryStage.setTitle("Home page");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
