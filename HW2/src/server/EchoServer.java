package server;

import java.io.*;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.Librarian;
import common.Subscriber1;
import gui.ServerGUI;
import javafx.stage.Stage;
import logic.ClientInfo;
import ocsf.server.*;

public class EchoServer extends AbstractServer 
{
	
	mysqlConnection SQLinstance;
    private List<ConnectionListener> listeners = new ArrayList<>();
	private String subEmail;
	private String subscriberID;
	private String bookName;
     
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
    
    final public static int DEFAULT_PORT = 5555;
  
    public EchoServer(int port) 
    {
        super(port);
        SQLinstance = mysqlConnection.getInstance();
    }

    protected void serverStarted()
    {
        System.out.println("Server listening for connections on port " + getPort());
    }
  
    protected void serverStopped()
    {
        System.out.println("Server has stopped listening for connections.");
    }
  
    @SuppressWarnings("unchecked")
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        Subscriber1 sub = null;
        Boolean ret;
        Librarian lib;
        
        if (msg instanceof ArrayList<?>) {
            ArrayList<Object> arr = (ArrayList<Object>) msg;
            int request = (Integer) arr.get(0);
            int subID;
            String bookName;
            
            ArrayList<Object> arrToSend = new ArrayList<>();
            boolean returnLate;
			boolean freeze;
			switch (request) { //go to DB controller based on the request
                case 1: // UPDATE
                    SQLinstance.update((String) arr.get(1), (String) arr.get(2), (String) arr.get(3));
                    arrToSend.add(1);
                	arrToSend.add(new Subscriber1());
                    try {                   	
                        client.sendToClient(arrToSend);// send null only to call the client so the awaitResponse will be false
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
                case 3: //Search the database to check email and password for librarian
                	lib = SQLinstance.searchLibId((String) arr.get(1), (String) arr.get(2));
                	arrToSend.add(3);
                	arrToSend.add(lib);
                    try {                   	
                        client.sendToClient(arrToSend);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                	
                	break;
                case 4: //Search the database to check email and password for subscriber
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
                    //retAvailability will have 'exist' if book even exists, or 'available' if can get a copy of it
                    // go to book's DB and check if there is a book like this, if yes check if there is an available copy
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
                 // go to orders table in the DB and check if can add a column (if the number of orders is less than the number of copys)
                    String canAdd = SQLinstance.canAddOrder(subID, bookName);
                    arrToSend.add(7);
                    arrToSend.add(canAdd);
                    try {
                        client.sendToClient(arrToSend);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case 8: //watch activity history
                    subID = Integer.parseInt((String)arr.get(1)); //subscriber ID is in the second position of the array
                 // Retrieve the borrow history for the given subscriber ID
				ArrayList<String> borrowHistory = null;
				try {
					borrowHistory = SQLinstance.getBorrowHistory(subID);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
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

                case 9:
                	subEmail = (String)arr.get(3);
                    ArrayList<String> activityHistory = SQLinstance.getActivityHistory(subEmail);
                    arrToSend.add(9);
                    arrToSend.add(activityHistory);
                    try {
                        client.sendToClient(arrToSend);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case 10:
                	// Extract parameters from the array
                    subID = Integer.parseInt((String)arr.get(1));
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

                case 11:
                    ArrayList<String> BorrowRepDet = null;
                    try {
                        BorrowRepDet = SQLinstance.BringBorrowRepInfo((String)arr.get(1) , (String)arr.get(2));
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
                case 12:
                	ArrayList<String> borrowedBooks = SQLinstance.getBorrowedBooks((int)arr.get(1));
                    arrToSend.add(12);
                    arrToSend.add(borrowedBooks);
                    try {
                    	// Send the borrow history to the client
                        client.sendToClient(arrToSend);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 13:
                	int Sub_id = (int)arr.get(1); 
                	String subName = (String)arr.get(2);
                	String subPhone =(String)arr.get(3);
                	String subEmail =(String)arr.get(4);
                	String subStatus =(String)arr.get(5);
                	String subPassword = (String)arr.get(6);
                	SQLinstance.addSubscriber(Sub_id,subName,subPhone,subEmail,subStatus,subPassword); 
                	arrToSend.add(13);
                	arrToSend.add(new Boolean(true));
                	try {
                		client.sendToClient(arrToSend); 
                	}catch (Exception e) {
                        e.printStackTrace();
                    }
                	break;
                case 14:

                    bookName = (String) arr.get(1);
                    Integer BookIsInTheInvatory = SQLinstance.getBookAvailability(bookName);
                    String deadlineDate =SQLinstance.getNearestReturnDate(bookName);
                    arrToSend.add(14);
                    arrToSend.add(BookIsInTheInvatory);
                    arrToSend.add(deadlineDate);
                    try {
                        client.sendToClient(arrToSend);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break; 
                case 15:

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
                case 16:
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
                case 17:
                	Sub_id =(int) arr.get(1);
                    bookName = (String) arr.get(2);
                    SQLinstance.addActivityToHistory(Sub_id,bookName);
                    arrToSend.add(17);
                    arrToSend.add(new Boolean(true));
                    try {
                    	client.sendToClient(arrToSend);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 18:
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
                    	statusRepDet = SQLinstance.BringStatusRepInfo((String)arr.get(1) , (String)arr.get(2));
                    } catch (SQLException e) {
                        e.printStackTrace();
                        statusRepDet = new ArrayList<>();
                        statusRepDet.add("Error fetching data: " + e.getMessage());
                    }
                    arrToSend.add(19);
                    if (statusRepDet != null && !statusRepDet.isEmpty()) {
                        try {
                        	arrToSend.add(statusRepDet);
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
                case 20:	// search if exist borrower in the DB
	            	String borrowerid = (String)arr.get(1); //subscriber ID is in the second position of the array
		          	String bookID = (String)arr.get(2);
		  
				try {
					String BookName=SQLinstance.BringBarCodeBookName(Integer.parseInt(bookID));	// get the book name from the book database
					
					Boolean isExist= SQLinstance.checkIfBorrowerFound(borrowerid, BookName);	// check if there is a borrow in the database
					arrToSend.add(20);
					
					arrToSend.add(isExist);
					arrToSend.add(BookName);
					client.sendToClient(arrToSend);
					
				} catch (SQLException | IOException e) {
					
					e.printStackTrace();
				}
		          	break;
                case 21:	// select the borrow action date and deadline and return it 
	            	 
	            	String Borrowerid = (String)arr.get(1); //subscriber ID is in the second position of the array
		          	String Bookname = (String)arr.get(2);
		          	arrToSend.add(21);
				try {
					arrToSend.add(SQLinstance.getBorrowDateAndReturnDate(Borrowerid, Bookname));
					
					client.sendToClient(arrToSend);
				} catch (SQLException | IOException e) {
					
					e.printStackTrace();
				}
				 break;
                case 22:
	            	 this.subscriberID = (String)arr.get(1);
	            	 this.bookName = (String)arr.get(2);          	 
	            	 returnLate = (boolean) arr.get(3);
	            	 freeze = (boolean)arr.get(4);
	            	 StringBuilder lateDifference = new StringBuilder();
	            	 if (arr.get(5)!=null) {
	            		 Period dateDifference=(Period) arr.get(5);
	            		 int totalDays=dateDifference.getDays();
	            		 int totalMonths=dateDifference.getMonths();
	            		 int totalYears= dateDifference.getYears();
	         		
	            		 if(totalYears>0) {
	            			 lateDifference.append(totalYears+" Years,");
	            		 }
	            		 if(totalMonths>0)
	            			 lateDifference.append(totalMonths+" Months, ");
	         		
	            		 if(totalDays>0)
	            			 lateDifference.append(totalDays + " Days");
	            		 
	            		 if(returnLate)
	            			 lateDifference.append(" Late");
	            	 }
	            	 boolean orderExists = SQLinstance.addArrivalTimeToOrders(this.bookName); //add the arrival time to orders table	           	 
	         		 Boolean bookIncrement = false;	// Initialized freezeSuccess to true because 
	            	 Boolean freezeSuccess = true;	// of the AND action at sendToClient
	            	 Boolean insertRowToActivity = false; // so if it won't happen then it will still pass.
	            	 arrToSend.add(22);
	            	 try {
	            		 if(returnLate==false && freeze==false) {
		            		 insertRowToActivity = SQLinstance.insertReturnBookRowInActivityHistory(this.subscriberID, this.bookName,"Returned on time",returnLate);

		            	 }
	            		 if(returnLate==true) {
		            		 insertRowToActivity = SQLinstance.insertReturnBookRowInActivityHistory(this.subscriberID, this.bookName,lateDifference.toString(),returnLate);

		            	 }
		            	 if(freeze==true){
		            		 freezeSuccess = SQLinstance.updateSubscriberStatusToFrozen(this.subscriberID,"Frozen");
		            		 
		            		 if(freezeSuccess) 
		            			 arrToSend.add("FROZEN");
		            			
		            		 else {
		            			 System.err.println("Freezing subscriber status didn't work");
		            			 
		            		 }
		            			 
		            		 
		            	 }
		            	 
		            	 if (freeze==false) 
		            		 arrToSend.add("Active");
	            			 
		            	 
		            	 if (orderExists == false) { //which means no one has ordered this book then we can add the copy to the inventory
		            		 bookIncrement = SQLinstance.incrimentBookAvailability(this.bookName);
		            		 
		            	 }		            	
		            	 arrToSend.add(bookIncrement && freezeSuccess && insertRowToActivity);
		            	 
		            	 client.sendToClient(arrToSend );

	            	 } catch (IOException e) {
	            		 e.printStackTrace();
	            	 } catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            	 break;
                case 23:
                	String bookNameBarCode;
                	try {
                		bookNameBarCode = SQLinstance.BringBarCodeBookName((int)arr.get(1));
                	}catch(SQLException e) {
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
                case 24:
                	String canExtend = SQLinstance.canExtend((int)arr.get(1), (String)arr.get(2));
                	arrToSend.add(24);
                	arrToSend.add(canExtend);
					try {
						client.sendToClient(arrToSend);
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;

                case 25:
                    try {
                        
                        String message = (String) arr.get(1); 
                        String[] parts = message.split(" ", 3);
                        if (parts.length < 3) {
                            System.out.println("Invalid search criteria message from client.");
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
                case 26:	// check if already returned the book
	            	 this.subscriberID = (String)arr.get(1);
	            	 this.bookName = (String)arr.get(2); 
	            	 arrToSend.add(26);
				try {
					/*
					 * the return Boolean of the method call in the database represents:
					 * True: if the book was already returned by subscriber
					 * False: the book  still didn't return.
					 */
					ret = SQLinstance.checkBookAlreadyReturned(this.subscriberID, this.bookName);
					arrToSend.add(ret);
					client.sendToClient(arrToSend);
				} catch (SQLException | IOException e) {
					
					e.printStackTrace();
				}
	            	break;
                case 27: //return ArrayList of ordered books of a subscriber
                	ArrayList<String> orders = SQLinstance.getOrdersOfSubscriber((int)arr.get(1));
                	arrToSend.add(27);
                	arrToSend.add(orders);
                	try {
                		client.sendToClient(arrToSend);
                	} catch (IOException e) {
                		e.printStackTrace();
                	}
                	break;
                case 28: //delete the order after it has been taken by the subscriber
                	Map <Integer, String> ordersToDelete = new HashMap<>();	
                	ordersToDelete.put((int)arr.get(1), (String)arr.get(2));
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
                case 30:
                	ArrayList<String> libMessages = SQLinstance.librarianMessages();
                	arrToSend.add(30);
                	arrToSend.add(libMessages);
                	try {
                		client.sendToClient(arrToSend);
                	} catch (IOException e) {
                		e.printStackTrace();
                	}
                	break;
                case 31:
            	    ArrayList<String> booksNearDeadline = SQLinstance.getBooksNearDeadlineForSubscriber(Integer.parseInt((String) arr.get(1)));
                	arrToSend.add(31);
                	arrToSend.add(booksNearDeadline);
                	try {
                		client.sendToClient(arrToSend);
                	} catch (IOException e) {
                		e.printStackTrace();
                	}
                	break;
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
    
    //this method check all the actions that use time, it is called when the server starts
   public void time() {
	 //go to DB and update subscribers that it has been 2 days since their order arrived
	 //also, delete the tuples in 'orders' table
   	 SQLinstance.timeDidntTakeOrder();
   	 SQLinstance.unfreezeAfterMonthStatus();
   	 SQLinstance.notifyBeforeReturnDeadline();
   }
}