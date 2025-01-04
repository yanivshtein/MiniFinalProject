package gui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ClientGUIHomePageController {

	
	@FXML
	private Button exit = null;

	@FXML
	private Button send = null;
	
	@FXML
	private Label subName = null;
	
	@FXML
	private void initialize() {
		subName.setText(ClientGUILoginController.id);
	}
	
	
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/gui/ClientGUIHomePage.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/ClientGUIHomePage.css").toExternalForm());
		primaryStage.setTitle("Login Screen");
		primaryStage.setScene(scene);
		
		primaryStage.show();
	}
	
	
	public void getUpdateDetails(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary window
        Stage primaryStage = new Stage();
        Pane root = loader.load(getClass().getResource("/gui/ClientGUIUpdateDetails.fxml").openStream());

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/ClientGUIUpdateDetails.css").toExternalForm());
        primaryStage.setTitle("Client Second GUI");

        primaryStage.setScene(scene);
        primaryStage.show();
		
	}
	
	public void getViewHistory(ActionEvent event) throws IOException {
	    // Hiding primary window
	    ((Node) event.getSource()).getScene().getWindow().hide();
	    
	    // Loading FXML and setting up the new stage
	    FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ViewHistoryGUI.fxml"));
	    Parent root = loader.load();
	    
	    Scene scene = new Scene(root);
	    scene.getStylesheets().add(getClass().getResource("/gui/ViewHistoryGUI.css").toExternalForm());
	    
	    Stage primaryStage = new Stage();
	    primaryStage.setTitle("View History");
	    primaryStage.setScene(scene);
	    primaryStage.show();
	}

	

	
	
	public void getExitBtn(ActionEvent event) {
		System.out.println("exit");
		System.exit(0);
	}
}
