package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

import common.Subscriber1;

public class mysqlConnection {

	private static Connection conn;

	public static mysqlConnection instance;
	
	private mysqlConnection() {
		connectToDB();
	}
	
	//singleton
	public static synchronized mysqlConnection getInstance() {
		if(instance == null) {
			instance = new mysqlConnection();
		}
		return instance;
	}
	
	private static void connectToDB() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			System.out.println("Driver definition succeed");
		} catch (Exception ex) {
			/* handle the error */
			System.out.println("Driver definition failed");
		}

		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/hw2-shitot?serverTimezone=IST", "root",
					"Aa123456");
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
	
	public static boolean ChangeReturnDate(String subscriberId, String BookName, String OldDate, String NewDate) {
	    String query = "UPDATE activityhistory SET ActionDate = ? WHERE SubscriberID = ? AND BookName = ? AND ActionDate = ? AND ActionType = 'Borrow'";
	    
	    try (PreparedStatement ps = conn.prepareStatement(query)) {
	        
	        // Set values 
	        ps.setString(1, NewDate);
	        ps.setString(2, subscriberId);
	        ps.setString(3, BookName);
	        ps.setString(4, OldDate);

	        int rowsAffected = ps.executeUpdate();
	        if (rowsAffected > 0) {
	            return true;
	        } else {
	            return false;
	        }
	    } catch (SQLException e) {
	        System.out.println("SQL Error: " + e.getMessage());
	        e.printStackTrace();
	        return false;
	    }
	}
	
	public static ArrayList<String> BringBorrowRepInfo() throws SQLException {
	    String selectClause, fromClause, joinCondition, groupByClause, leftJoinClause, whereClause, query;
	    ArrayList<String> FullBorrowRep = new ArrayList<>();

	    // Query 1: For Borrowed and Returned Books (with AdditionalDetails from br2)
	    selectClause = "SELECT br1.SubscriberID, br1.BookName, MIN(br1.ActionDate) AS BorrowDate, " +
	                   "MIN(br2.ActionDate) AS ReturnDate, br2.AdditionalDetails AS AdditionalDetails";
	    fromClause = "FROM activityhistory br1 JOIN activityhistory br2";
	    joinCondition = "ON br1.SubscriberID = br2.SubscriberID AND br1.BookName = br2.BookName " +
	                    "AND br1.ActionType = 'Borrow' AND br2.ActionType = 'Return' AND br1.ActionDate < br2.ActionDate";
	    groupByClause = "GROUP BY br1.SubscriberID, br1.BookName, br2.AdditionalDetails";

	    query = selectClause + " " + fromClause + " " + joinCondition + " " + groupByClause;

	    try (PreparedStatement ps = conn.prepareStatement(query)) {
	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                String bookName = rs.getString("BookName");
	                String subID = rs.getString("SubscriberID");
	                String BorrowDate = rs.getString("BorrowDate");
	                String ReturnDate = rs.getString("ReturnDate");
	                String AdditionalDet = rs.getString("AdditionalDetails");

	                String temp = "Subscriber ID: " + subID + "    Book Name: " + bookName +
	                              "    Borrow Date: " + BorrowDate + "    Return Date: " + ReturnDate +
	                              "    Additional Details: " + AdditionalDet;
	                FullBorrowRep.add(temp);
	            }
	        }
	    }

	    // Query 2: For Borrowed but Not Returned Books
	    selectClause = "SELECT br1.SubscriberID, br1.BookName, MIN(br1.ActionDate) AS BorrowDate";
	    fromClause = "FROM activityhistory br1";
	    leftJoinClause = "LEFT JOIN activityhistory br2";
	    joinCondition = "ON br1.SubscriberID = br2.SubscriberID AND br1.BookName = br2.BookName " +
	                    "AND br2.ActionType = 'Return'";
	    whereClause = "WHERE br1.ActionType = 'Borrow' AND br2.ActionType IS NULL";
	    groupByClause = "GROUP BY br1.SubscriberID, br1.BookName";

	    query = selectClause + " " + fromClause + " " + leftJoinClause + " " + joinCondition + " " + whereClause + " " + groupByClause;

	    try (PreparedStatement ps = conn.prepareStatement(query)) {
	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                String bookName = rs.getString("BookName");
	                String subID = rs.getString("SubscriberID");
	                String BorrowDate = rs.getString("BorrowDate");

	                String temp = "Subscriber ID: " + subID + "    Book Name: " + bookName +
	                              "    Borrow Date: " + BorrowDate + "    Return Date: __-__-____ __:__:__     Additional Details: N/A";
	                FullBorrowRep.add(temp);
	            }
	        }
	    }

	    return FullBorrowRep;
	}


		
}



