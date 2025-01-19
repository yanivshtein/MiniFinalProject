package gui;

import java.io.IOException;

import client.ClientConsole;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ClientGUIConnectionController {

	@FXML
	private Button connect = null;

	@FXML
	private TextField serverIP = null;

	@FXML
	private Button exit = null;

	public static ClientConsole chat;

	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/gui/ClientGUIConnection.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
		primaryStage.setTitle("Client Connection");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public void getConnectBtn(ActionEvent event) throws IOException {
		Alert alert = new Alert(Alert.AlertType.WARNING);
		if (serverIP.getText().trim().isEmpty()) {
			alert.setTitle("Error");
			alert.setContentText("Please enter IP");
			alert.showAndWait();
		} else {
			try {
				chat = new ClientConsole(serverIP.getText(), 5555);
				if (chat.connected) {
					FXMLLoader loader = new FXMLLoader();
					((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary window
					Stage primaryStage = new Stage();
					Pane root = loader.load(getClass().getResource("/gui/ClientGUILogin.fxml").openStream());

					Scene scene = new Scene(root);
					scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
					primaryStage.setTitle("Login Page");

					primaryStage.setScene(scene);
					primaryStage.show();

				}
			} catch (Exception e) {
				alert.setTitle("Error");
				alert.setContentText("Wrong IP");
				alert.showAndWait();
			}
		}
	}

	public void getExitBtn(ActionEvent event) {
		System.out.println("exit");
		System.exit(0);
	}
}