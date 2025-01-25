package server;

import java.io.*;
import java.sql.SQLException;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.Librarian;
import common.Subscriber1;
import logic.ClientInfo;
import ocsf.server.*;

/**
 * The EchoServer class extends AbstractServer to provide server
 * functionalities. It handles client connections, disconnections, and maintains
 * listeners for connection events.
 */
public class EchoServer extends AbstractServer {

	mysqlConnection SQLinstance;
	private List<ConnectionListener> listeners = new ArrayList<>();
	private String subEmail;
	private String subscriberID;
	private String bookName;
	private int Sub_id;

	/**
	 * Interface for listening to client connection and disconnection events.
	 */
	public interface ConnectionListener {
		void onClientConnected(ClientInfo c);

		void onClientDisconnected(ClientInfo clientInfo);
	}

	/**
	 * Adds a connection listener to monitor connection events.
	 *
	 * @param listener the {@link ConnectionListener} to be added.
	 */
	public void addConnectionListener(ConnectionListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes a connection listener.
	 *
	 * @param listener the {@link ConnectionListener} to be removed.
	 */
	public void removeConnectionListener(ConnectionListener listener) {
		listeners.remove(listener);
	}

	final public static int DEFAULT_PORT = 5555;

	/**
	 * Constructs an EchoServer instance with the specified port.
	 *
	 * @param port the port number for the server to listen on.
	 */
	public EchoServer(int port) {
		super(port);
		SQLinstance = mysqlConnection.getInstance();
	}

	/**
	 * Called when the server starts successfully and begins listening for
	 * connections.
	 */
	protected void serverStarted() {
		System.out.println("Server listening for connections on port " + getPort());
	}

	/**
	 * Called when the server stops listening for connections.
	 */
	protected void serverStopped() {
		System.out.println("Server has stopped listening for connections.");
	}

	/**
	 * Handles incoming messages from clients and processes requests based on their
	 * type.
	 *
	 * @param msg    the message received from the client. It is expected to be an
	 *               {@link ArrayList}.
	 * @param client the {@link ConnectionToClient} representing the client
	 *               connection.
	 */
	@SuppressWarnings("unchecked")
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		Subscriber1 sub = null;
		Librarian lib;

		if (msg instanceof ArrayList<?>) {
			ArrayList<Object> arr = (ArrayList<Object>) msg;
			int request = (Integer) arr.get(0);
			int subID;
			String bookName;

			ArrayList<Object> arrToSend = new ArrayList<>();
			boolean returnLate;
			boolean freeze;
			switch (request) { // go to DB controller based on the request
			case 1: // UPDATE
				SQLinstance.update((String) arr.get(1), (String) arr.get(2), (String) arr.get(3));
				arrToSend.add(1);
				arrToSend.add(new Subscriber1());
				try {
					client.sendToClient(arrToSend);// send null only to call the client so the awaitResponse will be
													// false
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;

			case 2: // SELECT
				sub = SQLinstance.select((String) arr.get(1));
				arrToSend.add(2);
				arrToSend.add(sub);
				try {
					client.sendToClient(arrToSend); // sent to the client
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case 3: // Search the database to check email and password for librarian
				lib = SQLinstance.searchLibId((String) arr.get(1), (String) arr.get(2));
				arrToSend.add(3);
				arrToSend.add(lib);
				try {
					client.sendToClient(arrToSend);
				} catch (IOException e) {
					e.printStackTrace();
				}

				break;
			case 4: // Search the database to check email and password for subscriber
				sub = SQLinstance.searchSubId((String) arr.get(1), (String) arr.get(2));
				arrToSend.add(4);
				arrToSend.add(sub);
				try {
					client.sendToClient(arrToSend);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;

			case 5: // Check subscriber's status
				if (arr.get(1) instanceof String) {
					subID = Integer.valueOf((String) arr.get(1));
				} else {
					subID = (int) arr.get(1);
				}

				String retStatus = SQLinstance.checkIsFrozen(subID);

				arrToSend.add(5); // Add the case identifier to the response

				if (retStatus == null) {
					arrToSend.add("NOT_FOUND"); // Indicate that the subscriber was not found
				} else {
					arrToSend.add(retStatus); // Add the subscription status
				}

				try {
					client.sendToClient(arrToSend); // Send back to the client if the status is frozen or not
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;

			case 6: // Check if there is a book like this and then Check book availability
				bookName = (String) arr.get(2);
				// retAvailability will have 'exist' if book even exists, or 'available' if can
				// get a copy of it
				// go to book's DB and check if there is a book like this, if yes check if there
				// is an available copy
				// also put the number of total copys in its variable
				String retAvailability = SQLinstance.isAvailable(bookName);
				arrToSend.add(6);
				arrToSend.add(retAvailability);
				try {
					client.sendToClient(arrToSend); // send back to the client if the book is available
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;

			case 7: // Add an order
				subID = (int) arr.get(1);
				bookName = (String) arr.get(2);
				// go to orders table in the DB and check if can add a column (if the number of
				// orders is less than the number of copys)
				String canAdd = SQLinstance.canAddOrder(subID, bookName);
				arrToSend.add(7);
				arrToSend.add(canAdd);
				try {
					client.sendToClient(arrToSend);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;

			case 8: // watch activity history
				subID = Integer.parseInt((String) arr.get(1)); // subscriber ID is in the second position of the array
				// Retrieve the borrow history for the given subscriber ID
				ArrayList<String> borrowHistory = null;
				try {
					borrowHistory = SQLinstance.getBorrowHistory(subID);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				arrToSend.add(8);
				arrToSend.add(borrowHistory);
				try {
					// Send the borrow history to the client
					client.sendToClient(arrToSend);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;

			case 9: // Retrieves the activity history of a subscriber based on their email address.
				subEmail = (String) arr.get(3);
				ArrayList<String> activityHistory = SQLinstance.getActivityHistory(subEmail);
				arrToSend.add(9);
				arrToSend.add(activityHistory);
				try {
					client.sendToClient(arrToSend);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;

			case 10: // Changes the return date of a borrowed book for a subscriber if no conflicts
						// exist.
				// Extract parameters from the array
				subID = Integer.parseInt((String) arr.get(1));
				bookName = (String) arr.get(2);
				String OldDate = (String) arr.get(3);
				String NewDate = (String) arr.get(4);
				String Librarian_name = (String) arr.get(5);
				boolean updateDate = SQLinstance.ChangeReturnDate(subID, bookName, OldDate, NewDate, Librarian_name);
				arrToSend.add(10);
				arrToSend.add(updateDate);
				try {
					client.sendToClient(arrToSend);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;

			case 11: // Retrieves a borrow report for a specific month and year.
				ArrayList<String> BorrowRepDet = null;
				try {
					BorrowRepDet = SQLinstance.BringBorrowRepInfo((String) arr.get(1), (String) arr.get(2));
				} catch (SQLException e) {
					e.printStackTrace();
					BorrowRepDet = new ArrayList<>();
					BorrowRepDet.add("Error fetching data: " + e.getMessage());
				}
				arrToSend.add(11);
				if (BorrowRepDet != null && !BorrowRepDet.isEmpty()) {
					try {
						arrToSend.add(BorrowRepDet);
						client.sendToClient(arrToSend);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					try {
						arrToSend.add("No data available or an error occurred.");
						client.sendToClient(arrToSend);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				break;
			case 12: // Retrieves the names of books borrowed by a specific subscriber.
				ArrayList<String> borrowedBooks = SQLinstance.getBorrowedBooks((int) arr.get(1));
				arrToSend.add(12);
				arrToSend.add(borrowedBooks);
				try {
					// Send the borrow history to the client
					client.sendToClient(arrToSend);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case 13: // Adds a new subscriber to the database.
				String subName = (String) arr.get(1);
				String subPhone = (String) arr.get(2);
				String subEmail = (String) arr.get(3);
				String subStatus = (String) arr.get(4);
				String subPassword = (String) arr.get(5);
				SQLinstance.addSubscriber( subName, subPhone, subEmail, subStatus, subPassword);
				arrToSend.add(13);
				arrToSend.add(new Boolean(true));
				try {
					client.sendToClient(arrToSend);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 14: // Retrieves the number of available copies of a specific book, Retrieves the
						// nearest return date for a borrowed book.
				bookName = (String) arr.get(1);
				Integer BookIsInTheInvatory = SQLinstance.getBookAvailability(bookName);
				String deadlineDate = SQLinstance.getNearestReturnDate(bookName);
				arrToSend.add(14);
				arrToSend.add(BookIsInTheInvatory);
				arrToSend.add(deadlineDate);
				try {
					client.sendToClient(arrToSend);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case 15: // Checks if a subscriber exists in the database by their ID, Retrieves the
						// subscription status of a specific subscriber.
				Sub_id = (int) arr.get(1);
				Boolean subExist = SQLinstance.isSubscriberExist(Sub_id);
				String statusSub = SQLinstance.getSubscriptionStatus(Sub_id);
				arrToSend.add(15);
				arrToSend.add(subExist);
				arrToSend.add(statusSub);
				try {
					client.sendToClient(arrToSend);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case 16:// Decrements the availability of a specific book in the database by one.
				bookName = (String) arr.get(1);
				Boolean decreaseBook = SQLinstance.decrementBookAvailability(bookName);
				arrToSend.add(16);
				arrToSend.add(decreaseBook);
				try {
					client.sendToClient(arrToSend);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case 17: // Records a borrowing activity in the activityhistory table.
				Sub_id = (int) arr.get(1);
				bookName = (String) arr.get(2);
				SQLinstance.addActivityToHistory(Sub_id, bookName);
				arrToSend.add(17);
				arrToSend.add(new Boolean(true));
				try {
					client.sendToClient(arrToSend);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case 18: // Retrieves the names of all books from the database.
				ArrayList<String> AllBooks = SQLinstance.getAllBookNames();
				arrToSend.add(18);
				arrToSend.add(AllBooks);
				try {
					// Send the borrow history to the client
					client.sendToClient(arrToSend);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case 19:
			    ArrayList<String> statusRepDet = null;
			    try {
			        statusRepDet = SQLinstance.bringStatusRepInfo((String) arr.get(1), (String) arr.get(2));
			    } catch (SQLException e) {
			        e.printStackTrace();
			        // Return an array list with the error message
			        statusRepDet = new ArrayList<>();
			        statusRepDet.add("Error fetching data: " + e.getMessage());
			    }

			    // Prepare what to send:
			    arrToSend.add(19);  // The code

			    if (statusRepDet != null && !statusRepDet.isEmpty()) {
			        arrToSend.add(statusRepDet); 
			    } else {
			        // Instead of a single string, return an ArrayList so the shape is consistent
			        ArrayList<String> emptyMsg = new ArrayList<>();
			        emptyMsg.add("No data available or an error occurred.");
			        arrToSend.add(emptyMsg);
			    }

			    try {
			        client.sendToClient(arrToSend);
			    } catch (IOException e) {
			        e.printStackTrace();
			    }
			    break;

			case 20: // search if exist borrower in the DB
				String borrowerid = (String) arr.get(1); // subscriber ID is in the second position of the array
				String BookName = (String) arr.get(2);

				try {
		
					String bookNameFromTable = SQLinstance.checkIfBorrowerFound(borrowerid,BookName ); // check if there is a
																								// borrow in the
																								// database
					arrToSend.add(20);

					
					if(bookNameFromTable.equals("")) 
						arrToSend.add(false);
						
					else 
						arrToSend.add(true);
							
					arrToSend.add(bookNameFromTable);
					client.sendToClient(arrToSend);

				} catch (SQLException | IOException e) {

					e.printStackTrace();
				}
				break;
			case 21: // select the borrow action date and deadline and return it

				String Borrowerid = (String) arr.get(1); // subscriber ID is in the second position of the array
				String Bookname = (String) arr.get(2);
				arrToSend.add(21);
				try {
					arrToSend.add(SQLinstance.getBorrowDateAndReturnDate(Borrowerid, Bookname));

					client.sendToClient(arrToSend);
				} catch (SQLException | IOException e) {

					e.printStackTrace();
				}
				break;
			case 22: // return a book
				this.subscriberID = (String) arr.get(1);
				this.bookName = (String) arr.get(2);
				returnLate = (boolean) arr.get(3);
				freeze = (boolean) arr.get(4);
				StringBuilder lateDifference = new StringBuilder();
				if (arr.get(5) != null) {
					Period dateDifference = (Period) arr.get(5);
					int totalDays = dateDifference.getDays();
					int totalMonths = dateDifference.getMonths();
					int totalYears = dateDifference.getYears();

					if (totalYears > 0) {
						lateDifference.append(totalYears + " Years,");
					}
					if (totalMonths > 0)
						lateDifference.append(totalMonths + " Months, ");

					if (totalDays > 0)
						lateDifference.append(totalDays + " Days");

					if (returnLate)
						lateDifference.append(" Late");
				}
				boolean orderExists = SQLinstance.addArrivalTimeToOrders(this.bookName); // add the arrival time to
																							// orders table

				Boolean bookIncrement = true; //
				Boolean freezeSuccess = true; // Initialized freezeSuccess to true because of the AND action at
												// sendToClient so if it won't happen then it will still pass.
				Boolean insertRowToActivity = false; 
				Boolean freezeFlag = false;
				arrToSend.add(22);
				try {
					if (returnLate == false && freeze == false) {
						insertRowToActivity = SQLinstance.insertReturnBookRowInActivityHistory(this.subscriberID,
								this.bookName, "Returned on time", returnLate);

					}
					if (returnLate == true) {
						insertRowToActivity = SQLinstance.insertReturnBookRowInActivityHistory(this.subscriberID,
								this.bookName, lateDifference.toString(), returnLate);

					}
					if (freeze == true) {
						freezeSuccess = SQLinstance.updateSubscriberStatusToFrozen(this.subscriberID, "frozen");
						freezeFlag = true;

					}

					if (orderExists == false) { // which means no one has ordered this book then we can add the copy to
												// the inventory
						bookIncrement = SQLinstance.incrimentBookAvailability(this.bookName);

					}
					arrToSend.add(bookIncrement && freezeSuccess && insertRowToActivity);
					
					if (freezeSuccess && freezeFlag)
						arrToSend.add("FROZEN");
					
					else if (freeze == false)
						arrToSend.add("Active");
					
					else
						arrToSend.add("Already Frozen");
					client.sendToClient(arrToSend);

				} catch (IOException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				break;
			case 23: // Retrieves the name of a book based on its barcode ID.
				String bookNameBarCode;
				try {
					bookNameBarCode = SQLinstance.BringBarCodeBookName((int) arr.get(1));
				} catch (SQLException e) {
					e.printStackTrace();
					bookNameBarCode = "";
				}
				arrToSend.add(23);
				arrToSend.add(bookNameBarCode);
				try {
					client.sendToClient(arrToSend);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case 24: // Checks whether a subscriber can extend the borrow period for a specific book.
				String canExtend = SQLinstance.canExtend((int) arr.get(1), (String) arr.get(2));
				arrToSend.add(24);
				arrToSend.add(canExtend);
				try {
					client.sendToClient(arrToSend);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case 25: // Retrieves books from the database that match a specified search criteria.
				try {
					String message = (String) arr.get(1);
					String[] parts = message.split(" ", 3);
					if (parts.length < 3) {
						break;
					}
					String criteria = parts[1];
					String value = parts[2];
					ArrayList<String> foundBooks = SQLinstance.fetchBooksByCriteria(criteria, value);
					ArrayList<Object> arrToSend1 = new ArrayList<>();
					arrToSend1.add(25);
					arrToSend1.add(foundBooks);

					client.sendToClient(arrToSend1);

				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case 27: // return ArrayList of ordered books of a subscriber
				ArrayList<String> orders = SQLinstance.getOrdersOfSubscriber((int) arr.get(1));
				arrToSend.add(27);
				arrToSend.add(orders);
				try {
					client.sendToClient(arrToSend);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case 28: // delete the order after it has been taken by the subscriber
				Map<Integer, String> ordersToDelete = new HashMap<>();
				ordersToDelete.put((int) arr.get(1), (String) arr.get(2));
				SQLinstance.deleteOrders(ordersToDelete);
				arrToSend.add(28);
				try {
					client.sendToClient(arrToSend);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case 29:
				ArrayList<String> subMessages = SQLinstance.subscriberMessages((int) arr.get(1));
				arrToSend.add(29);
				arrToSend.add(subMessages);
				try {
					client.sendToClient(arrToSend);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case 30: // Retrieves all messages for librarians from the database.
				ArrayList<String> libMessages = SQLinstance.librarianMessages();
				arrToSend.add(30);
				arrToSend.add(libMessages);
				try {
					client.sendToClient(arrToSend);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case 31: // Retrieves a list of books borrowed by a subscriber that are near their return
						// deadline.
				ArrayList<String> booksNearDeadline = SQLinstance
						.getBooksNearDeadlineForSubscriber(Integer.parseInt((String) arr.get(1)));
				arrToSend.add(31);
				arrToSend.add(booksNearDeadline);
				try {
					client.sendToClient(arrToSend);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case 32:
				try {
					// Retrieve the count of new subscribers for the given month and year from the
					// input array
					int cnt = SQLinstance.getNewSubscriberCount((String) arr.get(1), (String) arr.get(2));

					// Prepare response to send to the client
					arrToSend.add(32); // Case number identifier
					arrToSend.add(cnt); // Add the count result

					// Send response to the client
					client.sendToClient(arrToSend);

				} catch (SQLException e) {
					e.printStackTrace(); // Log the error details

				} catch (IOException e) {
					e.printStackTrace(); // Log the error details
				}
				break;
			case 33:
				//lost book, freeze subscriber account and minus one the inventory of book
				String retS = SQLinstance.lostBook((String)arr.get(1), (String)arr.get(2));
				arrToSend.add(33);
				arrToSend.add(retS);
				try {
					client.sendToClient(arrToSend);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			default:
				System.out.println("The server - Received message is not of the expected type.");
				break;
			}
		}
	}

	/**
	 * Handles the event when a new client connects to the server. Logs the client's
	 * IP address and hostname, and notifies all registered listeners about the
	 * connection.
	 *
	 * @param client the {@link ConnectionToClient} object representing the
	 *               connected client.
	 */
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

	/**
	 * Handles the event when a client disconnects from the server. Retrieves the
	 * client's IP and hostname and notifies all registered listeners about the
	 * disconnection.
	 *
	 * @param client the {@link ConnectionToClient} object representing the
	 *               disconnected client.
	 */
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

	/**
	 * Performs time-sensitive actions when the server starts.
	 */
	public void time() {
		SQLinstance.timeDidntTakeOrder(); // go to DB and update subscribers that it has been 2 days since their order
											// arrived
		SQLinstance.unfreezeAfterMonthStatus(); // unfreeze subscribers after being frozen for a month
		SQLinstance.notifyBeforeReturnDeadline(); // Sends reminders to subscribers whose book return deadlines are the
													// next day.
	}
}