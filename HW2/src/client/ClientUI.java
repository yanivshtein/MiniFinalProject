package client;
import javafx.application.Application;

import javafx.stage.Stage;


import java.util.Vector;

import gui.ClientGUILandController;


public class ClientUI extends Application {

    public static String hostIp;

    public static void main( String args[] ) throws Exception
       { 


          launch(args);


       } // end main

    @Override
    public void start(Stage primaryStage) throws Exception {


        // TODO Auto-generated method stub

        ClientGUILandController aFrame = new ClientGUILandController(); 

        aFrame.start(primaryStage);
    }


}
