package gui;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import client.ChatClient;
import client.ClientConsole;
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
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import server.EchoServer;
import server.ServerUI;

public class ClientGUILoginController {

	public static String id;
	@FXML
	private Button exit = null;

	@FXML
	private Button send = null;
	
	@FXML
	private Button connect = null;

	@FXML
	private TextField subID = null;
	
	@FXML
	private TextField serverIP = null;
	
	@FXML
	private TextField status = null;
	
	
	@FXML
	private DialogPane alertMsg = null;
	
	public static ClientConsole chat;


	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/gui/ClientGUILogin.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/ClientGUILogin.css").toExternalForm());
		primaryStage.setTitle("Client GUI");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	// This method is called on button click
    public void getSendBtn(ActionEvent event) throws IOException, InterruptedException {
        FXMLLoader loader = new FXMLLoader();

        id = subID.getText();
        if (id.trim().isEmpty()) {

            System.out.println("You must enter an id number");
        } else {

            chat.accept("search", id, "", "");
            if (ChatClient.bool==false) {
                alertMsg.setContentText("The ID does not exist!");
            }
            else {
                System.out.println("Subscriber ID Found");
                ((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary window
                Stage primaryStage = new Stage();
                Pane root = loader.load(getClass().getResource("/gui/ClientGUISecond.fxml").openStream());

                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/gui/ClientGUISecond.css").toExternalForm());
                primaryStage.setTitle("Client Second GUI");

                primaryStage.setScene(scene);
                primaryStage.show();
            }

        }

    }

	public void getExitBtn(ActionEvent event) {
		System.out.println("exit");
		System.exit(0);
	}
	public void getConnectBtn(ActionEvent event) {
		 chat= new ClientConsole(serverIP.getText(), 5555);
		if(chat.connected) {
			status.setText("connected");
		}
	}

}