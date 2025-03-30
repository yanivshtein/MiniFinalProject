// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;

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
	public static ArrayList<String> FullBorrowRep, FullStatusRep;
	public static Boolean bool, isFrozen, isAvailable, isCan, isExist, isSeven, orderExists;
	public static boolean awaitResponse = false;
	public static ArrayList<String> ActionDateAndDeadline = new ArrayList<>();
	public static Integer bookAvailability = 0, subID;
	public static String deadlineDate = "";
	public static String statusSub = "";
	public static boolean connected;
	public static ArrayList<String> orders = new ArrayList<>();
	public static ArrayList<String> allbooks = new ArrayList<>();
	public static ArrayList<String> borrowedBooks = new ArrayList<>();
	public static ArrayList<String> filteredBooks = new ArrayList<>();
	public static ArrayList<String> subMessages = new ArrayList<>();
	public static ArrayList<String> libMessages = new ArrayList<>();
	public static ArrayList<String> booksNearDeadline = new ArrayList<>();
	public static ArrayList<String> subCurrentBorrowedBooks = new ArrayList<>();
	public static String bookName;
	public static Subscriber1 sub1;
	public static Librarian lib;
	public static int SubCnt;
	public static String lostBook = "";
	// Constructors ****************************************************

	/**
	 * Constructs an instance of the chat client.
	 *
	 * @param host     The server to connect to.
	 * @param port     The port number to connect on.
	 * @param clientUI The interface type variable.
	 * @throws IOException
	 */

	public ChatClient(String host, int port, ChatIF clientUI) throws IOException {
		super(host, port); // Call the superclass constructor
		connected = false;
		this.clientUI = clientUI;
		Thread connectionThread = new Thread(() -> {

			System.out.println("Attempting to connect to " + host + ":" + port);
			try {

				openConnection();
				connected = true;
			} catch (IOException e) {

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
			if (!connected)
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
	    // Reset the await response flag as the server has responded
	    awaitResponse = false;

	    // Variables to handle server response
	    int request;
	    isExist = true;
	    isSeven = true;
	    ArrayList<Object> arr = null;

	    // Cast the message to an ArrayList if applicable
	    if (msg instanceof ArrayList<?>) {
	        arr = (ArrayList<Object>) msg;
	    }

	    // Extract the request type from the response
	    request = (int) arr.get(0);

	    // Handle different request types based on the server response
	    switch (request) {
	        case 1:
	        case 2:
	            // Populate subscriber details
	            Subscriber1 sub = (Subscriber1) arr.get(1);
	            s1.setSubscriber_id(sub.getSubscriber_id());
	            s1.setSubscriber_name(sub.getSubscriber_name());
	            s1.setSubscriber_phone_number(sub.getSubscriber_phone_number());
	            s1.setSubscriber_email(sub.getSubscriber_email());
	            s1.setSub_status(sub.getSub_status());
	            s1.setPassword(sub.getPassword());
	            break;
	        case 3:
	            // Assign the librarian object
	            lib = (Librarian) arr.get(1);
	            break;
	        case 4:
	            // Assign a subscriber object
	            sub1 = (Subscriber1) arr.get(1);
	            break;
	        case 5:
	            // Handle frozen account status
	            if ("NOT_FOUND".equals(arr.get(1))) {
	                isFrozen = null;
	            } else if (arr.get(1).equals("frozen")) {
	                isFrozen = true;
	            } else {
	                isFrozen = false;
	            }
	            break;
	        case 6:
	            // Handle book availability status
	            if (arr.get(1).equals("available")) {
	                isAvailable = true;
	            } else if (arr.get(1).equals("notAvailable")) {
	                isAvailable = false;
	            } else {
	                isExist = false;
	            }
	            break;
	        case 7:
	            // Handle borrowing permission
	            isCan = arr.get(1).equals("can");
	            break;
	        case 9:
	            // Assign activity history
	            activityHistory = (ArrayList<String>) arr.get(1);
	            break;
	        case 10:
	            // Handle boolean flag for specific operation
	            bool = (Boolean) arr.get(1);
	            break;
	        case 11:
	            // Assign full borrowing report
	            FullBorrowRep = (ArrayList<String>) arr.get(1);
	            break;
	        case 12:
	            // Assign borrowed books list
	            borrowedBooks = (ArrayList<String>) arr.get(1);
	            break;
	        case 13:
	            // Handle generic boolean flag
	            bool = (Boolean) arr.get(1);
	            break;
	        case 14:
	            // Handle book availability status and deadline
	            Integer bookAvailabilitytmp = (Integer) arr.get(1);
	            if (bookAvailabilitytmp.equals(0)) {
	                bookAvailability = 0;
	                deadlineDate = (String) arr.get(2);
	            } else if (bookAvailabilitytmp.equals(-1)) {
	                bookAvailability = -1;
	            } else if (bookAvailabilitytmp > 0) {
	                bookAvailability = bookAvailabilitytmp;
	            } else {
	                bookAvailability = -2;
	            }
	            break;
	        case 15:
	            // Handle subscription status and boolean flag
	            bool = (Boolean) arr.get(1);
	            statusSub = (String) arr.get(2);
	            break;
	        case 16:
	        case 17:
	            // Generic boolean flag handling
	            bool = (Boolean) arr.get(1);
	            break;
	        case 18:
	            // Assign all books list
	            allbooks = (ArrayList<String>) arr.get(1);
	            break;
	        case 19:
	            // Assign full status report
	            FullStatusRep = (ArrayList<String>) arr.get(1);
	            break;
	        case 20:
	            // Handle book name and related flag
	            bool = (Boolean) arr.get(1);
	            bookName = (String) arr.get(2);
	            break;
	        case 21:
	            // Assign action date and deadline
	            ActionDateAndDeadline = (ArrayList<String>) arr.get(1);
	            break;
	        case 22:
	            // Handle frozen account based on additional info
	            bool = (Boolean) arr.get(1);
	            isFrozen = ((String) arr.get(2)).equals("FROZEN");
	            break;
	        case 23:
	            // Assign book name
	            bookName = (String) arr.get(1);
	            break;
	        case 24:
	            // Handle specific conditions for "isSeven" and "orderExists"
	            if (arr.get(1).equals("more than 7")) {
	                isSeven = false;
	            } else if (arr.get(1).equals("order exists")) {
	                orderExists = true;
	            } else {
	                orderExists = false;
	            }
	            break;
	        case 25:
	            // Handle filtering of books
	            try {
	                ArrayList<String> foundBooks = (ArrayList<String>) arr.get(1);
	                filteredBooks = foundBooks;
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	            break;

	        case 27:
	            // Assign list of orders
	            orders = (ArrayList<String>) arr.get(1);
	            break;
	        case 28:
	            // Empty case (possibly a placeholder)
	            break;
	        case 29:
	            // Assign subscriber messages
	            subMessages = (ArrayList<String>) arr.get(1);
	            break;
	        case 30:
	            // Assign librarian messages
	            libMessages = (ArrayList<String>) arr.get(1);
	            break;
	        case 31:
	            // Assign books near deadline
	            booksNearDeadline = (ArrayList<String>) arr.get(1);
	            break;
	        case 32:
	            // Assign subscriber count
	            SubCnt = (int) arr.get(1);
	            break;
	        case 33:
	        	lostBook = (String) arr.get(1);
	        	break;
	        	
	    }
	    
	}
	

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
			try {
				awaitResponse = true;
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