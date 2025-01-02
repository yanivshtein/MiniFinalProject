package client;
import javafx.application.Application;

import javafx.stage.Stage;

import gui.ReportsGUI;
import gui.SubscriberOrderUIController;


public class OrderUI extends Application {
	public static ClientConsole chat; //only one instance

	public static void main( String args[] ) throws Exception
	   { 
		    launch(args);  
	   } // end main
	 
	@Override
	public void start(Stage primaryStage) throws Exception {
		 chat= new ClientConsole("localhost", 5555);
		// TODO Auto-generated method stub
						  		
		SubscriberOrderUIController aFrame = new SubscriberOrderUIController(); 
		 
		aFrame.start(primaryStage);
	}
}
