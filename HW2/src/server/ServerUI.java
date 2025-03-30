package server;


import gui.ServerGUI;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * The main entry point for the server application. This class initializes
 * the server UI and provides functionality to run the server.
 */
public class ServerUI extends Application {
	final public static int DEFAULT_PORT = 5555;

	  /**
     * The main method of the application. Launches the JavaFX application.
     *
     * @param args command-line arguments passed to the application.
     * @throws Exception if an error occurs during the application launch.
     */

	public static void main( String args[] ) throws Exception
	   {
		 launch(args);
	  } // end main

	/**
     * Starts the JavaFX application by initializing and starting the Server GUI.
     *
     * @param primaryStage the primary stage for the JavaFX application.
     * @throws Exception if an error occurs during GUI initialization.
     */
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		ServerGUI aFrame = new ServerGUI(); // create Frame

		aFrame.start(primaryStage);
	}

	 /**
     * Initializes and runs the server on the specified port.
     *
     * @param p the port number as a string.
     * @return an instance of {@link EchoServer} that listens for client connections.
     */
	public static EchoServer runServer(String p)
	{
		 int port = 0; //Port to listen on

	        try
	        {
	        	port = Integer.parseInt(p); //Set port to 5555

	        }
	        catch(Throwable t)
	        {
	        	System.out.println("ERROR - Could not connect!");
	        }

	        EchoServer sv = new EchoServer(port);

	        try
	        {
	          sv.listen(); //Start listening for connections
	        }
	        catch (Exception ex)
	        {
	          System.out.println("ERROR - Could not listen for clients!");
	        }
	        return sv;
	}


}
