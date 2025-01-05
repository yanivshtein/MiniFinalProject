package client;

import gui.AddSubscriberGUIController;
import gui.SubscriberOrderUIController;
import javafx.application.Application;
import javafx.stage.Stage;

public class AddSubscriberUI  extends Application {
	public static ClientConsole chat; //only one instance

	public static void main( String args[] ) throws Exception
	   { 
		    launch(args);  
	   } // end main
	 
	@Override
	public void start(Stage primaryStage) throws Exception {
		 chat= new ClientConsole("localhost", 5555);
		// TODO Auto-generated method stub
						  		
		 AddSubscriberGUIController aFrame = new AddSubscriberGUIController(); 
		 
		aFrame.start(primaryStage);
	}
}
