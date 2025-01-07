package client;

import gui.BorrowBookGUIController;
import javafx.application.Application;
import javafx.stage.Stage;

public class BorrowBookUI  extends Application {
    public static ClientConsole chat; //only one instance

    public static void main( String args[] ) throws Exception
       { 
            launch(args);
       } // end main

    @Override
    public void start(Stage primaryStage) throws Exception {
         chat= new ClientConsole("localhost", 5555);
        // TODO Auto-generated method stub

         BorrowBookGUIController aFrame = new BorrowBookGUIController(); 

        aFrame.start(primaryStage);
    }
}