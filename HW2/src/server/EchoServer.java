package server;

import java.io.*;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.Subscriber1;
import gui.ServerGUI;
import javafx.stage.Stage;
import logic.ClientInfo;
import ocsf.server.*;

public class EchoServer extends AbstractServer 
{
	
	mysqlConnection instance;
    private List<ConnectionListener> listeners = new ArrayList<>();
	private String subEmail;
     
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
        instance = mysqlConnection.getInstance();
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
        int id;
        
        if (msg instanceof ArrayList<?>) {
            ArrayList<Object> arr = (ArrayList<Object>) msg;
            int request = (Integer) arr.get(0);
            String subID;
            String bookName;
            
            ArrayList<Object> arrToSend = new ArrayList<>();
            switch (request) { //go to DB controller based on the request
                case 1: // UPDATE
                    mysqlConnection.update((String) arr.get(1), (String) arr.get(2), (String) arr.get(3));
                    arrToSend.add(1);
                	arrToSend.add(new Subscriber1());
                    try {                   	
                        client.sendToClient(arrToSend);// send null only to call the client so the awaitResponse will be false
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case 2: // SELECT
                    sub = mysqlConnection.select((String) arr.get(1));
                    arrToSend.add(2);
                	arrToSend.add(sub);
                    try {                   	
                        client.sendToClient(arrToSend); // sent to the client
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3: //Search the database to check email and password for librarian
                	ret = mysqlConnection.searchLibId((String) arr.get(1), (String) arr.get(2));
                	arrToSend.add(3);
                	arrToSend.add(ret);
                    try {                   	
                        client.sendToClient(ret);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                	
                	break;
                case 4: //Search the database to check email and password for subscriber
                    ret = mysqlConnection.searchSubId((String) arr.get(1), (String) arr.get(2));
                    arrToSend.add(4);
                    arrToSend.add(ret);
                    try {
                        client.sendToClient(arrToSend);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case 5: // Check subscriber's status
                    subID = (String) arr.get(1);
                    String retStatus = "notFrozen"; // for the example
                 // go to subscriber's DB and return the status of subID (subscriber's id)
                    arrToSend.add(5);
                    arrToSend.add(retStatus);
                    try {
                        client.sendToClient(arrToSend); // send back to the client if the status is frozen or not
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case 6: // Check if there is a book like this and then Check book availability
                    bookName = (String) arr.get(2); 
                    //retAvailability will have 'exist' if book even exists, or 'available' if can get a copy of it
                    // go to book's DB and check if there is a book like this, if yes check if there is an available copy
	                // also put the number of total copys in its variable
                    String retAvailability = mysqlConnection.isAvailable(bookName); 
                    arrToSend.add(6);
                    arrToSend.add(retAvailability);
                    try {
                        client.sendToClient(arrToSend); // send back to the client if the book is available
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case 7: // Add an order
                    subID = (String) arr.get(1);
                    bookName = (String) arr.get(2);
                 // go to orders table in the DB and check if can add a column (if the number of orders is less than the number of copys)
                    String canAdd = mysqlConnection.canAddOrder(subID, bookName);
                    arrToSend.add(7);
                    arrToSend.add(canAdd);
                    try {
                        client.sendToClient(arrToSend);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case 8: //watch activity history
                    subID = (String)arr.get(1); //subscriber ID is in the second position of the array
                 // Retrieve the borrow history for the given subscriber ID
                    ArrayList<String> borrowHistory = mysqlConnection.getBorrowHistory(subID);
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
                    ArrayList<String> activityHistory = mysqlConnection.getActivityHistory(subEmail);
                    arr.add(9);
                    arr.add(activityHistory);
                    try {
                        client.sendToClient(arrToSend);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case 10:
                	// Extract parameters from the array
                    subID = (String) arr.get(1);
                    bookName = (String) arr.get(2);
                    String OldDate = (String) arr.get(3);
                    String NewDate = (String) arr.get(4);
                    String Librarian_name = (String) arr.get(5);
                    boolean updateDate = mysqlConnection.ChangeReturnDate(subID, bookName, OldDate, NewDate, Librarian_name);
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
                        BorrowRepDet = mysqlConnection.BringBorrowRepInfo((String)arr.get(1) , (String)arr.get(2));
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
                case 13:
                	int Sub_id = (int)arr.get(1); 
                	String subName = (String)arr.get(2);
                	String subPhone =(String)arr.get(3);
                	String subEmail =(String)arr.get(4);
                	String subStatus =(String)arr.get(5);
                	String subPassword = (String)arr.get(6);
                	mysqlConnection.addSubscriber(Sub_id,subName,subPhone,subEmail,subStatus,subPassword); 
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
                    Integer BookIsInTheInvatory = mysqlConnection.getBookAvailability(bookName);
                    arrToSend.add(14);
                    arrToSend.add(BookIsInTheInvatory);
                    try {
                        client.sendToClient(arrToSend);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break; 
                case 15:

                    Sub_id = (int) arr.get(1);
                    Boolean subExist = mysqlConnection.isSubscriberExist(Sub_id);
                    System.out.println(subExist);
                    arrToSend.add(15);
                    arrToSend.add(subExist);
                    try {
                        client.sendToClient(arrToSend);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 16:
                    bookName = (String) arr.get(1);
                    Boolean decreaseBook = mysqlConnection.decrementBookAvailability(bookName);
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
                    mysqlConnection.addActivityToHistory(Sub_id,bookName);
                    arrToSend.add(17);
                    arrToSend.add(new Boolean(true));
                    try {
                    	client.sendToClient(arrToSend);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 18:
                       ArrayList<String> AllBooks = mysqlConnection.getAllBookNames();
                       System.out.println(AllBooks+ "echoserver");
                       arrToSend.add(18);
                       arrToSend.add(AllBooks);
                       try {
                       	// Send the borrow history to the client
                           client.sendToClient(arrToSend);
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                       break;
<<<<<<< HEAD
                                    	                    
=======
                case 19:
                	ArrayList<String> statusRepDet = null;
                    try {
                    	statusRepDet = mysqlConnection.BringStatusRepInfo((String)arr.get(1) , (String)arr.get(2));
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
               
                    
                	
                    

>>>>>>> main
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