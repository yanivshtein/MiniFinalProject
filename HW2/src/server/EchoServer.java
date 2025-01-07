// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 
package server;

import java.io.*;
import java.net.ServerSocket;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import common.Subscriber1;
import gui.ServerGUI;
import javafx.stage.Stage;
import logic.ClientInfo;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer 
{
	private List<ConnectionListener> listeners = new ArrayList<>();
	 
	private String subscriberID ;
	private String bookName ;
	private boolean returnLate;
	private boolean freeze;
	//Interface to notify about connections
	 public interface ConnectionListener {
	     void onClientConnected(ClientInfo c);
	     void onClientDisconnected(ClientInfo clientInfo);
	 }
	 
	 public void addConnectionListener(ConnectionListener listener) {
	     listeners.add(listener);
	 }
	 
	 public void removeConnectionListener(ConnectionListener listener) {
	     listeners.remove(listener);
	 }
	 
	 
	
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port) 
  {
    super(port);
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */


    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
	  	mysqlConnection.connectToDB();

    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  
  
  @SuppressWarnings("unchecked")
  protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
	    Subscriber1 sub = null;

	    if (msg instanceof ArrayList<?>) {
	        ArrayList<Object> arr = (ArrayList<Object>) msg;
	        int request = (Integer) arr.get(0);

	        switch (request) { //go to DB controller based on the request
	            case 1: // UPDATE
	                mysqlConnection.update((String) arr.get(1), (String) arr.get(2), (String) arr.get(3));
	                try {
	                    client.sendToClient(new Subscriber1()); // send null only to call the client so the awaitResponse will be false
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	                break;

	            case 2: // SELECT
	                sub = mysqlConnection.select((String) arr.get(1));
	                try {
	                    client.sendToClient(sub); // sent to the client
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	                break;

	            case 4: // Search the id to check if the subscriber exists
	                Boolean ret = mysqlConnection.searchId((String) arr.get(1));
	                try {
	                    client.sendToClient(ret);
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	                break;

	            case 5: // Check subscriber's status
	                String subID = (String) arr.get(1);
	                String retStatus = "frozen"; // for the example
	                // go to subscriber's DB and return the status of subID (subscriber's id)
	                try {
	                    client.sendToClient(retStatus); // send back to the client if the status is frozen or not
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	                break;

	            case 6: // Check book availability
	                String bookName = (String) arr.get(2);
	                String retAvailability = "available"; // for the example
	                // go to book's DB and check if there is an available copy
	                try {
	                    client.sendToClient(retAvailability); // send back to the client if the book is available
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	                break;

	            case 7: // Add an order
	                String subIdOrder = (String) arr.get(1);
	                String bookNameOrder = (String) arr.get(2);
	             // go to orders table in the DB and check if can add a column (if the number of orders is less than the number of copys)
	                String canAdd = mysqlConnection.canAddOrder(subIdOrder, bookNameOrder); 
	                try {
	                    client.sendToClient(canAdd); 
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
                	break;
	            case 8: //watch activity history
	            	String subscriberID = (String)arr.get(1); //subscriber ID is in the second position of the array
	          	  	
	            	// Retrieve the borrow history for the given subscriber ID
	          	    ArrayList<String> borrowHistory = mysqlConnection.getBorrowHistory(subscriberID);
	          	  try {
	        	        // Send the borrow history to the client
	        	        client.sendToClient(borrowHistory);
	        	    } catch (IOException e) {
	        	        e.printStackTrace();
	        	    }
	            case 9:	            		          	  
		          	String subscriberId = (String)arr.get(1); //subscriber ID is in the second position of the array
	        	    
	        	    // Retrieve the activity history for the given subscriber ID
	        	    ArrayList<String> activityHistory = mysqlConnection.getActivityHistory(subscriberId);
	        	    
	        	    try {
	        	        // Send the activity history to the client
	        	        client.sendToClient(activityHistory);
	        	    } catch (IOException e) {
	        	        e.printStackTrace();
	        	    }
	            case 10:
	            	String subscriberid = (String)arr.get(1); //subscriber ID is in the second position of the array
	          	  String BookName = (String)arr.get(2);
	          	  String OldDate = (String)arr.get(3);
	          	  String NewDate = (String)arr.get(4);

	          	  boolean UpdateDate = mysqlConnection.ChangeReturnDate(subscriberid , BookName , OldDate ,NewDate);
	          	 
	          	  
	            case 19:	// search if exist borrower in the DB
	            	String borrowerid = (String)arr.get(1); //subscriber ID is in the second position of the array
		          	String bookname = (String)arr.get(2);
				try {
					boolean isExist= mysqlConnection.checkIfBorrowerFound(borrowerid, bookname);
					client.sendToClient(isExist);
				} catch (SQLException | IOException e) {
					
					e.printStackTrace();
				}
		          	
	            case 20:
	            	LinkedHashSet<String> Dates= new LinkedHashSet<String>();
	            	String Borrowerid = (String)arr.get(1); //subscriber ID is in the second position of the array
		          	String Bookname = (String)arr.get(2);
				try {
					Dates=mysqlConnection.getBorrowDateAndReturnDate(Borrowerid, Bookname);
					client.sendToClient(Dates);
				} catch (SQLException | IOException e) {
					
					e.printStackTrace();
				}
				
	            case 21:
	            	 this.subscriberID = (String)arr.get(1);
	            	 this.bookName = (String)arr.get(2);          	 
	            	 returnLate = (boolean) arr.get(4);
	            	 freeze = (boolean)arr.get(5);
	            	 boolean bookIncrement = false;
	            	 boolean freezeSuccess = false;
	            	 boolean insertRowToActivity = false;
	            	 
	            	 try {
	            		 if(returnLate==false && freeze==false) {
		            		 insertRowToActivity = mysqlConnection.insertReturnBookRowInActivityHistory(this.subscriberID, this.bookName,0);

		            	 }
	            		 if(returnLate==true) {
		            		 insertRowToActivity = mysqlConnection.insertReturnBookRowInActivityHistory(this.subscriberID, this.bookName,1);

		            	 }
		            	 if(freeze==true){
		            		 freezeSuccess = mysqlConnection.updateSubscriberStatusToFrozen(this.subscriberID, this.bookName);
		            	 }
		            	
		            	 bookIncrement = mysqlConnection.incrimentBookAvailability(this.bookName);
		           	 
		            	 client.sendToClient(bookIncrement || freezeSuccess || insertRowToActivity );

	            	 } catch (SQLException | IOException e) {
					
	            		 e.printStackTrace();
	            	 }
	            	 
	            default:
	                System.out.println("The server - Received message is not of the expected type.");
	                break;
	                
	           
	        }
	    }
	}


  @Override
  protected void clientConnected(ConnectionToClient client) {
      // Log the client's IP address when they connect
      String clientInfo = client.getInetAddress().getHostAddress();
      String name = client.getInetAddress().getHostName();
      ClientInfo c = new ClientInfo(clientInfo, name);
      
   // Notify all listeners about the new connection
      for (ConnectionListener listener : listeners) {
          listener.onClientConnected(c);
      }
  }
  
  @Override
  protected void clientDisconnected(ConnectionToClient client) {
      try {
          // Retrieve client's IP and hostname
          String clientIp = client.getInetAddress().getHostAddress();
          String clientHostName = client.getInetAddress().getHostName();
          ClientInfo clientInfo = new ClientInfo(clientIp, clientHostName);

          // Notify listeners of the disconnection
          for (ConnectionListener listener : listeners) {
              listener.onClientDisconnected(clientInfo);
          }
      } catch (Exception e) {
          System.out.println("Error during client disconnection: " + e.getMessage());
      }
  }  
}


