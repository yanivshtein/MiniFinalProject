package client;
import javafx.application.Application;

import javafx.stage.Stage;

import gui.ClientGUILandController;


public class ClientUI extends Application {
	public static ClientConsole chat; //only one instance

	public static void main( String args[] ) throws Exception
	   { 
		    launch(args);  
	   } // end main
	 
	@Override
	public void start(Stage primaryStage) throws Exception {
		 chat= new ClientConsole("localhost", 5555);
		// TODO Auto-generated method stub
						  		
		ClientGUILandController aFrame = new ClientGUILandController(); 
		 
		aFrame.start(primaryStage);
	}
}
