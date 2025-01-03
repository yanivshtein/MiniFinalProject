package server;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import common.Subscriber1;

public class mysqlConnection {
	private static Connection conn;
	private static int totalCopys;
	public static void connectToDB() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			System.out.println("Driver definition succeed");
		} catch (Exception ex) {
			System.out.println("Driver definition failed");
		}

		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/hw2-shitot?serverTimezone=IST", "root",
					"Aa123456");
			System.out.println("SQL connection succeed");
		} catch (SQLException ex) {
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
			ps.executeUpdate();
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

	public static String isAvailable(String bookName) {
		String query = "SELECT copysAvailable, totalCopys FROM books WHERE bookName = ?";
		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, bookName);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) { // if found the book
				int copysAvailable = rs.getInt("copysAvailable");
				totalCopys = rs.getInt("totalCopys");
				if (copysAvailable > 0) // which means there is a copy of this book in the library then no need to
										// order, can simply borrow
					return "available";
				else 
					return "notAvailable";
			} 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "notExist"; //there isnt a book like this in the library
	}

	public static String canAddOrder(String id, String bookName) {
		int count;
		// query that returns the number of tuples with the same bookName
		String canAddQuery = "SELECT COUNT(*) AS count FROM orders WHERE bookName = ?";
		try {
			PreparedStatement stmt = conn.prepareStatement(canAddQuery);
			stmt.setString(1, bookName);
			try (ResultSet rs = stmt.executeQuery()) { // execute the query
				rs.next();
				count = rs.getInt("count");
				if ((count != 0) && (count == totalCopys)) // which means that cant order anymore
					return "can't";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		addOrder(bookName, id); // add the order to the orders table in the DB
		addOrderToActivityHistory(bookName, id); // add the order to the acitivityHistory table in the DB
		return "can";
	}

	public static void addOrder(String bookName, String id) {
		String addQuery = "INSERT INTO orders (time, bookName, subID) VALUES (?, ?, ?);";
		String dateTime = LocalDateTime.now().toString(); // get the time of the computer
		try {
			PreparedStatement stmt = conn.prepareStatement(addQuery);
			stmt.setString(1, dateTime);
			stmt.setString(2, bookName);
			stmt.setString(3, id);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void addOrderToActivityHistory(String bookName, String id) {
		String addQuery = "INSERT INTO activityhistory (SubscriberID, BookName, ActionType, ActionDate, AdditionalDetails) VALUES (?, ?, ?, ?, ?);";
		try {
			PreparedStatement stmt = conn.prepareStatement(addQuery);
			int ID = 1; // delete after check !!!!!!1
			stmt.setInt(1, ID);
			stmt.setString(2, bookName);
			stmt.setString(3, "Reservation");
			stmt.setString(4, LocalDateTime.now().toString()); // get the time of the computer
			stmt.setString(5, "");
			stmt.executeUpdate();
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
					activityHistory.add("Book Name: " + bookName + ", Action: " + actionType + ", Date: " + actionDate
							+ ", Details: " + additionalDetails);
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
					borrowHistory.add(
							"Book Name: " + bookName + ", Date: " + actionDate + ", Details: " + additionalDetails);
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
			ps.setString(1, NewDate);
			ps.setString(2, subscriberId);
			ps.setString(3, BookName);
			ps.setString(4, OldDate);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			System.out.println("SQL Error: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public static ArrayList<String> BringBorrowRepInfo() throws SQLException {
		ArrayList<String> FullBorrowRep = new ArrayList<>();

		// Query for Borrowed and Returned Books
		String query1 = "SELECT br1.SubscriberID, br1.BookName, MIN(br1.ActionDate) AS BorrowDate, "
				+ "MIN(br2.ActionDate) AS ReturnDate, br2.AdditionalDetails "
				+ "FROM activityhistory br1 JOIN activityhistory br2 "
				+ "ON br1.SubscriberID = br2.SubscriberID AND br1.BookName = br2.BookName "
				+ "AND br1.ActionType = 'Borrow' AND br2.ActionType = 'Return' AND br1.ActionDate < br2.ActionDate "
				+ "GROUP BY br1.SubscriberID, br1.BookName, br2.AdditionalDetails";

		try (PreparedStatement ps = conn.prepareStatement(query1)) {
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					FullBorrowRep.add(String.format(
							"Subscriber ID: %s    Book Name: %s    Borrow Date: %s    Return Date: %s    Additional Details: %s",
							rs.getString("SubscriberID"), rs.getString("BookName"), rs.getString("BorrowDate"),
							rs.getString("ReturnDate"), rs.getString("AdditionalDetails")));
				}
			}
		}

		// Query for Borrowed but Not Returned Books
		String query2 = "SELECT br1.SubscriberID, br1.BookName, MIN(br1.ActionDate) AS BorrowDate "
				+ "FROM activityhistory br1 LEFT JOIN activityhistory br2 "
				+ "ON br1.SubscriberID = br2.SubscriberID AND br1.BookName = br2.BookName "
				+ "AND br2.ActionType = 'Return' " + "WHERE br1.ActionType = 'Borrow' AND br2.ActionType IS NULL "
				+ "GROUP BY br1.SubscriberID, br1.BookName";

		try (PreparedStatement ps = conn.prepareStatement(query2)) {
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					FullBorrowRep.add(String.format(
							"Subscriber ID: %s    Book Name: %s    Borrow Date: %s    Return Date: __-__-____ __:__:__     Additional Details: N/A",
							rs.getString("SubscriberID"), rs.getString("BookName"), rs.getString("BorrowDate")));
				}
			}
		}

		return FullBorrowRep;
	}
}