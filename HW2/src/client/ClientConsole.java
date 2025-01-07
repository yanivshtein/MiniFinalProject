// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 
package client;

import java.io.*;
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

  
  //Constructors ****************************************************

  /**
   * Constructs an instance of the ClientConsole UI.
   *
   * @param host The host to connect to.
   * @param port The port to connect on.
   */
  public ClientConsole(String host, int port) 
  {
    try 
    {
      client= new ChatClient(host, port, this);
    } 
    catch(IOException exception) 
    {
      System.out.println("Error: Can't setup connection!"
                + " Terminating client.");
      System.exit(1);
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
      
      if(str.equals("watch borrow history")){
    	  arr1.add(8);
    	  arr1.add(id);  
      }
      
      if (str.equals("watch activity history")) {
    	  arr1.add(9);
    	  arr1.add(id);  
      }
      else if (str.equals("search")) {
    	  arr1.add(4);
    	  arr1.add(id);
      }
      else if (str.equals("select")) { //SELECT
    	  arr1.add(2);
    	  arr1.add(id);
      }  
      else if (str.equals("update")) { //UPDATE
    	  arr1.add(1); 
    	  arr1.add(id);
    	  arr1.add(phone);
    	  arr1.add(email);
      }
      else  { //EXIT
    	  arr1.add(3);
      }
      client.handleMessageFromClientUI(arr1);
      
      
    } 
    catch (Exception ex) 
    {
      System.out.println
        ("Unexpected error while reading from console!");
    }
  }

  public void acceptFromOrderController(int request, String id, String bookName) {
	  ArrayList<Object> arr = new ArrayList<>();
	  arr.add(request);
	  arr.add(id);
	  arr.add(bookName);
	  client.handleMessageFromClientUI(arr);		  
  }
  
  public void book_accept(String str, String BookName, String id,String OldDate ,String NewDate) 
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
        }
    	client.handleMessageFromClientUI(arr1);
    } 
    catch (Exception ex) 
    {
      System.out.println
        ("Unexpected error while reading from console!");
    }
  }
  
  public void returnBook_accept(String actionType, String borrowerID,String bookName, boolean isLate,boolean freezeStatus) {
	  
	  ArrayList<Object> arr1 = new ArrayList<>();
	  
	  
	  switch (actionType) {
	case "EXIST":
		
		arr1.add(19);
		arr1.add(borrowerID);
		arr1.add(bookName);
		break;
		
	case "SELECT DATE":
		
		arr1.add(20);
		arr1.add(borrowerID);
		arr1.add(bookName);
		break;
		
	case "INSERT":
		
		arr1.add(21);
		arr1.add(borrowerID);
		arr1.add(bookName);
		arr1.add(isLate);
		arr1.add(freezeStatus);
		break;
		
	default:
		System.err.println("Entered default in switch case None of the above selected");
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
  
 

  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of the Client UI.
   *
   * @param args[0] The host to connect to.
   */
  public static void main(String[] args) 
  {
    String host = "";
    int port = 0;  //The port number

    try
    {
      host = args[0];
    }
    catch(ArrayIndexOutOfBoundsException e)
    {
      host = "localhost";
    }
    ClientConsole chat= new ClientConsole(host, DEFAULT_PORT);
    Scanner scanner = new Scanner(System.in);

    while (true) {
        // Display menu
        System.out.println("Choose an action:");
        System.out.println("1. Insert data into the database");
        System.out.println("2. Fetch data from the database");
        System.out.println("3. Exit");
        System.out.print("Enter your choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine();  // Consume the newline character after the number

        switch (choice) {
            case 1:
                // Insert data into the database
                chat.insertData();
                break;

            case 2:
                // Fetch data from the database
                //fetchData();
                break;

            case 3:
                // Exit the program
                System.out.println("Exiting...");
                scanner.close();
                return;

            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }
  }
  public void insertData() 
  {
	  ArrayList<Object> list = new ArrayList<>();
	  list.add(1);
	  list.add(new Subscriber1());
    try
    {
      
          client.handleMessageFromClientUI(list);


    } 
    catch (Exception ex) 
    {
      System.out.println
        ("Unexpected error while reading from console!");
    }
  }
  
  
  public void fetchData() {
	  ArrayList<Object> list = new ArrayList<>();
	  list.add(2);
    try
    {
      
          client.handleMessageFromClientUI(list);

    } 
    catch (Exception ex) 
    {
      System.out.println
        ("Unexpected error while reading from console!");
    }
	  
	  
  }



}
//End of ConsoleChat class
