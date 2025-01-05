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
        
        if (msg instanceof ArrayList<?>) {
            ArrayList<Object> arr = (ArrayList<Object>) msg;
            int request = (Integer) arr.get(0);
            String subID;
            String bookName;
            
            switch (request) { //go to DB controller based on the request
                case 1: // UPDATE
                    mysqlConnection.update((String) arr.get(1), (String) arr.get(2), (String) arr.get(3));
                    try {
                        client.sendToClient(new Subscriber1());// send null only to call the client so the awaitResponse will be false
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
                case 3: //Search the database to check email and password for librarian
                	ret = mysqlConnection.searchLibId((String) arr.get(1), (String) arr.get(2));
                    try {
                        client.sendToClient(ret);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                	
                	break;
                case 4: //Search the database to check email and password for subscriber
                    ret = mysqlConnection.searchSubId((String) arr.get(1), (String) arr.get(2));
                    try {
                        client.sendToClient(ret);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case 5: // Check subscriber's status
                    subID = (String) arr.get(1);
                    String retStatus = "notFrozen"; // for the example
                 // go to subscriber's DB and return the status of subID (subscriber's id)
                    try {
                        client.sendToClient(retStatus); // send back to the client if the status is frozen or not
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
                    try {
                        client.sendToClient(retAvailability); // send back to the client if the book is available
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case 7: // Add an order
                    subID = (String) arr.get(1);
                    bookName = (String) arr.get(2);
                 // go to orders table in the DB and check if can add a column (if the number of orders is less than the number of copys)
                    String canAdd = mysqlConnection.canAddOrder(subID, bookName);
                    try {
                        client.sendToClient(canAdd);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case 8: //watch activity history
                    subID = (String)arr.get(1); //subscriber ID is in the second position of the array
                 // Retrieve the borrow history for the given subscriber ID
                    ArrayList<String> borrowHistory = mysqlConnection.getBorrowHistory(subID);
                    try {
                    	// Send the borrow history to the client
                        client.sendToClient(borrowHistory);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case 9:
                	subEmail = (String)arr.get(3);
                    ArrayList<String> activityHistory = mysqlConnection.getActivityHistory(subEmail);
                    try {
                        client.sendToClient(activityHistory);
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
                    try {
                        client.sendToClient(updateDate);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case 11:
                    ArrayList<String> BorrowRepDet = null;
                    try {
                        BorrowRepDet = mysqlConnection.BringBorrowRepInfo();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        BorrowRepDet = new ArrayList<>();
                        BorrowRepDet.add("Error fetching data: " + e.getMessage());
                    }

                    if (BorrowRepDet != null && !BorrowRepDet.isEmpty()) {
                        try {
                            client.sendToClient(BorrowRepDet);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            client.sendToClient("No data available or an error occurred.");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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
}