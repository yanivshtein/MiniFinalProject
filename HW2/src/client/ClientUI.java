package client;
import javafx.application.Application;

import javafx.stage.Stage;

import gui.ClientGUIConnectionController;

/**
 * The ClientUI class is responsible for launching the client-side graphical user interface (GUI) application
 * This class extends the JavaFX Application class and overrides the start method to initialize the client UI.
 */
public class ClientUI extends Application {

    public static String hostIp;  
    /**
     * Main method to launch the client application.
     * @param args Command line arguments passed to the application.
     * @throws Exception If an error occurs during application launch
     */
    public static void main( String args[] ) throws Exception
       { 
          launch(args);
       } // end main

    /**
     * The start method is called when the application is launched.
     * @param primaryStage The primary stage for the client GUI.
     * @throws Exception If an error occurs while initializing the client connection screen.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        ClientGUIConnectionController aFrame = new ClientGUIConnectionController(); 
        aFrame.start(primaryStage);
    }
}
