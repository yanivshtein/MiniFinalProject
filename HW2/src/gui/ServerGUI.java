package gui;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logic.ClientInfo;
import server.EchoServer;
import server.ServerUI;

/**
 * Controller class for the Server GUI.
 * Provides functionality to run the server, display connection information, and handle server-related actions.
 */
public class ServerGUI {

    @FXML
    private Button show = null;
    
    @FXML
    private Button exit = null;

    @FXML
    private TextField ip = null;

    @FXML
    private TextField hostName = null;

    @FXML
    private TextField connStatus = null;
    
    @FXML
    private Label serverRun = null;

    /**
     * Starts the Server GUI by loading the FXML layout and displaying it in a new window.
     *
     * @param primaryStage the primary stage for this application.
     * @throws Exception if an error occurs while loading the FXML file or initializing the scene.
     */
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/gui/ServerGUI.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/AppCss.css").toExternalForm());
        primaryStage.setTitle("Server GUI");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Runs the server on the default port (5555) and updates the GUI to display server status.
     * Adds listeners to monitor client connection and disconnection events.
     * 
     * @param event the action event triggered by clicking the "Run Server" button.
     */
    public void runServer(ActionEvent event) {
        String port = "5555"; // Default port
        serverRun.setText("Server is listening...");
        EchoServer sv = ServerUI.runServer(port);
        
        // Add listener for connection events
        sv.addConnectionListener(new EchoServer.ConnectionListener() {
            @Override
            public void onClientConnected(ClientInfo clientInfo) {
                ip.setText(clientInfo.getIpAddress());
                hostName.setText(clientInfo.getHostName());
                connStatus.setText("Connected");
                System.out.println("Connection established: IP = " + clientInfo.getIpAddress() + ", Host = " + clientInfo.getHostName());                
            }

            @Override
            public void onClientDisconnected(ClientInfo clientInfo) {
                ip.setText("");
                hostName.setText("");
                connStatus.setText("Disconnected");
                System.out.println("Connection lost: IP = " + clientInfo.getIpAddress() + ", Host = " + clientInfo.getHostName());
            }
        });
        sv.time(); // call the method time in sv (Echo Server) to check all the actions that use time
        
    }
    
    /**
     * Handles the action for the "Exit" button.
     * Terminates the application.
     *
     * @param event the action event triggered by clicking the "Exit" button.
     */
    public void getCloseBtn(ActionEvent event) {
    	System.out.println("Exit");	
		System.exit(0);
    }

    
}
