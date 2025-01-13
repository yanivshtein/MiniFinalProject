// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;

import java.io.*;
import java.util.ArrayList;

/**
 * This class overrides some of the methods defined in the abstract superclass
 * in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient {
	// Instance variables **********************************************

	/**
	 * The interface type variable. It allows the implementation of the display
	 * method in the client.
	 */
	ChatIF clientUI;
	public static Subscriber1 s1 = new Subscriber1(0, "", "", "", "", "");
	public static ArrayList<String> activityHistory;
	public static ArrayList<String> borrowHistory;
	public static ArrayList<String> FullBorrowRep , FullStatusRep;
	public static Boolean bool, isFrozen, isAvailable, isCan, isExist, isSeven, orderExists;
	public static boolean awaitResponse = false;
	public static ArrayList<String> ActionDateAndDeadline;
	public static Integer bookAvailability=0, subID;
	public static String deadlineDate="";
	public static String statusSub="";
	public static boolean connected;

	public static ArrayList<String> allbooks = new ArrayList<>();
	public static ArrayList<String> borrowedBooks = new ArrayList<>();
	public static ArrayList<String> filteredBooks = new ArrayList<>();
	public static String bookName;
	public static Subscriber1 sub1;
	public static Librarian lib;

	// Constructors ****************************************************

	/**
	 * Constructs an instance of the chat client.
	 *
	 * @param host     The server to connect to.
	 * @param port     The port number to connect on.
	 * @param clientUI The interface type variable.
	 * @throws IOException 
	 */

	public ChatClient(String host, int port, ChatIF clientUI) throws IOException{
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

	// Instance methods ************************************************

	/**
	 * This method handles all data that comes in from the server.
	 *
	 * @param msg The message from the server.
	 */
	@SuppressWarnings("unchecked")
	public void handleMessageFromServer(Object msg) {
		awaitResponse = false;
		int request;
		isExist = true;
		isSeven = true;
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
				System.out.println(s1.getSubscriber_name());
			}
			break;
		case 3:
			lib = (Librarian)arr.get(1);
			break;
		case 4:
			sub1 = (Subscriber1)arr.get(1);
			
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
		case 12:
			borrowedBooks = (ArrayList<String>) arr.get(1);
			break;
		case 13:
			bool=(Boolean) arr.get(1);
			break;
		case 14:
			Integer bookAvailabilitytmp = (Integer)arr.get(1);
			
			if(bookAvailabilitytmp.equals(0)) {
				bookAvailability = 0;
				deadlineDate=(String)arr.get(2);
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
			statusSub =(String)arr.get(2);
			break;
		case 16:
			bool=(Boolean) arr.get(1);
			break;
		case 17:
			bool=(Boolean) arr.get(1);
			break;
		case 18:
			allbooks =(ArrayList<String>) arr.get(1);  
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
		case 23:
			bookName = (String) arr.get(1);
			break;
		case 24:
			if (arr.get(1).equals("more than 7")) 
				isSeven=false;
			else if (arr.get(1).equals("order exists"))
				orderExists=true;
			else 
				orderExists=false;
			break;
		case 25:
		    try {
		        ArrayList<String> foundBooks = (ArrayList<String>) arr.get(1); 
		        
		        filteredBooks = foundBooks;

		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		    break;
		case 26:
			 bool=(Boolean) arr.get(1);
			break;

		}
			
		
	}	
		/*
		if (msg instanceof Boolean) {
			bool = (Boolean) msg;
		} else if (msg instanceof String) {
			String returned = (String) msg; // returned from the server
			isExist = true;
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
			case "notExist":
				isExist = false;
				break;
			default:
				System.out.println("Unexpected status: " + returned);
				break;
			}

		} else if (msg instanceof ArrayList) {
			allbooks =(ArrayList<String>) msg;  //
			System.out.println(allbooks +"chatclient");
			ArrayList<String> receivedHistory = (ArrayList<String>) msg;

			// Check if it's activity or borrow history based on the marker in the string
			if (receivedHistory.size() > 0) {
				String firstEntry = receivedHistory.get(0); // Get the first element to check the type
                if (firstEntry.contains("status report")) {
                	FullStatusRep = receivedHistory;
                }
                else if (firstEntry.contains("borrow report")) {
					FullBorrowRep = receivedHistory;
				} else if (firstEntry.contains("Action")) {
					activityHistory = receivedHistory; // Process as activity history
				} else {
					borrowHistory = receivedHistory; // Process as borrow history
				}
			}
			System.out.println(allbooks +"chatclient2");
		}else if (msg instanceof Integer) {
			id = (Integer)msg;
			Integer bookAvailabilitytmp = (Integer)msg;
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
			
			
		}
		else {
			Subscriber1 sub = (Subscriber1) msg;
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
		}*/

	

	/**
	 * This method handles all data coming from the UI
	 *
	 * @param arr1 The message from the UI.
	 */
	@SuppressWarnings("unchecked")
	public void handleMessageFromClientUI(Object obj) // changed from ArrayList<Object> to Object
	{
		if (obj instanceof ArrayList<?>) {
			ArrayList<Object> arr1 = (ArrayList<Object>) obj;
			// int needWait = (Integer)arr1.get(0);
			try {
				awaitResponse = true;
				// if (needWait==11) //dont need to wait for response from the server
				// awaitResponse=false;
				sendToServer(arr1);
				while (awaitResponse) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			} catch (IOException e) {
				clientUI.display("Could not send message to server.  Terminating client.");
				quit();
			}
		}
	}

	/**
	 * This method terminates the client.
	 */
	public void quit() {
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