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
  public static Subscriber1 s1 = new Subscriber1(0, "", "", "", "", "");
  public static ArrayList<String> activityHistory;
  public static ArrayList<String> borrowHistory;
  public static ArrayList<String> FullBorrowRep, FullStatusRep;
  public static ArrayList<String> ActionDateAndDeadline;
  public static Boolean bool, isFrozen, isAvailable, isCan, isExist;
  public static boolean awaitResponse = false;
  public static boolean connected;
  public static Integer bookAvailability=0;
  public static ArrayList<String> allbooks = new ArrayList<>();
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
	  super(host, port); // Call the superclass constructor
		connected = false;
		this.clientUI = clientUI;
		Thread connectionThread = new Thread(() -> {
	        
	            System.out.println("Attempting to connect to " + host + ":" + port);
		try {
			
			openConnection();
			connected = true;
		}catch(IOException e) {
			
			System.out.println("Connection failed: " + e.getMessage());
			connected = false;
			
		}
		});
		connectionThread.start();
		
		try {
	        connectionThread.join(1000); // Wait for the thread to finish within the timeout
	        if (connectionThread.isAlive()) {
	            System.out.println("Connection attempt timed out.");
	            connectionThread.interrupt(); // Stop the thread if it's still running
	        }
	        if(!connected)
          	throw new IOException();
	    } catch (InterruptedException e) {
	        System.out.println("Error waiting for connection thread: " + e.getMessage());
	    }
  }

  
  @SuppressWarnings("unchecked")
  public void handleMessageFromServer(Object msg) 
  {
	  
	  awaitResponse = false;
		int request;
		isExist = true;
		ArrayList<Object> arr = null;
		if (msg instanceof ArrayList<?>) {
			arr = (ArrayList<Object>) msg;
		}
		request = (int) arr.get(0);	
		
		switch (request) {
		case 1:
		case 2:
			Subscriber1 sub = (Subscriber1) arr.get(1);
			if (sub.equals(null)) {
				s1 = new Subscriber1(0, "", "", "", "", "");
			} else {
				s1.setSubscriber_id(sub.getSubscriber_id());
				s1.setSubscriber_name(sub.getSubscriber_name());
				s1.setSubscriber_phone_number(sub.getSubscriber_phone_number());
				s1.setSubscriber_email(sub.getSubscriber_email());
				s1.setSub_status(sub.getSub_status());
				s1.setPassword(sub.getPassword());
			}
			break;
		case 3:
		case 4:
			bool = (Boolean)arr.get(1);
			break;
		case 5:
			if (arr.get(1).equals("frozen")) 
				isFrozen=true;
			else 
				isFrozen=false;
			break;
		case 6:
			if (arr.get(1).equals("available"))
				isAvailable=true;
			else if(arr.get(1).equals("notAvailable"))
				isAvailable=false;
			else 
				isExist=false;
			break;
		case 7:
			if (arr.get(1).equals("can"))
				isCan=true;
			else
				isCan=false;
			break;
		case 8: 
			borrowHistory = (ArrayList<String>) arr.get(1);
			break;
		case 9:
			activityHistory = (ArrayList<String>) arr.get(1);
			break;
		case 10:
			bool=(Boolean) arr.get(1);
			break;
		case 11:
			FullBorrowRep = (ArrayList<String>) arr.get(1);
			break;
		case 13:
			bool=(Boolean) arr.get(1);
			break;
		case 14:
			Integer bookAvailabilitytmp = (Integer)arr.get(1);
			if(bookAvailabilitytmp.equals(0)) {
				bookAvailability = 0;
			}
			else if(bookAvailabilitytmp.equals(-1)) {
				bookAvailability =-1;
			}
			else if(bookAvailabilitytmp>0) {
				bookAvailability =bookAvailabilitytmp;
			}
			else {
				bookAvailability=-2;
			}
			break;
		case 15:
			bool=(Boolean) arr.get(1);
			break;
		case 16:
			bool=(Boolean) arr.get(1);
			break;
		case 17:
			bool=(Boolean) arr.get(1);
			break;
		case 18:
			//all books
			break;
		case 19:
			FullStatusRep = (ArrayList<String>) arr.get(1);
			break;
		case 20:
			bool=(Boolean) arr.get(1);
			break;
			
		case 21:
			ActionDateAndDeadline = (ArrayList<String>)arr.get(1);
			break;
			
		case 22:
			bool=(Boolean) arr.get(1);
			break;
	
		}
		
	  
	  //**************** that is before
//	  awaitResponse = false;
//	  if (msg instanceof Boolean) {
//		 bool = (Boolean)msg;
//	  }
//	  else if (msg instanceof String) { 
//		  String returned = (String) msg; //returned from the server
//		  isExist = true;
//		  switch (returned) {
//		      case "frozen":
//		          isFrozen = true;
//		          break;
//		      case "notFrozen":
//		          isFrozen = false;
//		          break;
//		      case "available":
//		          isAvailable = true;
//		          break;
//		      case "notAvailable":
//		          isAvailable = false;
//		          break;
//		      case "can":
//		          isCan = true;
//		          break;
//		      case "can't":
//		          isCan = false;
//		          break;
//		      case "notExist":
//		    	  isExist = false;
//		    	  break;
//		      default:
//		          System.out.println("Unexpected status: " + returned);
//		          break;
//		  }
//
//	  }
//
////	  else if (msg instanceof ArrayList<?>) {
////		  bool=(Boolean) ( (ArrayList<Boolean>)msg).get(0);
////		  System.out.println("Here:");
////	  }
////		    ArrayList<String> receivedHistory = (ArrayList<String>) msg;
//	  else if (msg instanceof ArrayList) {
//			allbooks =(ArrayList<String>) msg;  //
//			System.out.println(allbooks +"chatclient");
//			ArrayList<String> receivedHistory = (ArrayList<String>) msg;
//
//			// Check if it's activity or borrow history based on the marker in the string
//			if (receivedHistory.size() > 0) {
//				String firstEntry = receivedHistory.get(0); // Get the first element to check the type
//
//				if (firstEntry.contains("Borrow Report")) {
//					FullBorrowRep = receivedHistory;
//				} else if (firstEntry.contains("Action")) {
//					activityHistory = receivedHistory; // Process as activity history
//				} else {
//					borrowHistory = receivedHistory; // Process as borrow history
//				}
//			}
//			System.out.println(allbooks +"chatclient2");
//		}
//	  else if (msg instanceof Integer) {
//			Integer bookAvailabilitytmp = (Integer)msg;
//			if(bookAvailabilitytmp.equals(0)) {
//				bookAvailability = 0;
//			}
//			else if(bookAvailabilitytmp.equals(-1)) {
//				bookAvailability =-1;
//			}
//			else if(bookAvailabilitytmp>0) {
//				bookAvailability =bookAvailabilitytmp;
//			}
//			else {
//				bookAvailability=-2;
//			}
//			
//			
//		}
//	  else if (msg instanceof LinkedHashSet<?>) {
//			  ActionDateAndDeadline = (LinkedHashSet<String>)msg;
//		  
//	  }
//	  else {
//		  Subscriber1 sub = (Subscriber1) msg;
//			if (sub.equals(null)) {
//				s1 = new Subscriber1(0, "", "", "", "", "");
//			} else {
//				s1.setSubscriber_id(sub.getSubscriber_id());
//				s1.setSubscriber_name(sub.getSubscriber_name());
//				s1.setSubscriber_phone_number(sub.getSubscriber_phone_number());
//				s1.setSubscriber_email(sub.getSubscriber_email());
//				s1.setSub_status(sub.getSub_status());
//				s1.setPassword(sub.getPassword());
//			 }
//	  }
	 
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
			// if (needWait==11) //dont need to wait for response from the server
			// awaitResponse=false;			System.out.println("arr1: " + arr1);
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
	  try {
			closeConnection();
		} catch (IOException e) {
		}
		System.exit(0);
  }
  
  public void method() {
	  System.out.println("doroty");
  }
}
//End of ChatClient class
