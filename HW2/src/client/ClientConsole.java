package client;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Period;
import java.util.ArrayList;
import java.util.Scanner;

import client.*;
import common.*;

/**
 * This class constructs the UI for a chat client.  It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here is cloned in ServerConsole 
 *
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Dr Timothy C. Lethbridge  
 * @author Dr Robert Lagani&egrave;re
 * @version July 2000
 */
public class ClientConsole implements ChatIF 
{
  //Class variables *************************************************
  
  /**
   * The default port to connect on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Instance variables **********************************************
  
  /**
   * The instance of the client that created this ConsoleChat.
   */
  ChatClient client;
  public boolean connected = false;
  
  //Constructors ****************************************************

  /**
   * Constructs an instance of the ClientConsole UI.
   *
   * @param host The host to connect to.
   * @param port The port to connect on.
   * @throws IOException 
   */
  public ClientConsole(String host, int port) throws IOException 
  {
    try 
    {
      client= new ChatClient(host, port, this);
      connected = true;
    } 
    catch(IOException exception) 
    {
      System.out.println("Error: Can't setup connection!"
                + " Terminating client.");
      throw exception;
    }
  }

  public void acceptLogin(String str, String email, String password) 
  {
    try
    {
      ArrayList<Object> arr1 = new ArrayList<>();
      
      if (str.equals("searchSub")) {
          arr1.add(4);
          arr1.add(email);
          arr1.add(password);
      }
      else if (str.equals("searchLib")) {
          arr1.add(3);
          arr1.add(email);
          arr1.add(password);
      }
      else  { //EXIT
          arr1.add(0);
      }
      client.handleMessageFromClientUI(arr1);
    } 
    catch (Exception ex) 
    {
      System.out.println("Unexpected error while reading from console!");
    }
  }

  //Instance methods ************************************************
  
  /**
   * This method waits for input from the console.  Once it is 
   * received, it sends it to the client's message handler.
   */
  public void accept(String str, String id, String phone, String email) 
  {
    try
    {
      ArrayList<Object> arr1 = new ArrayList<>();
      
      if(str.equals("check if frozen")) {
    	  arr1.add(5);
    	  arr1.add(id);
      }
      
      else if(str.equals("watch books to extend")) {
    	  arr1.add(31);
    	  arr1.add(id);
      }
      
      else if(str.equals("watch borrow history")){
          arr1.add(8);
          arr1.add(id);  
      }
      else if (str.equals("watch activity history")) {
          arr1.add(9);
          arr1.add("");  
          arr1.add("");
          arr1.add(email);
      }
      else if (str.equals("searchSub")) {
          arr1.add(4);
          arr1.add(id);
      }
      else if (str.equals("searchLib")) {
          arr1.add(3);
          arr1.add(id);
      }
      else if (str.equals("select")) { 
          arr1.add(2);
          arr1.add(id);
      }  
      else if (str.equals("update")) { 
          arr1.add(1); 
          arr1.add(id);
          arr1.add(phone);
          arr1.add(email);
      }
      else  { //EXIT
          arr1.add(0);
      }
      client.handleMessageFromClientUI(arr1);
    } 
    catch (Exception ex) 
    {
      System.out.println("Unexpected error while reading from console!");
    }
  }

  public void acceptFromController(int request, int id, String bookName) {
      ArrayList<Object> arr = new ArrayList<>();
      arr.add(request);
      arr.add(id);
      arr.add(bookName);
      client.handleMessageFromClientUI(arr);		  
  }
  
  public void acceptMessagesForLibrarian() {
	  ArrayList<Object> arr = new ArrayList<>();
      arr.add(30);
      client.handleMessageFromClientUI(arr);	
  }

  public void acceptFromOrderController(int request, String id, String bookName) {
      ArrayList<Object> arr = new ArrayList<>();
      arr.add(request);
      arr.add(id);
      arr.add(bookName);
      client.handleMessageFromClientUI(arr);		  
  }

  public void acceptAddToActivityHistoryController(int request, int id, String bookName) {
      ArrayList<Object> arr = new ArrayList<>();
      arr.add(request);
      arr.add(id);
      arr.add(bookName);
      client.handleMessageFromClientUI(arr);		  
  }

  public void acceptBorrowBook(int id) {
      ArrayList<Object> arr = new ArrayList<>();
      arr.add(15);
      arr.add(id);
      client.handleMessageFromClientUI(arr);		  
  }

  public void acceptSearchByCriteria(String criteria, String value) {
      String message = "SEARCH_BY_CRITERIA " + criteria + " " + value;
      ArrayList<Object> arr1 = new ArrayList<>();
      arr1.add(25);
      arr1.add(message);
      client.handleMessageFromClientUI(arr1);
  }
  
  public void acceptBarCode(int id) {
      ArrayList<Object> arr = new ArrayList<>();
      arr.add(23);
      arr.add(id);
      client.handleMessageFromClientUI(arr);	
  }

  public void reports_accept(String str, String selectedMonth, String selectedYear) 
  {
    try
    {
        ArrayList<Object> arr1 = new ArrayList<>();

        if(str.equals("create borrow report")) {
            arr1.add(11);
            arr1.add(selectedMonth);
            arr1.add(selectedYear);
        }
        else {
            arr1.add(19);
            arr1.add(selectedMonth);
            arr1.add(selectedYear);
        }
        client.handleMessageFromClientUI(arr1);
    } 
    catch (Exception ex) 
    {
      System.out.println("Unexpected error while reading from console!");
    }
  }

  public void acceptAddSubscriber(int id, String name, String phoneNumber, String email, String status, String password) {
      ArrayList<Object> arr = new ArrayList<>();
      arr.add(13);
      arr.add(id);
      arr.add(name);
      arr.add(phoneNumber);
      arr.add(email);
      arr.add(status);
      arr.add(password);
      client.handleMessageFromClientUI(arr);		  
  }

  public void acceptSearchBook(int request, String bookName) {
      ArrayList<Object> arr = new ArrayList<>();
      arr.add(request);
      arr.add(bookName);
      client.handleMessageFromClientUI(arr);		  
  }

  public void acceptAllTheBooks(int request) {
      ArrayList<Object> arr = new ArrayList<>();
      arr.add(request);
      client.handleMessageFromClientUI(arr);		  
  }

  public void book_accept(String str, String id, String BookName, String OldDate, String NewDate, String Librarian_name) 
  {
    try
    {
        ArrayList<Object> arr1 = new ArrayList<>();
        
        if(str.equals("set new return date")){
            arr1.add(10);
            arr1.add(id);
            arr1.add(BookName);
            arr1.add(OldDate);
            arr1.add(NewDate);
            arr1.add(Librarian_name);
        }
        client.handleMessageFromClientUI(arr1);
    } 
    catch (Exception ex) 
    {
      System.out.println("Unexpected error while reading from console!");
    }
  }

  public void returnBook_accept(String actionType, String borrowerID, String bookID, Boolean isLate, Boolean freezeStatus, Period totalDaysLate) {
      ArrayList<Object> arr1 = new ArrayList<>();
      String bookName=bookID;
      switch (actionType) {
        case "EXIST":
            arr1.add(20);
            arr1.add(borrowerID);
            arr1.add(bookID);
            break;
            
        case "SELECT DATE":		//  the action and deadline date of the borrow 
            arr1.add(21);
            arr1.add(borrowerID);
            arr1.add(bookName);
            break;
            
        case "INSERT":
            arr1.add(22);
            arr1.add(borrowerID);
            arr1.add(bookName);
            arr1.add(isLate);
            arr1.add(freezeStatus);
            arr1.add(totalDaysLate);
            break;
            
        case "CHECK_BOOK_RETURNED":	
            arr1.add(26);
            arr1.add(borrowerID);
            arr1.add(bookName);
            break;
           
        case "SELECT_CURRENT_BORROWED_BOOKS_BY_ID":
        	arr1.add(32);
        	arr1.add(borrowerID);
        	break;
        default:
            System.err.println("returnBook_accept:Entered default in switch case None of the above selected!");
            break;
      }
      
      client.handleMessageFromClientUI(arr1);
  }

  /**
   * This method overrides the method in the ChatIF interface.  It
   * displays a message onto the screen.
   *
   * @param message The string to be displayed.
   */
  public void display(String message) 
  {
    System.out.println("> " + message);
  }
}
//End of ConsoleChat class