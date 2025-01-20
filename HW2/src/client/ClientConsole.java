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

  /**
   * This method handles the login process based on the provided parameters. It constructs an ArrayList containing
   * relevant data for login and passes it to the client for further handling.
   * @param str The type of login attempt.
   * @param email The email of the user attempting to log in.
   * @param password The password of the user attempting to log in.
   */
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

  /**
   * Sends a request to the client with the provided details to handle a specific action.
   * 
   * @param request The type of request.
   * @param id The ID associated with the request.
   * @param bookName The name of the book related to the request.
   */
  public void acceptFromController(int request, int id, String bookName) {
      ArrayList<Object> arr = new ArrayList<>();
      arr.add(request);
      arr.add(id);
      arr.add(bookName);
      client.handleMessageFromClientUI(arr);		  
  }
  
  /**
   * Sends a request to the client to accept messages for the librarian.
   */
  public void acceptMessagesForLibrarian() {
	  ArrayList<Object> arr = new ArrayList<>();
      arr.add(30);
      client.handleMessageFromClientUI(arr);	
  }

  /**
   * Sends a request to the client with the provided details to handle a specific order action.
   * 
   * @param request The type of request.
   * @param id The ID associated with the request.
   * @param bookName The name of the book related to the request.
   */
  public void acceptFromOrderController(int request, String id, String bookName) {
      ArrayList<Object> arr = new ArrayList<>();
      arr.add(request);
      arr.add(id);
      arr.add(bookName);
      client.handleMessageFromClientUI(arr);		  
  }

  /**
   * Sends a request to the client with the provided details to add an action to the activity history.
   * 
   * @param request The type of request.
   * @param id The ID associated with the request.
   * @param bookName The name of the book related to the request.
   */
  public void acceptAddToActivityHistoryController(int request, int id, String bookName) {
      ArrayList<Object> arr = new ArrayList<>();
      arr.add(request);
      arr.add(id);
      arr.add(bookName);
      client.handleMessageFromClientUI(arr);		  
  }

  /**
   * Sends a request to the client to handle borrowing a book with the given ID.
   * 
   * @param id The ID associated with the book to borrow.
   */
  public void acceptBorrowBook(int id) {
      ArrayList<Object> arr = new ArrayList<>();
      arr.add(15);
      arr.add(id);
      client.handleMessageFromClientUI(arr);		  
  }

  /**
   * Sends a request to the client to search by specific criteria.
   * 
   * @param criteria The search criteria (e.g., author, genre).
   * @param value The value associated with the criteria.
   */
  public void acceptSearchByCriteria(String criteria, String value) {
      String message = "SEARCH_BY_CRITERIA " + criteria + " " + value;
      ArrayList<Object> arr1 = new ArrayList<>();
      arr1.add(25);
      arr1.add(message);
      client.handleMessageFromClientUI(arr1);
  }
  
  /**
   * Sends a request to the client to handle barcode scanning with the provided ID.
   * 
   * @param id The ID associated with the barcode.
   */
  public void acceptBarCode(int id) {
      ArrayList<Object> arr = new ArrayList<>();
      arr.add(23);
      arr.add(id);
      client.handleMessageFromClientUI(arr);	
  }

  /**
   * Sends a request to the client to generate reports based on the provided criteria.
   * 
   * @param str The type of report ("how many joined", "create borrow report", or other).
   * @param selectedMonth The month for which the report is to be generated.
   * @param selectedYear The year for which the report is to be generated.
   */
  public void reports_accept(String str, String selectedMonth, String selectedYear) 
  {
    try
    {
        ArrayList<Object> arr1 = new ArrayList<>();
        
        if(str.equals("how many joined")) {
        	arr1.add(32);
            arr1.add(selectedMonth);
            arr1.add(selectedYear);
        }

        else if(str.equals("create borrow report")) {
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

  /**
   * Sends a request to the client to add a new subscriber with the provided details.
   * 
   * @param id The ID of the subscriber.
   * @param name The name of the subscriber.
   * @param phoneNumber The phone number of the subscriber.
   * @param email The email address of the subscriber.
   * @param status The subscription status (e.g., Active, Inactive).
   * @param password The password of the subscriber.
   */
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

  /**
   * Sends a request to the client to search for a book by its name.
   * 
   * @param request The type of request for searching a book.
   * @param bookName The name of the book to search.
   */
  public void acceptSearchBook(int request, String bookName) {
      ArrayList<Object> arr = new ArrayList<>();
      arr.add(request);
      arr.add(bookName);
      client.handleMessageFromClientUI(arr);		  
  }

  /**
   * Sends a request to the client to fetch all the books.
   * 
   * @param request The type of request for retrieving all books.
   */
  public void acceptAllTheBooks(int request) {
      ArrayList<Object> arr = new ArrayList<>();
      arr.add(request);
      client.handleMessageFromClientUI(arr);		  
  }

  /**
   * Sends a request to the client to set a new return date for a book.
   * 
   * @param str The type of action ("set new return date").
   * @param id The ID of the book.
   * @param BookName The name of the book.
   * @param OldDate The current return date.
   * @param NewDate The new return date.
   * @param Librarian_name The name of the librarian updating the return date.
   */
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

  /**
   * Sends a request to the client for handling the return of a book based on the action type.
   * 
   * @param actionType The action to perform (e.g., "EXIST", "SELECT DATE", "INSERT", "CHECK_BOOK_RETURNED").
   * @param borrowerID The ID of the borrower returning the book.
   * @param bookID The ID of the book being returned.
   * @param isLate Whether the book is late or not.
   * @param freezeStatus Whether the borrower's account is frozen.
   * @param totalDaysLate The total number of days the book is late.
   */
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
            
        default:
            System.err.println("returnBook_accept:Entered default in switch case None of the above selected!");
            break;
      }
      
      client.handleMessageFromClientUI(arr1);
  }

@Override
public void display(String message) {	
}
  
}
