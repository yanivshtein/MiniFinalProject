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
    private int copysAmount = 0;
    private List<ConnectionListener> listeners = new ArrayList<>();
     
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
    }

    protected void serverStarted()
    {
        mysqlConnection.connectToDB();
        System.out.println("Server listening for connections on port " + getPort());
    }
  
    protected void serverStopped()
    {
        System.out.println("Server has stopped listening for connections.");
    }
  
    @SuppressWarnings("unchecked")
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        Subscriber1 sub = null;
        
        if (msg instanceof ArrayList<?>) {
            ArrayList<Object> arr = (ArrayList<Object>) msg;
            int request = (Integer) arr.get(0);
            String subID;
            String bookName;
            
            switch (request) {
                case 1:
                    mysqlConnection.update((String) arr.get(1), (String) arr.get(2), (String) arr.get(3));
                    try {
                        client.sendToClient(new Subscriber1());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case 2:
                    sub = mysqlConnection.select((String) arr.get(1));
                    try {
                        client.sendToClient(sub);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case 4:
                    Boolean ret = mysqlConnection.searchId((String) arr.get(1));
                    try {
                        client.sendToClient(ret);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case 5:
                    subID = (String) arr.get(1);
                    String retStatus = "frozen";
                    try {
                        client.sendToClient(retStatus);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case 6:
                    bookName = (String) arr.get(2);
                    String retAvailability = "notAvailable";
                    copysAmount = 2;
                    try {
                        client.sendToClient(retAvailability);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case 7:
                    subID = (String) arr.get(1);
                    bookName = (String) arr.get(2);
                    String canAdd = mysqlConnection.canAddOrder(subID, bookName, copysAmount);
                    try {
                        client.sendToClient(canAdd);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case 8:
                    subID = (String)arr.get(1);
                    ArrayList<String> borrowHistory = mysqlConnection.getBorrowHistory(subID);
                    try {
                        client.sendToClient(borrowHistory);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case 9:
                    subID = (String)arr.get(1);
                    ArrayList<String> activityHistory = mysqlConnection.getActivityHistory(subID);
                    try {
                        client.sendToClient(activityHistory);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case 10:
                    subID = (String) arr.get(1);
                    bookName = (String) arr.get(2);
                    String OldDate = (String) arr.get(3);
                    String NewDate = (String) arr.get(4);
                    boolean updateDate = mysqlConnection.ChangeReturnDate(subID, bookName, OldDate, NewDate);
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
        String clientInfo = client.getInetAddress().getHostAddress();
        String name = client.getInetAddress().getHostName();
        ClientInfo c = new ClientInfo(clientInfo, name);
        
        for (ConnectionListener listener : listeners) {
            listener.onClientConnected(c);
        }
    }
  
    @Override
    protected void clientDisconnected(ConnectionToClient client) {
        try {
            String clientIp = client.getInetAddress().getHostAddress();
            String clientHostName = client.getInetAddress().getHostName();
            ClientInfo clientInfo = new ClientInfo(clientIp, clientHostName);

            for (ConnectionListener listener : listeners) {
                listener.onClientDisconnected(clientInfo);
            }
        } catch (Exception e) {
            System.out.println("Error during client disconnection: " + e.getMessage());
        }
    }  
}