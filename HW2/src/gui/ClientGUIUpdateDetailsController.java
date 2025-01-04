package gui;

import java.io.IOException;
import java.util.ArrayList;

import client.ChatClient;
import client.ClientUI;
import common.Subscriber1;
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
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;

public class ClientGUIUpdateDetailsController {
    // FXML Button controls
    @FXML
    private Button show;
    @FXML
    private Button update;
    @FXML
    private Button exitBtn;
    @FXML
    private Button view;
    @FXML
    private Button Return;

    // FXML TextField controls for the first view
    @FXML
    private TextField Phone;
    @FXML
    private TextField Email;
    
    // FXML TextField controls for the second view
    @FXML
    private TextField secondID;
    @FXML
    private TextField secondName;
    @FXML
    private TextField secondHistory;
    @FXML
    private TextField secondPhoneLeft;
    @FXML
    private TextField secondEmailLeft;
    @FXML
    private TextField secondPhoneRight;
    @FXML
    private TextField secondEmailRight;

    // FXML Label controls
    @FXML
    private Label IdName;
    @FXML
    private Label subName;

    // FXML Dialog controls
    @FXML
    private DialogPane afterUpdate;

    @FXML
    private void initialize() {
        ClientGUIConnectionController.chat.accept("select", ClientGUILoginController.id, "", "");
        Subscriber1 sub = ChatClient.s1;
        
        // Initialize fields based on which view is active
        if (IdName != null && subName != null) {
            // First view initialization
            IdName.setText(ClientGUILoginController.id);
            subName.setText(sub.getSubscriber_name());
            Phone.setText(sub.getSubscriber_phone_number());
            Email.setText(sub.getSubscriber_email());
        }
    }

    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/gui/ClientGUIHomePage.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/ClientGUIHomePage.css").toExternalForm());
        primaryStage.setTitle("Client GUI");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void getSelectBtn(ActionEvent event) throws IOException {
        ClientGUIConnectionController.chat.accept("select", ClientGUILoginController.id, "", "");
        Subscriber1 sub = ChatClient.s1;
        secondID.setText(String.valueOf(sub.getSubscriber_id()));
        secondName.setText(sub.getSubscriber_name());
        secondHistory.setText(String.valueOf(sub.getDetailed_subscription_history()));
        secondPhoneLeft.setText(sub.getSubscriber_phone_number());
        secondEmailLeft.setText(sub.getSubscriber_email());
    }

    public void getUpdatedBtn(ActionEvent event) throws IOException {
        // Handle both update scenarios
        if (secondPhoneRight != null && secondEmailRight != null) {
            // Second view update
            afterUpdate.setContentText("Update sent");
            ClientGUIConnectionController.chat.accept("update", ClientGUILoginController.id, 
                secondPhoneRight.getText(), secondEmailRight.getText());
        } else if (Phone != null && Email != null) {
            // First view update
            afterUpdate.setContentText("Updated");
            ClientGUIConnectionController.chat.accept("update", ClientGUILoginController.id, 
                Phone.getText(), Email.getText());
        }
    }

    public void getExitBtn(ActionEvent event) throws IOException {
        System.out.println("Exit client");
        System.exit(0);
    }

    public void getReturnBtn(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        ((Node) event.getSource()).getScene().getWindow().hide();
        Stage primaryStage = new Stage();
        Pane root = loader.load(getClass().getResource("/gui/ClientGUIHomePage.fxml").openStream());

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/ClientGUIHomePage.css").toExternalForm());
        primaryStage.setTitle("Client Second GUI");

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}