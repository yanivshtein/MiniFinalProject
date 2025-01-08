package server;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Scanner;

import common.Subscriber1;

public class mysqlConnection {

	private static Connection conn;

	public static void connectToDB() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			System.out.println("Driver definition succeed");
		} catch (Exception ex) {
			/* handle the error */
			System.out.println("Driver definition failed");
		}

		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/hw2-shitot?serverTimezone=IST", "root",
					"!vex123S");
			System.out.println("SQL connection succeed");

		} catch (SQLException ex) {/* handle any errors */
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
	}

	public static void update(String id, String p_number, String email) {
		String updateQuery = "UPDATE subscriber SET subscriber_phone_number = ?, subscriber_email = ? WHERE subscriber_id = ?";
		try (PreparedStatement ps = conn.prepareStatement(updateQuery)) {
			ps.setString(1, p_number);
			ps.setString(2, email);
			ps.setString(3, id);
			int rowsAffected = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static Subscriber1 select(String id) {
		Subscriber1 sub = null;
		String selectQuery = "SELECT * FROM subscriber WHERE subscriber_id = " + id;
		try {

			PreparedStatement ps = conn.prepareStatement(selectQuery);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				int sub_id = rs.getInt("subscriber_id");
				String sub_name = rs.getString("subscriber_name");
				int detail = rs.getInt("detailed_subscription_history");
				String phone = rs.getString("subscriber_phone_number");
				String email = rs.getString("subscriber_email");
				sub = new Subscriber1(sub_id, sub_name, detail, phone, email);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sub;
	}

	public static Boolean searchId(String id) {
		String searchQuery = "SELECT 1 FROM subscriber WHERE subscriber_id = ?";
		try (PreparedStatement ps = conn.prepareStatement(searchQuery)) {
			ps.setString(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return true; // The ID exists in the database
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false; // Return false if the ID does not exist
	}

	public static String canAddOrder(String id, String bookName) {
		String priority = null;
		String copysAmount = null;
		// query that returns the last priority and the total number of copys of the
		String canAddQuery = "SELECT priority, copysAmount " + "FROM BooksTable " + "WHERE bookName = ? "
				+ "AND priority = (SELECT MAX(priority) FROM orders WHERE bookName = ?);";
		try {
			PreparedStatement stmt = conn.prepareStatement(canAddQuery);
			stmt.setString(1, bookName);
			stmt.setString(2, bookName);
			try (ResultSet rs = stmt.executeQuery()) { // execute the query
				rs.next();
				priority = rs.getString("priority");
				copysAmount = rs.getString("copysAmount");
				if (priority.equals(copysAmount))//which means that cant order anymore
					return "can't";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		addOrder(bookName, id, priority, copysAmount); // add the order to the orders table in the DB
		return "can";
	}

	public static void addOrder(String bookName, String id, String priority, String copysAmount) {
		String addQuery = "INSERT INTO orders (bookName, id, priority, copysAmount) VALUES (?, ?, ?, ?);";
		//add 1 to the priority
		int priorityInt = Integer.parseInt(priority);
		priorityInt++;
		String newPriority = Integer.toString(priorityInt);
		try {
			PreparedStatement stmt = conn.prepareStatement(addQuery);
			stmt.setString(1, bookName);
			stmt.setString(2, id);
			stmt.setString(3, newPriority);
			stmt.setString(4, copysAmount);
			stmt.executeUpdate(); //execute the query
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	public static ArrayList<String> getActivityHistory(String subscriberId) {
	    ArrayList<String> activityHistory = new ArrayList<>();
	    String query = "SELECT * FROM ActivityHistory WHERE SubscriberID = ?";
	    
	    try (PreparedStatement ps = conn.prepareStatement(query)) {
	        ps.setString(1, subscriberId); 
	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                String bookName = rs.getString("BookName");
	                String actionType = rs.getString("ActionType");
	                String actionDate = rs.getString("ActionDate");
	                String additionalDetails = rs.getString("AdditionalDetails");

	                String activity = "Book Name: " + bookName + ", Action: " + actionType + 
	                                  ", Date: " + actionDate + ", Details: " + additionalDetails;
	                activityHistory.add(activity);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return activityHistory;
	}
	public static ArrayList<String> getBorrowHistory(String subscriberId) {
	    ArrayList<String> borrowHistory = new ArrayList<>();
	    String query = "SELECT * FROM ActivityHistory WHERE SubscriberID = ? AND ActionType = 'Borrow'";
	    try (PreparedStatement ps = conn.prepareStatement(query)) {
	        ps.setString(1, subscriberId); 
	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                String bookName = rs.getString("BookName");
	                String actionDate = rs.getString("ActionDate");
	                String additionalDetails = rs.getString("AdditionalDetails");

	                String activity = "Book Name: " + bookName + ", Date: " + actionDate + ", Details: " + additionalDetails;
	                borrowHistory.add(activity);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return borrowHistory;
	}
	
	// אני פה
	public static boolean ChangeReturnDate(String subscriberId , String BookName , String OldDate , String NewDate) {
		String query = "SELECT * FROM ActivityHistory WHERE SubscriberID = ? AND BookName = ? AND ";
		return false;
		
		
		
	}
	
	/*
	 * method that return the last borrow date and deadline of the borrow.
	 * if found in the database
	 */
	public static LinkedHashSet<String>  getBorrowDateAndReturnDate(String borrowerId,String bookName) throws SQLException {
		LinkedHashSet<String> borrowAndReturnDate = new LinkedHashSet<>();
		PreparedStatement ps = conn.prepareStatement("SELECT ActionDate,deadline FROM activityhistory where SubscriberID=? AND BookName=? AND ActionType='Borrow'");
		
		ps.setString(1, borrowerId);
		ps.setString(2, bookName);
		
		ResultSet resultSet = ps.executeQuery();
		if( resultSet.last()) {
			borrowAndReturnDate.add(resultSet.getString(1));
			borrowAndReturnDate.add(resultSet.getString(2));
			return borrowAndReturnDate;
		}
		return null;

		
	}
	
	// method that checks in the database if there is a certain borrower that borrowed the selected book
	// using "Exist" if there is a row  that match the borrower's ID and book's name  then
	// the method return true
	public static Boolean  checkIfBorrowerFound(String borrowerId,String bookName) throws SQLException {
		
		PreparedStatement ps = conn.prepareStatement("SELECT EXISTS(SELECT * FROM activityhistory where SubscriberID=? AND BookName=? AND ActionType='Borrow')");
		ps.setString(1, borrowerId);
		ps.setString(2, bookName);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
            return rs.getBoolean(1); // Retrieve the result from the first column
        }
		return false;
	}
	
	public static Boolean insertReturnBookRowInActivityHistory(String borrowerId,String bookName,int returnedLate) throws SQLException {
		
		int borrowerIdAsInt = Integer.parseInt(borrowerId);
		String insertQuary = "INSERT INTO activityhistory (SubScriberID, BookName, ActionType, ActionDate,"
				+ "returned_late) VALUES (?,?,?,?,?)";
		PreparedStatement ps = conn.prepareStatement(insertQuary);
		LocalDate actionDate = LocalDate.now();
		ps.setInt(1,borrowerIdAsInt);
		ps.setString(2,bookName);
		ps.setString(3,"Return");
		ps.setDate(4,Date.valueOf(actionDate));
		ps.setInt(5,returnedLate);
		
		
		return ps.execute();
	}
	
	public static Boolean updateSubscriberStatusToFrozen(String subscriberId,String IsFrozen) throws SQLException {
		
		String insertQuary = "UPDATE subscriber SET subscription_status=? WHERE subscriber_id = ?";
		PreparedStatement ps = conn.prepareStatement(insertQuary);
			
		ps.setString(1, IsFrozen);
		ps.setString(2, subscriberId);
			
		if(ps.executeUpdate()==1)
			return true;
		
		return false;
		
	}
	
	public static boolean incrimentBookAvailability(String bookName) throws SQLException {
		PreparedStatement checkIfFull= conn.prepareStatement("SELECT totalCopys From books WHERE bookName=? AND copysAvailable>=totalCopys");
		//int numOfCopiesAvailable=getBookAvailality(bookName);		//need to add to code
		ResultSet rs;
		checkIfFull.setString(1, bookName);
		rs= checkIfFull.executeQuery();
		
		
		if(rs.next())	{	// if there is a row that book copies available is equal or greater then total copies of the book
			System.err.println("there shouldn't be a book borrow in the first place");
			return false;	// then it is an error.
		
		}
			
		
		String insertQuary = "UPDATE books SET copysAvailable=copysAvailable + 1 WHERE bookName = ?";
		PreparedStatement ps = conn.prepareStatement(insertQuary);
		ps.setString(1, bookName);
		
		if(ps.executeUpdate()==1)
			return true;
		
		return false;
	}
}
