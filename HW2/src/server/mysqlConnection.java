package server;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import common.Librarian;
import common.Subscriber1;

public class mysqlConnection {
	private static Connection conn;
	private static int totalCopys;
	public static mysqlConnection instance;

	// Private constructor for singleton pattern
	private mysqlConnection() {
		connectToDB();
	}

	// Singleton getter
	public static synchronized mysqlConnection getInstance() {
		if (instance == null) {
			instance = new mysqlConnection();
		}
		return instance;
	}

	static void connectToDB() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			System.out.println("Driver definition succeed");
		} catch (Exception ex) {
			System.out.println("Driver definition failed");
		}

		try {

			conn = DriverManager.getConnection("jdbc:mysql://localhost/hw2-shitot?serverTimezone=IST", "root",
					"!vex123S");

			System.out.println("SQL connection succeed");
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
	}


	public void update(String id, String p_number, String email) {
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


	public Subscriber1 select(String id) {
		Subscriber1 sub = null;
		String selectQuery = "SELECT * FROM subscriber WHERE subscriber_id = ?"; // Use a placeholder (?) for the
																					// parameter
		try {
			PreparedStatement ps = conn.prepareStatement(selectQuery);
			ps.setString(1, id); // Safely set the parameter value
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				int sub_id = rs.getInt("subscriber_id");
				String sub_name = rs.getString("subscriber_name");
				String phone = rs.getString("subscriber_phone_number");
				String email = rs.getString("subscriber_email");
				String status = rs.getString("Subscription_status");
				String password = rs.getString("password");
				sub = new Subscriber1(sub_id, sub_name, phone, email, status, password);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sub;
	}

	public Subscriber1 searchSubId(String email, String password) {
		Subscriber1 sub = null;
		String searchQuery = "SELECT * FROM subscriber WHERE subscriber_email = ? AND password = ?";
		try (PreparedStatement ps = conn.prepareStatement(searchQuery)) {
			ps.setString(1, email);
			ps.setString(2, password);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					int sub_id = rs.getInt("subscriber_id");
					String sub_name = rs.getString("subscriber_name");
					String phone = rs.getString("subscriber_phone_number");
					String email1 = rs.getString("subscriber_email");
					String status = rs.getString("Subscription_status");
					String password1 = rs.getString("password");
					sub = new Subscriber1(sub_id, sub_name, phone, email1, status, password1);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sub; // no subscriber found
	}

	public Librarian searchLibId(String email, String password) {
		Librarian lib = null;
		String searchQuery = "SELECT * FROM librarian WHERE librarian_email = ? AND librarian_password = ?";
		try (PreparedStatement ps = conn.prepareStatement(searchQuery)) {
			ps.setString(1, email);
			ps.setString(2, password);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					String lib_id = String.valueOf(rs.getInt("librarian_id"));
					String lib_name = rs.getString("librarian_name");
					String email1 = rs.getString("librarian_email");
					String password1 = rs.getString("librarian_password");
					lib = new Librarian(lib_id, lib_name, email1, password1);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lib;
	}

	public Integer getBookAvailability(String bookName) {
		String query = "SELECT copysAvailable FROM books WHERE bookName = ?";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setString(1, bookName);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					int copiesAvailable = rs.getInt("copysAvailable");
					return copiesAvailable;

				} else {
					return -1;
				}
			}
		} catch (SQLException e) {
			System.err.println("Error occurred while fetching book availability: " + e.getMessage());
			e.printStackTrace();
			return -2;
		}
	}

	public String getNearestReturnDate(String bookName) {
		// Query to select the nearest return date for a borrowed book
		String query = "SELECT deadline FROM activityhistory WHERE BookName = ? AND ActionType = 'Borrow' ORDER BY deadline ASC LIMIT 1";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, bookName); // Set the book name in the query
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					// Return the nearest return date
					return rs.getString("deadline");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null; // Return null if no result is found or an error occurs
	}

	public String getSubscriptionStatus(int subscriberId) {
		// Query to check the subscription status of the subscriber by subscriber ID
		String query = "SELECT subscription_status FROM subscriber WHERE subscriber_id = ?";

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, subscriberId); // Set the subscriber ID in the query

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					// Return the subscription status of the subscriber
					return rs.getString("subscription_status");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null; // Return null if no result is found or if there is an error
	}

	public String isAvailable(String bookName) {
		String query = "SELECT copysAvailable, totalCopys FROM books WHERE bookName = ?";
		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, bookName);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				int copysAvailable = rs.getInt("copysAvailable");
				totalCopys = rs.getInt("totalCopys");
				if (copysAvailable > 0)
					return "available";
				else
					return "notAvailable";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "notExist";
	}

	public String canAddOrder(int id, String bookName) {
		// Check availability first
		String availability = isAvailable(bookName);
		if (availability.equals("notExist")) {
			return "notExist";
		}

		// If book exists but not available, check if can be ordered
		if (availability.equals("notAvailable")) {
			int count;
			String canAddQuery = "SELECT COUNT(*) AS count FROM orders WHERE bookName = ?";
			try {
				PreparedStatement stmt = conn.prepareStatement(canAddQuery);
				stmt.setString(1, bookName);
				try (ResultSet rs = stmt.executeQuery()) {
					rs.next();
					count = rs.getInt("count");
					if ((count != 0) && (count == totalCopys))
						return "can't";
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			addOrder(bookName, id);
			addOrderToActivityHistory(bookName, id);
			return "can";
		}

		return "available";
	}

	public void addOrder(String bookName, int id) {
		String addQuery = "INSERT INTO orders (time, bookName, subID) VALUES (?, ?, ?);";
		LocalDateTime now = LocalDateTime.now();
		Timestamp timestamp = Timestamp.valueOf(now);
		try {
			PreparedStatement stmt = conn.prepareStatement(addQuery);
			stmt.setTimestamp(1, timestamp);
			stmt.setString(2, bookName);
			stmt.setInt(3, id);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addOrderToActivityHistory(String bookName, int id) {
		String addQuery = "INSERT INTO activityhistory (SubscriberID, BookName, ActionType, ActionDate) VALUES (?, ?, ?, ?);";
		try {
			PreparedStatement stmt = conn.prepareStatement(addQuery);
			stmt.setInt(1, id);
			stmt.setString(2, bookName);
			stmt.setString(3, "Reservation");
			stmt.setString(4, LocalDateTime.now().toString());
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String canExtend(int id, String bookName) {
		String query = "SELECT deadline FROM activityhistory WHERE SubscriberID = ? AND BookName = ? AND ActionType = 'Borrow';";
		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, id);
			stmt.setString(2, bookName);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					Date deadlineFromDB = rs.getDate("deadline");
					LocalDate deadline = deadlineFromDB.toLocalDate();
					LocalDate today = LocalDate.now();// Get today's date
					long daysDifference = ChronoUnit.DAYS.between(today, deadline); // Calculate the difference in days
					if (daysDifference > 7)
						return "more than 7";
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		boolean orderExists = orderExists(bookName); // check if there is an order for this book
		if (orderExists == true)
			return "order exists";
		addExtension(id, bookName);
		// need to send a message to the librarian
		return "can extend";
	}

	private boolean orderExists(String bookName) {
		String query = "SELECT COUNT(*) AS count FROM orders WHERE bookName = ?;";
		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, bookName);
			try (ResultSet rs = stmt.executeQuery()) {
				rs.next();
				int count = rs.getInt("count");
				if ((count != 0))
					return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// this method updates the Borrow's deadline in more 7 days
	private void addExtension(int id, String bookName) {
		String addQuery = "UPDATE activityhistory SET additionalInfo = ?, deadline = DATE_ADD(deadline, INTERVAL 14 DAY)"
				+ " WHERE SubscriberID = ? AND bookName = ? AND ActionType = 'Borrow';";
		try {
			PreparedStatement stmt = conn.prepareStatement(addQuery);
			stmt.setString(1, "autoExtended");
			stmt.setInt(2, id);
			stmt.setString(3, bookName);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//update the Librarian's messages that the subscirber got extension
		String libQuery = "INSERT INTO lib_messages (note) VALUES (?);";
		LocalDateTime now = LocalDateTime.now();
		Timestamp timestamp = Timestamp.valueOf(now);
		try (PreparedStatement ps = conn.prepareStatement(libQuery)) {
			ps.setString(1, "The subscriber " + id + ", got Auto Extension for 14 more days. Action Date: " + timestamp);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// add addSubcriber
	public void addSubscriber(int subId, String subName, String phone, String email, String status, String password) {
		String insertQuery = "INSERT INTO subscriber (subscriber_id, subscriber_name, subscriber_phone_number, subscriber_email, subscription_status, password) VALUES (?, ?, ?, ?, ?, ?);";
		try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
			ps.setInt(1, subId);
			ps.setString(2, subName);
			ps.setString(3, phone);
			ps.setString(4, email);
			ps.setString(5, status);
			ps.setString(6, password);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList getActivityHistory(String subscriberEmail) {
		System.out.println(subscriberEmail);
		ArrayList<Object> activityHistory = new ArrayList<>();

		// First query to get subscriber ID
		String subscriberQuery = "SELECT subscriber_id FROM subscriber WHERE subscriber_email = ?";

		try (PreparedStatement psSubscriber = conn.prepareStatement(subscriberQuery)) {
			psSubscriber.setString(1, subscriberEmail);

			try (ResultSet rsSubscriber = psSubscriber.executeQuery()) {
				if (rsSubscriber.next()) {
					int subscriberId = rsSubscriber.getInt("subscriber_id");

					// Second query to get activity history using subscriber ID
					String activityQuery = "SELECT * FROM activityhistory WHERE SubscriberID = ?";

					try (PreparedStatement psActivity = conn.prepareStatement(activityQuery)) {
						psActivity.setInt(1, subscriberId);

						try (ResultSet rsActivity = psActivity.executeQuery()) {
							while (rsActivity.next()) {
								String bookName = rsActivity.getString("BookName");
								String actionType = rsActivity.getString("ActionType");
								String actionDate = rsActivity.getString("ActionDate");
								activityHistory.add(
										"Book Name: " + bookName + ", Action: " + actionType + ", Date: " + actionDate);
							}
						}
					}
				} else {
					System.out.println("No subscriber found with email: " + subscriberEmail);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return activityHistory;
	}

	public ArrayList<String> getBorrowHistory(int subscriberId) {
		ArrayList<String> borrowHistory = new ArrayList<>();
		String query = "SELECT * FROM ActivityHistory WHERE SubscriberID = ? AND ActionType = 'Borrow'";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, subscriberId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					String deadline = rs.getString("deadline");
					// System.out.println(deadline);
					String bookName = rs.getString("BookName");
					String actionDate = rs.getString("ActionDate");
					borrowHistory
							.add("Book Name: " + bookName + ",Borrow Date: " + actionDate + ", Deadline: " + deadline);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return borrowHistory;
	}

	public boolean ChangeReturnDate(int subscriberId, String BookName, String OldDate, String NewDate,
			String Librarian_name) {
		String CheckOrderQuery = "SELECT * FROM orders WHERE bookName = ?";
		try (PreparedStatement ps = conn.prepareStatement(CheckOrderQuery)) {
			ps.setString(1, BookName);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return false;
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String formattedDate = now.format(formatter);

		String checkQuery = "SELECT additionalInfo FROM activityhistory WHERE SubscriberID = ? AND BookName = ? AND deadline = ? AND ActionType = 'Borrow'";
		String updateQuery = "UPDATE activityhistory SET deadline = ?, additionalInfo = ? WHERE SubscriberID = ? AND BookName = ? AND deadline = ? AND ActionType = 'Borrow'";

		try (PreparedStatement checkPs = conn.prepareStatement(checkQuery)) {
			// Set parameters for the check query
			checkPs.setInt(1, subscriberId);
			checkPs.setString(2, BookName);
			checkPs.setString(3, OldDate);

			try (ResultSet rs = checkPs.executeQuery()) {
				if (rs.next()) {
					String additionalInfo = rs.getString("additionalInfo");
					if (additionalInfo != null && !additionalInfo.isEmpty()) {
						// Return false if there is already an entry in additionalInfo
						return false;
					}
				}
			}

			// Proceed with the update query if no entry exists in additionalInfo
			try (PreparedStatement updatePs = conn.prepareStatement(updateQuery)) {
				updatePs.setString(1, NewDate);
				updatePs.setString(2, "Extended by: " + Librarian_name + " , at: " + formattedDate);
				updatePs.setInt(3, subscriberId);
				updatePs.setString(4, BookName);
				updatePs.setString(5, OldDate);

				int result = updatePs.executeUpdate();
				return result > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean isSubscriberExist(int id) {
		String selectQuery = "SELECT 1 FROM subscriber WHERE subscriber_id = ?";
		try (PreparedStatement ps = conn.prepareStatement(selectQuery)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean decrementBookAvailability(String bookName) {

		int currentAvailability = getBookAvailability(bookName);
		if (currentAvailability <= 0) {
			return false;
		}
		String updateQuery = "UPDATE books SET copysAvailable = copysAvailable - 1 WHERE bookName = ?";
		try (PreparedStatement ps = conn.prepareStatement(updateQuery)) {
			ps.setString(1, bookName);
			int rowsUpdated = ps.executeUpdate();

			return rowsUpdated > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void addActivityToHistory(int subscriberId, String bookName) {
		// SQL query to insert a record into the activityhistory table
		String insertQuery = "INSERT INTO activityhistory (SubscriberID, BookName, ActionType, ActionDate, Deadline) VALUES (?, ?, ?, ?, ?)";

		try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
			// Set values for the columns in the table
			ps.setInt(1, subscriberId); // SubscriberID (Subscriber's ID)
			ps.setString(2, bookName); // BookName (Name of the book)
			ps.setString(3, "borrow"); // ActionType (The action is "borrow")

			// ActionDate (Date of the action) - the current date
			LocalDate currentDate = LocalDate.now();
			ps.setDate(4, Date.valueOf(currentDate)); // Convert the current date to `DATE` type

			// Deadline (The due date - two weeks later)
			LocalDate deadlineDate = currentDate.plusWeeks(2); // Add two weeks
			ps.setDate(5, Date.valueOf(deadlineDate)); // Convert the deadline date to `DATE` type

			// Execute the insertion into the database
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace(); // Print any SQL exceptions that occur
		}
	}

	public ArrayList<String> getAllBookNames() {
		ArrayList<String> bookNames = new ArrayList<>();
		String query = "SELECT bookName FROM books";

		try (PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				String name = rs.getString("bookName");
				bookNames.add(name);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return bookNames;
	}

	public ArrayList<String> getBorrowedBooks(int id) {
		System.out.println(id);
		ArrayList<String> borrowedBooks = new ArrayList<>();
		String query = "SELECT BookName FROM activityhistory WHERE SubscriberID = ? AND ActionType = 'Borrow'";

		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String name = rs.getString("BookName");
				borrowedBooks.add(name);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println(borrowedBooks);
		return borrowedBooks;
	}

	public ArrayList<String> fetchBooksByCriteria(String criteria, String value) {
		String query = "SELECT bookName FROM books WHERE " + criteria + " LIKE ?";
		ArrayList<String> books = new ArrayList<>();
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, "%" + value + "%");
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				books.add(rs.getString("bookName"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return books;

	}

	public ArrayList<String> BringBorrowRepInfo(String SelectedMonth, String SelectedYear) throws SQLException {
		ArrayList<String> FullBorrowRep = new ArrayList<>();
		FullBorrowRep.add("borrow report");

		// Query for Borrowed and Returned Books
		String query1 = "SELECT br1.SubscriberID, br1.BookName, MIN(br1.ActionDate) AS BorrowDate, "
				+ "MIN(br2.ActionDate) AS ReturnDate, br1.deadline , br2.additionalInfo "
				+ "FROM activityhistory br1 JOIN activityhistory br2 "
				+ "ON br1.SubscriberID = br2.SubscriberID AND br1.BookName = br2.BookName "
				+ "AND br1.ActionType = 'Borrow' AND br2.ActionType = 'Return' AND br1.ActionDate < br2.ActionDate "
				+ "WHERE DATE_FORMAT(br1.ActionDate, '%Y-%m') = ? "
				+ "GROUP BY br1.SubscriberID, br1.BookName, br1.deadline , br2.additionalInfo";

		try (PreparedStatement ps = conn.prepareStatement(query1)) {
			// Set the SelectedMonth parameter for the query
			ps.setString(1, SelectedYear + "-" + SelectedMonth);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					// Check if the book was returned late or on time
					String lateStatus = rs.getString("additionalInfo");

					// Add the information to the list, including the late status
					FullBorrowRep.add(String.format(
							"Subscriber ID: %s , Book Name: %s , Borrow Date: %s , Return Date: %s , Deadline: %s , Status: %s",
							rs.getString("SubscriberID"), rs.getString("BookName"), rs.getString("BorrowDate"),
							rs.getString("ReturnDate"), rs.getString("Deadline"), lateStatus));
				}
			}
		}

		// Query for Borrowed but Not Returned Books
		String query2 = "SELECT br1.SubscriberID, br1.BookName, MIN(br1.ActionDate) AS BorrowDate, br1.deadline "
				+ "FROM activityhistory br1 LEFT JOIN activityhistory br2 "
				+ "ON br1.SubscriberID = br2.SubscriberID AND br1.BookName = br2.BookName "
				+ "AND br2.ActionType = 'Return' " + "WHERE br1.ActionType = 'Borrow' AND br2.ActionType IS NULL "
				+ "AND DATE_FORMAT(br1.ActionDate, '%Y-%m') = ? "
				+ "GROUP BY br1.SubscriberID, br1.BookName, br1.deadline";

		try (PreparedStatement ps = conn.prepareStatement(query2)) {
			// Set the SelectedMonth parameter for the query
			ps.setString(1, SelectedYear + "-" + SelectedMonth);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					FullBorrowRep.add(String.format(
							"Subscriber ID: %s , Book Name: %s , Borrow Date: %s , Return Date: __-__-____ __ , Deadline: %s , Status: Not returned yet",
							rs.getString("SubscriberID"), rs.getString("BookName"), rs.getString("BorrowDate"),
							rs.getString("deadline")));
				}
			}
		}

		return FullBorrowRep;
	}

	public ArrayList<String> BringStatusRepInfo(String selectedMonth, String selectedYear) throws SQLException {
		// Input validation
		if (selectedMonth == null || selectedYear == null) {
			throw new IllegalArgumentException("Selected month and year cannot be null.");
		}

		// Initialize list to store subscribers who are late with submissions
		ArrayList<Integer> LateSubs = new ArrayList<>();

		// First query: Find subscribers who are more than 7 days late in the selected
		// month
		String query1 = "SELECT SubscriberID " + "FROM activityhistory " + "WHERE additionalInfo LIKE '% days late' "
				+ "AND CAST(SUBSTRING_INDEX(additionalInfo, ' ', 1) AS UNSIGNED) > 7 "
				+ "AND DATE_FORMAT(ActionDate, '%Y-%m') = ?";
		try (PreparedStatement ps = conn.prepareStatement(query1)) {
			String selectedDate = selectedYear + "-" + selectedMonth;
			ps.setString(1, selectedDate);

			// Execute query and collect late subscriber IDs
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					int subscriberID = rs.getInt("SubscriberID");
					LateSubs.add(subscriberID);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Initialize list to store subscribers who are on time
		ArrayList<Integer> OnTimeSubs = new ArrayList<>();

		// Second query: Find subscribers who are NOT late (complement of late
		// subscribers)
		String query2 = "SELECT subscriber_id " + "FROM subscriber " + "WHERE subscriber_id NOT IN ("
				+ " SELECT SubscriberID " + " FROM activityhistory " + " WHERE additionalInfo LIKE '% days late' "
				+ " AND CAST(SUBSTRING_INDEX(additionalInfo, ' ', 1) AS UNSIGNED) > 7 " + " AND DATE_FORMAT(ActionDate"
				+ ", '%Y-%m') = ?" + ")";
		try (PreparedStatement ps = conn.prepareStatement(query2)) {
			String selectedDate = selectedYear + "-" + selectedMonth;
			ps.setString(1, selectedDate);

			// Execute query and collect on-time subscriber IDs
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					int subscriberID = rs.getInt("subscriber_id");
					OnTimeSubs.add(subscriberID);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Initialize ArrayList to store the final formatted results
		ArrayList<String> statusReport = new ArrayList<>();
		statusReport.add("status report");

		// Process late subscribers (Frozen status)
		// Build dynamic IN clause for late subscribers query
		String query3 = "SELECT subscriber_name, subscriber_id FROM subscriber WHERE subscriber_id IN (";
		for (int i = 0; i < LateSubs.size(); i++) {
			query3 += "?";
			if (i < LateSubs.size() - 1) {
				query3 += ", ";
			}
		}
		query3 += ")";

		// Execute query for late subscribers if any exist
		if (!LateSubs.isEmpty()) {
			try (PreparedStatement ps = conn.prepareStatement(query3)) {
				// Set parameters for IN clause
				for (int i = 0; i < LateSubs.size(); i++) {
					ps.setInt(i + 1, LateSubs.get(i));
				}

				// Process results and add formatted strings to statusReport
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						String subscriberName = rs.getString("subscriber_name");
						int subscriberId = rs.getInt("subscriber_id");
						statusReport.add(
								"Subscriber name: " + subscriberName + " , ID: " + subscriberId + " , Status: Frozen");
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		// Process on-time subscribers (Active status)
		// Build dynamic IN clause for on-time subscribers query
		String query4 = "SELECT subscriber_name, subscriber_id FROM subscriber WHERE subscriber_id IN (";
		for (int i = 0; i < OnTimeSubs.size(); i++) {
			query4 += "?";
			if (i < OnTimeSubs.size() - 1) {
				query4 += ", ";
			}
		}
		query4 += ")";

		// Execute query for on-time subscribers if any exist
		if (!OnTimeSubs.isEmpty()) {
			try (PreparedStatement ps = conn.prepareStatement(query4)) {
				// Set parameters for IN clause
				for (int i = 0; i < OnTimeSubs.size(); i++) {
					ps.setInt(i + 1, OnTimeSubs.get(i));
				}

				// Process results and add formatted strings to statusReport
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						String subscriberName = rs.getString("subscriber_name");
						int subscriberId = rs.getInt("subscriber_id");
						statusReport.add(
								"Subscriber name: " + subscriberName + " , ID: " + subscriberId + " , Status: Active");
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return statusReport;
	}

	public String BringBarCodeBookName(int bookId) throws SQLException {
		String bookName = "";
		String query = "SELECT bookName FROM books WHERE BookID = ?";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, bookId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String name = rs.getString("bookName");
				bookName = name;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return bookName;

	}

	public ArrayList<Object> getBorrowDateAndReturnDate(String borrowerId, String bookName) throws SQLException {
		ArrayList<Object> borrowAndReturnDate = new ArrayList<>();
		PreparedStatement ps = conn.prepareStatement(
				"SELECT ActionDate,deadline FROM activityhistory where SubscriberID=? AND BookName=? AND ActionType='Borrow'");

		ps.setString(1, borrowerId);
		ps.setString(2, bookName);

		ResultSet resultSet = ps.executeQuery();
		if (resultSet.last()) {
			borrowAndReturnDate.add(resultSet.getString(1));
			borrowAndReturnDate.add(resultSet.getString(2));
			return borrowAndReturnDate;
		}
		return null;

	}

	// method that checks in the database if there is a certain borrower that
	// borrowed the selected book
	// using "Exist" if there is a row that match the borrower's ID and book's name
	// then
	// the method return true
	public Boolean checkIfBorrowerFound(String borrowerId, String bookName) throws SQLException {

		PreparedStatement ps = conn.prepareStatement(
				"SELECT EXISTS(SELECT * FROM activityhistory where SubscriberID=? AND BookName=? AND ActionType='Borrow')");
		ps.setString(1, borrowerId);
		ps.setString(2, bookName);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			return rs.getBoolean(1); // Retrieve the result from the first column
		}
		return false;
	}

	public Boolean checkBookAlreadyReturned(String borrowerId, String bookName) throws SQLException {

		String query = " SELECT " + "COUNT(CASE WHEN ActionType ='Borrow' THEN 1 END) AS borrow_count,"
				+ "COUNT(CASE WHEN ActionType = 'Return' THEN 1 END) AS return_count "
				+ "FROM activityhistory WHERE SubscriberID=? AND BookName=? ";

		int countBorrowed = 0;
		int countReturned = 0;

		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setString(1, borrowerId);
			ps.setString(2, bookName);

			try (ResultSet rs = ps.executeQuery()) {

				if (rs.next()) {
					countBorrowed = rs.getInt("borrow_count");
					countReturned = rs.getInt("return_count");
				}

				// Return true if the book has been returned (when borrowed and returned count
				// are the same)
				return countBorrowed == countReturned;
			}
		}
	}

	public Boolean insertReturnBookRowInActivityHistory(String borrowerId, String bookName, String dateDifference,
			Boolean isLate) throws SQLException {

		int borrowerIdAsInt = Integer.parseInt(borrowerId);
		int rowsAffected = 0;
		LocalDate actionDate = LocalDate.now();
		String insertQuary = "INSERT INTO activityhistory (SubScriberID, BookName, ActionType, ActionDate,"
				+ "additionalInfo) VALUES (?,?,?,?,?)";
		PreparedStatement ps = conn.prepareStatement(insertQuary);

		ps.setInt(1, borrowerIdAsInt);
		ps.setString(2, bookName);
		ps.setString(3, "Return");
		ps.setDate(4, Date.valueOf(actionDate));
		ps.setString(5, dateDifference);

		rowsAffected = ps.executeUpdate();

		return rowsAffected > 0;
	}

	public Boolean updateSubscriberStatusToFrozen(String subscriberId, String IsFrozen) throws SQLException {

		String insertQuary = "UPDATE subscriber SET subscription_status=? WHERE subscriber_id = ?";
		PreparedStatement ps = conn.prepareStatement(insertQuary);

		ps.setString(1, IsFrozen);
		ps.setString(2, subscriberId);

		if (ps.executeUpdate() == 1)
			return true;

		return false;

	}

	public Boolean incrimentBookAvailability(String bookName) throws SQLException {
		String insertQuary = "UPDATE books SET copysAvailable=copysAvailable + 1 WHERE bookName = ?";
		PreparedStatement ps = conn.prepareStatement(insertQuary);
		ps.setString(1, bookName);

		if (ps.executeUpdate() == 1)
			return true;

		return false;
	}

	public String checkIsFrozen(int id) {
		String status = null;
		String query = "SELECT subscription_status FROM subscriber WHERE subscriber_id = ?;";
		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, id);
			ResultSet resultSet = stmt.executeQuery();
			resultSet.next();
			status = resultSet.getString("subscription_status");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return status;
	}

	// UPDATE arrivalTime in orders table of the oldest time for the given bookName
	// and add a message to messages table
	public boolean addArrivalTimeToOrders(String bookName) {
		int subID = 0;
		String query = "SELECT subID FROM orders WHERE bookName = ? AND time = "
				+ "(SELECT MIN(time) FROM orders WHERE bookName = ?);";
		String updateQuery = "UPDATE orders SET arrivalTime = CURDATE() WHERE subID = ?;";
		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, bookName);
			stmt.setString(2, bookName);
			ResultSet resultSet = stmt.executeQuery();
			if (resultSet.next())
				subID = resultSet.getInt("subID");
			else
				return false; // no one has ordered this book
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
			updateStmt.setInt(1, subID);
			updateStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		addArrivedMessage(subID, bookName); // add an arrived message in 'messages' table
		return true;
	}

	public void addArrivedMessage(int subID, String bookName) {
		String query = "INSERT INTO sub_messages (subID, note) VALUES (?, ?);";		
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, subID);
			ps.setString(2,
					"Your order of the book: '" + bookName + "' has arrived! Please take it in less than two days");
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<String> getOrdersOfSubscriber(int id) {
		ArrayList<String> orders = new ArrayList<>();
		String query = "SELECT bookName FROM orders WHERE subID = ? and arrivalTime IS NOT NULL;";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					String bookName = rs.getString("bookName");
					orders.add(bookName);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return orders;
	}

	// method to DELETE statement for each order that has been canceled
	public void deleteOrders(Map<Integer, String> ordersToDelete) {
		String deleteQuery = "DELETE FROM orders WHERE subID = ? AND bookName = ?;";
		try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
			// Loop through the map and delete the relevant orders
			for (Map.Entry<Integer, String> entry : ordersToDelete.entrySet()) {
				deleteStmt.setInt(1, entry.getKey());
				deleteStmt.setString(2, entry.getValue());
				deleteStmt.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// UPDATE the subscribers 'notes' in messages table that their order has arrived
	// and it has been more than 2 days
	public void timeDidntTakeOrder() {
		Map<Integer, String> ordersToDelete = new HashMap<>();
		String query = "SELECT subID, bookName FROM orders WHERE arrivalTime IS NOT NULL AND arrivalTime < CURDATE() - INTERVAL 2 DAY;";
		String updateMessage = "UPDATE sub_messages SET note = ? WHERE subID = ?;";
		try {
			PreparedStatement checkOrderStmt = conn.prepareStatement(query);
			PreparedStatement updateMessageStmt = conn.prepareStatement(updateMessage);
			ResultSet resultSet = checkOrderStmt.executeQuery();
			while (resultSet.next()) {
				int subID = resultSet.getInt("subID");
				String bookName = resultSet.getString("bookName");
				// Update the notes column in the messages table
				updateMessageStmt.setString(1, "Your order of the book: '" + bookName + "' is canceled!");
				updateMessageStmt.setInt(2, subID);
				updateMessageStmt.executeUpdate();

				ordersToDelete.put(subID, bookName); // add the order to the HashMap
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		deleteOrders(ordersToDelete); // call the method to delete the non taken orders from the DB
	}
	public void notifyBeforeReturnDeadline() {
	    // SQL query to fetch subscribers with books borrowed and whose deadline is the next day
	    String query = "SELECT SubscriberID, BookName, deadline, reminderSent " +
	                   "FROM activityhistory " +
	                   "WHERE ActionType = 'borrow' " +
	                   "AND deadline = CURDATE() + INTERVAL 1 DAY " +
	                   "AND reminderSent = FALSE;"; // Only fetch records where reminderSent is FALSE

	    String insertMessage = "INSERT INTO sub_messages (subID, note) VALUES (?, ?);";
	    String updateReminderSent = "UPDATE activityhistory SET reminderSent = TRUE WHERE SubscriberID = ? AND BookName = ?;";

	    try {
	        // Prepare the SQL statement to check for deadlines
	        PreparedStatement checkDeadlineStmt = conn.prepareStatement(query);
	        // Prepare the SQL statement to insert notification messages
	        PreparedStatement insertMessageStmt = conn.prepareStatement(insertMessage);
	        // Prepare the SQL statement to update reminderSent column
	        PreparedStatement updateReminderSentStmt = conn.prepareStatement(updateReminderSent);
	        
	        // Execute the query and get the result set
	        ResultSet resultSet = checkDeadlineStmt.executeQuery();

	        // Process each record in the result set
	        while (resultSet.next()) {
	            int subID = resultSet.getInt("SubscriberID"); 
	            String bookName = resultSet.getString("BookName"); 
	            Date deadline = resultSet.getDate("deadline"); 

	            // Create a notification message for the subscriber
	            String note = "Reminder: The book \"" + bookName + "\" must be returned by " + deadline + ".";

	            // Insert the notification message into the sub_messages table
	            insertMessageStmt.setInt(1, subID);
	            insertMessageStmt.setString(2, note);
	            insertMessageStmt.executeUpdate();

	            // Update the reminderSent column to TRUE to indicate that a reminder was sent
	            updateReminderSentStmt.setInt(1, subID);
	            updateReminderSentStmt.setString(2, bookName);
	            updateReminderSentStmt.executeUpdate();

	        }
	    } catch (SQLException e) {
	        e.printStackTrace(); // Print the stack trace in case of an exception
	    }
	}




	public ArrayList<String> subscriberMessages(int subID) {
		ArrayList<String> messages = new ArrayList<>();
		String query = "SELECT note FROM sub_messages WHERE subID = ?;";

		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, subID);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String message = rs.getString("note");
				messages.add(message);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return messages;
	}

}