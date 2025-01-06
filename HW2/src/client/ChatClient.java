// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  public static Subscriber1 s1 = new Subscriber1(0,"",0,"","");
  public static ArrayList<String> activityHistory;
  public static ArrayList<String> borrowHistory;
  public static LinkedHashSet<String> ActionDateAndDeadline;
  public static Boolean bool;
  public static Boolean isFrozen;
  public static Boolean isAvailable;
  public static Boolean isCan;
  public static boolean awaitResponse = false;
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
	  awaitResponse = false;
	  if (msg instanceof Boolean) {
		 bool = (Boolean)msg;
	  }
	  else if (msg instanceof String) { 
		  String returned = (String) msg; //returned from the server

		  switch (returned) {
		      case "frozen":
		          isFrozen = true;
		          break;
		      case "notFrozen":
		          isFrozen = false;
		          break;
		      case "available":
		          isAvailable = true;
		          break;
		      case "notAvailable":
		          isAvailable = false;
		          break;
		      case "can":
		          isCan = true;
		          break;
		      case "can't":
		          isCan = false;
		          break;
		      default:
		          System.out.println("Unexpected status: " + returned);
		          break;
		  }

	  }
	  else if (msg instanceof ArrayList) {
		  
		    ArrayList<String> receivedHistory = (ArrayList<String>) msg;

		    // Check if it's activity or borrow history based on the marker in the string
		    if (receivedHistory.size() > 0) {
		        String firstEntry = receivedHistory.get(0);  // Get the first element to check the type
		        System.out.println(firstEntry);
		        if (firstEntry.contains("Action")) {
		            activityHistory = receivedHistory;  // Process as activity history
		        } else   {
		            borrowHistory = receivedHistory;  // Process as borrow history
		        }
		    }
		}
	  
	  else if (msg instanceof LinkedHashSet) {
		  ActionDateAndDeadline = (LinkedHashSet<String>)msg;
	  }
	  else {
		  Subscriber1 sub = (Subscriber1)msg;
			 if (sub.equals(null)) {
				 s1 = new Subscriber1(0,"",0,"","");
			 }
			 else {
				 s1.setSubscriber_id(sub.getSubscriber_id());
				 s1.setSubscriber_name(sub.getSubscriber_name());
				 s1.setDetailed_subscription_history(sub.getDetailed_subscription_history());
				 s1.setSubscriber_phone_number(sub.getSubscriber_phone_number());
				 s1.setSubscriber_email(sub.getSubscriber_email()); 
			 }
	  }
	 
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param arr1 The message from the UI.    
   */
  @SuppressWarnings("unchecked")
public void handleMessageFromClientUI(Object obj)  //changed from ArrayList<Object> to Object
  {
	if (obj instanceof ArrayList<?>) {
		ArrayList<Object> arr1 = (ArrayList<Object>)obj;
		//int needWait = (Integer)arr1.get(0);
		try
		{
			awaitResponse = true;
			//if (needWait==7) //dont need to wait for response from the server
				//awaitResponse=false;
			sendToServer(arr1);
			while (awaitResponse) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		
		}
		catch(IOException e)
		{
		  clientUI.display
		    ("Could not send message to server.  Terminating client.");
		  quit();
		}
	}
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  
  public void method() {
	  System.out.println("doroty");
  }
}
//End of ChatClient class
