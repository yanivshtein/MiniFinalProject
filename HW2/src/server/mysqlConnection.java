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

/**
 * The {@code mysqlConnection} class provides methods for managing the database operations 
 * related to subscribers, librarians, and other entities in the library system. 
 * It uses the Singleton pattern to ensure a single database connection instance.
 */
public class mysqlConnection {
    private static Connection conn;
    private static int totalCopys;
    public static mysqlConnection instance;

    /**
     * Private constructor to initialize the database connection. 
     * This ensures that only one instance of the connection is created.
     */
    private mysqlConnection() {
        connectToDB();
    }

    /**
     * Retrieves the Singleton instance of the {@code mysqlConnection} class.
     * If the instance doesn't exist, it initializes a new one.
     *
     * @return The Singleton instance of the {@code mysqlConnection} class.
     */
    public static synchronized mysqlConnection getInstance() {
        if (instance == null) {
            instance = new mysqlConnection();
        }
        return instance;
    }

    /**
     * Establishes a connection to the MySQL database. 
     * Logs success or failure messages to the console.
     */
    static void connectToDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            System.out.println("Driver definition succeed");
        } catch (Exception ex) {
            System.out.println("Driver definition failed");
        }

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/hw2-shitot?serverTimezone=Asia/Jerusalem", "root", "yaniv1234");
            System.out.println("SQL connection succeed");
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    /**
     * Updates the subscriber's phone number and email in the database.
     *
     * @param id        The subscriber ID.
     * @param p_number  The new phone number of the subscriber.
     * @param email     The new email address of the subscriber.
     */
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

    /**
     * Retrieves a subscriber's details by their ID.
     *
     * @param id The subscriber ID.
     * @return A {@code Subscriber1} object with the subscriber's details, or an empty object if not found.
     */
    public Subscriber1 select(String id) {
        Subscriber1 sub = null;
        String selectQuery = "SELECT * FROM subscriber WHERE subscriber_id = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(selectQuery);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int sub_id = rs.getInt("subscriber_id");
                String sub_name = rs.getString("subscriber_name");
                String phone = rs.getString("subscriber_phone_number");
                String email = rs.getString("subscriber_email");
                String status = rs.getString("Subscription_status");
                String password = rs.getString("password");
                sub = new Subscriber1(sub_id, sub_name, phone, email, status, password);
            } else {
                sub = new Subscriber1(0, "", "", "", "", "");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sub;
    }

    /**
     * Searches for a subscriber by their email and password.
     *
     * @param email    The subscriber's email.
     * @param password The subscriber's password.
     * @return A {@code Subscriber1} object if the subscriber exists, or null otherwise.
     */
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
        return sub;
    }

    /**
     * Searches for a librarian by their email and password.
     *
     * @param email    The librarian's email.
     * @param password The librarian's password.
     * @return A {@code Librarian} object if the librarian exists, or null otherwise.
     */
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

    /**
     * Retrieves the number of available copies of a specific book.
     *
     * @param bookName The name of the book to check availability for.
     * @return The number of available copies if the book exists, -1 if the book does not exist, 
     *         or -2 if an error occurred during the query execution.
     */
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

    /**
     * Retrieves the nearest return date for a borrowed book.
     *
     * @param bookName The name of the book to find the nearest return date for.
     * @return A {@code String} representing the nearest return date if found, or {@code null} 
     *         if no results are found or an error occurs.
     */
    public String getNearestReturnDate(String bookName) {
        String query = "SELECT deadline FROM activityhistory WHERE BookName = ? AND ActionType = 'Borrow' ORDER BY deadline ASC LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, bookName); // Set the book name in the query
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("deadline");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves the subscription status of a specific subscriber.
     *
     * @param subscriberId The ID of the subscriber.
     * @return The subscription status as a {@code String}, or {@code null} if no result is found or an error occurs.
     */
    public String getSubscriptionStatus(int subscriberId) {
        String query = "SELECT subscription_status FROM subscriber WHERE subscriber_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, subscriberId); // Set the subscriber ID in the query
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("subscription_status");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Checks if a specific book is available, not available, or does not exist.
     *
     * @param bookName The name of the book to check.
     * @return "available" if copies are available, "notAvailable" if no copies are available, 
     *         or "notExist" if the book does not exist in the database.
     */
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

    /**
     * Determines if an order can be added for a specific book. If the book is available, an order is not needed. 
     * If the book is not available but can be ordered, the order is added. If the book cannot be ordered, the 
     * appropriate status is returned.
     *
     * @param id       The subscriber ID placing the order.
     * @param bookName The name of the book to order.
     * @return "notExist" if the book does not exist, "can't" if no more orders can be placed, 
     *         "can" if the order was successfully placed, or "available" if the book is already available.
     */
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
    
    
    /** Adds a new order to the orders table.
     * @param bookName The book's name being ordered.
     * @param id       The subscriber's ID.
     * Inserts the current timestamp, book name, and subscriber ID into the orders table.
     * Prints the stack trace if a database error occurs.
     * */
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

	/**
	 * Adds an order entry to the activity history for a specific subscriber.
	 *
	 * @param bookName The name of the book being ordered.
	 * @param id       The ID of the subscriber placing the order.
	 */
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

	/**
	 * Checks whether a subscriber can extend the borrow period for a specific book.
	 *
	 * @param id       The subscriber's ID.
	 * @param bookName The name of the book for which the extension is requested.
	 * @return A {@code String} indicating the result: "more than 7" if the deadline is too far, 
	 *         "order exists" if an order exists for the book, or "can extend" if the extension is possible.
	 */
	public String canExtend(int id, String bookName) {
	    String query = "SELECT deadline FROM activityhistory WHERE SubscriberID = ? AND BookName = ? AND ActionType = 'Borrow' AND hasReturned = 0;";
	    try {
	        PreparedStatement stmt = conn.prepareStatement(query);
	        stmt.setInt(1, id);
	        stmt.setString(2, bookName);
	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                Date deadlineFromDB = rs.getDate("deadline");
	                LocalDate deadline = deadlineFromDB.toLocalDate();
	                LocalDate today = LocalDate.now();
	                long daysDifference = ChronoUnit.DAYS.between(today, deadline);
	                if (daysDifference > 7) {
	                    return "more than 7";
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    boolean orderExists = orderExists(bookName);
	    if (orderExists) {
	        return "order exists";
	    }
	    addExtension(id, bookName);
	    return "can extend";
	}

	/**
	 * Checks if there is an existing order for a specific book.
	 *
	 * @param bookName The name of the book to check.
	 * @return {@code true} if an order exists for the book; otherwise, {@code false}.
	 */
	private boolean orderExists(String bookName) {
	    String query = "SELECT COUNT(*) AS count FROM orders WHERE bookName = ?;";
	    try {
	        PreparedStatement stmt = conn.prepareStatement(query);
	        stmt.setString(1, bookName);
	        try (ResultSet rs = stmt.executeQuery()) {
	            rs.next();
	            int count = rs.getInt("count");
	            return count > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}

	/**
	 * Adds an extension to a borrow period for a specific book by a subscriber.
	 * Updates the deadline and notifies the librarian.
	 *
	 * @param id       The subscriber's ID.
	 * @param bookName The name of the book for which the borrow period is extended.
	 */
	private void addExtension(int id, String bookName) {
	    String addQuery = "UPDATE activityhistory SET additionalInfo = ?, deadline = DATE_ADD(deadline, INTERVAL 7 DAY)"
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

	    String libQuery = "INSERT INTO lib_messages (note) VALUES (?);";
	    LocalDate currentDate = LocalDate.now();
	    Date sqlDate = Date.valueOf(currentDate);
	    try (PreparedStatement ps = conn.prepareStatement(libQuery)) {
	        ps.setString(1, "The subscriber " + id + ", got Auto Extension for 14 more days. Action Date: " + sqlDate);
	        ps.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	/**
	 * Adds a new subscriber to the database.
	 *
	 * @param subId    The subscriber's ID.
	 * @param subName  The subscriber's name.
	 * @param phone    The subscriber's phone number.
	 * @param email    The subscriber's email address.
	 * @param status   The subscription status of the subscriber.
	 * @param password The subscriber's password.
	 */
	public void addSubscriber(String subName, String phone, String email, String status, String password) {
	    String insertQuery = "INSERT INTO subscriber (subscriber_name, subscriber_phone_number, subscriber_email, subscription_status, password, join_date) VALUES (?, ?, ?, ?, ?,?);";
	    try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
	        ps.setString(1, subName);
	        ps.setString(2, phone);
	        ps.setString(3, email);
	        ps.setString(4, status);
	        ps.setString(5, password);
			LocalDate currentDate = LocalDate.now();
			ps.setDate(6, Date.valueOf(currentDate));
	        ps.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	/**
	 * Retrieves the activity history of a subscriber based on their email address.
	 *
	 * @param subscriberEmail The email address of the subscriber.
	 * @return An {@code ArrayList} containing the subscriber's activity history in formatted strings.
	 */
	public ArrayList getActivityHistory(String subscriberEmail) {
	    ArrayList<Object> activityHistory = new ArrayList<>();
	    String subscriberQuery = "SELECT subscriber_id FROM subscriber WHERE subscriber_email = ?";

	    try (PreparedStatement psSubscriber = conn.prepareStatement(subscriberQuery)) {
	        psSubscriber.setString(1, subscriberEmail);

	        try (ResultSet rsSubscriber = psSubscriber.executeQuery()) {
	            if (rsSubscriber.next()) {
	                int subscriberId = rsSubscriber.getInt("subscriber_id");

	                String activityQuery = "SELECT * FROM activityhistory WHERE SubscriberID = ? ORDER BY ActionDate DESC";
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
	            } else {}
	            
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return activityHistory;
	}

	/**
	 * Retrieves the borrow history of a subscriber, including both returned and unreturned books.
	 *
	 * @param subscriberID The ID of the subscriber.
	 * @return A list of borrow history records, each containing book name, borrow date, return date, deadline, and status.
	 * @throws SQLException If a database access error occurs.
	 */
	public ArrayList<String> getBorrowHistory(int subscriberID) throws SQLException {
	    ArrayList<String> borrowHistory = new ArrayList<>();

	    // Query for books that are borrowed and returned
	    String query1 = "SELECT br1.BookName, MIN(br1.ActionDate) AS BorrowDate, MIN(br2.ActionDate) AS ReturnDate, br1.deadline, br2.additionalInfo "
	                  + "FROM activityhistory br1 JOIN activityhistory br2 "
	                  + "ON br1.SubscriberID = br2.SubscriberID AND br1.BookName = br2.BookName "
	                  + "AND br1.ActionType = 'Borrow' AND br2.ActionType = 'Return' AND br1.ActionDate < br2.ActionDate "
	                  + "WHERE br1.SubscriberID = ? "
	                  + "GROUP BY br1.BookName, br1.deadline, br2.additionalInfo";

	    try (PreparedStatement ps = conn.prepareStatement(query1)) {
	        ps.setInt(1, subscriberID);
	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                borrowHistory.add(String.format("%s,%s,%s,%s,%s",
	                    rs.getString("BookName"), rs.getString("BorrowDate"),
	                    rs.getString("ReturnDate"), rs.getString("deadline"),
	                    rs.getString("additionalInfo")));
	            }
	        }
	    }

	    // Query for books that are borrowed but not returned
	    String query2 = "SELECT br1.BookName, MIN(br1.ActionDate) AS BorrowDate, br1.deadline "
	                  + "FROM activityhistory br1 LEFT JOIN activityhistory br2 "
	                  + "ON br1.SubscriberID = br2.SubscriberID AND br1.BookName = br2.BookName AND br2.ActionType = 'Return' "
	                  + "WHERE br1.ActionType = 'Borrow' AND br2.ActionType IS NULL AND br1.SubscriberID = ? "
	                  + "GROUP BY br1.BookName, br1.deadline";

	    try (PreparedStatement ps = conn.prepareStatement(query2)) {
	        ps.setInt(1, subscriberID);
	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                borrowHistory.add(String.format("%s,%s, __-__-____,%s,Not returned yet",
	                    rs.getString("BookName"), rs.getString("BorrowDate"),
	                    rs.getString("deadline")));
	            }
	        }
	    }

	    return borrowHistory;
	}

	/**
	 * Changes the return date of a borrowed book for a subscriber if no conflicts exist.
	 *
	 * @param subscriberId The ID of the subscriber.
	 * @param BookName     The name of the borrowed book.
	 * @param OldDate      The current return deadline.
	 * @param NewDate      The new return deadline.
	 * @param Librarian_name The name of the librarian making the change.
	 * @return True if the return date was successfully updated; otherwise, false.
	 */
	public boolean ChangeReturnDate(int subscriberId, String BookName, String OldDate, String NewDate, String Librarian_name) {
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
	        checkPs.setInt(1, subscriberId);
	        checkPs.setString(2, BookName);
	        checkPs.setString(3, OldDate);

	        try (ResultSet rs = checkPs.executeQuery()) {
	            if (rs.next()) {
	                String additionalInfo = rs.getString("additionalInfo");
	                if (additionalInfo != null && !additionalInfo.isEmpty()) {
	                    return false;
	                }
	            }
	        }

	        try (PreparedStatement updatePs = conn.prepareStatement(updateQuery)) {
	            updatePs.setString(1, NewDate);
	            updatePs.setString(2, "Extended by: " + Librarian_name + " , at: " + formattedDate);
	            updatePs.setInt(3, subscriberId);
	            updatePs.setString(4, BookName);
	            updatePs.setString(5, OldDate);
	            return updatePs.executeUpdate() > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	/**
	 * Checks if a subscriber exists in the database by their ID.
	 * @param id the ID of the subscriber to check.
	 * @return {@code true} if the subscriber exists, {@code false} otherwise.
	 * Returns {@code false} also in case of a database error.
	 */
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

	/**
	 * Decrements the availability of a specific book in the database by one.
	 * @param bookName the name of the book whose availability is to be decremented.
	 * @return {@code true} if the availability was successfully decremented,
	 * {@code false} if the book is unavailable or if an error occurs.
	 */
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

	/**	 
	 * Records a borrowing activity in the activityhistory table.	
	 * @param subscriberId The ID of the subscriber borrowing the book.
	 * @param bookName     The name of the book being borrowed.
	 * This method logs a "borrow" action for a given subscriber and book, including the
	 * current date as the action date and a deadline set to two weeks from the current date.
	 * If any database error occurs, the stack trace is printed for debugging purposes.
	 * */
	public void addActivityToHistory(int subscriberId, String bookName) {
		String insertQuery = "INSERT INTO activityhistory (SubscriberID, BookName, ActionType, ActionDate, Deadline) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
			ps.setInt(1, subscriberId); 
			ps.setString(2, bookName); 
			ps.setString(3, "borrow"); 
			LocalDate currentDate = LocalDate.now();
			ps.setDate(4, Date.valueOf(currentDate)); // Convert the current date to `DATE` type
			// Deadline (The due date - two weeks later)
			LocalDate deadlineDate = currentDate.plusWeeks(2); // Add two weeks
			ps.setDate(5, Date.valueOf(deadlineDate)); // Convert the deadline date to `DATE` type
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace(); 
		}
	}

	/**
	 * Retrieves the names of all books from the database.
	 *
	 * @return an ArrayList containing the names of all books.
	 */
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

	/**
	 * Retrieves the names of books borrowed by a specific subscriber.
	 *
	 * @param id the ID of the subscriber.
	 * @return an ArrayList containing the names of books borrowed by the subscriber.
	 */
	public ArrayList<String> getBorrowedBooks(int id) {
		ArrayList<String> borrowedBooks = new ArrayList<>();
		String query = "SELECT BookName FROM activityhistory WHERE SubscriberID = ? AND ActionType = 'Borrow' AND hasReturned = 0";

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
		return borrowedBooks;
	}

	/**
	 * Retrieves books from the database that match a specified search criteria.
	 *
	 * @param criteria the column name to search (e.g., "author", "category").
	 * @param value    the value to match in the specified column.
	 * @return an ArrayList containing the names of books matching the criteria.
	 */
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

	/**
	 * Retrieves a borrow report for a specific month and year.
	 *
	 * @param selectedMonth the selected month (e.g., "01" for January).
	 * @param selectedYear  the selected year (e.g., "2025").
	 * @return an ArrayList of formatted strings containing the borrow report details.
	 * @throws SQLException if an error occurs while querying the database.
	 */
	public ArrayList<String> BringBorrowRepInfo(String selectedMonth, String selectedYear) throws SQLException {
        ArrayList<String> FullBorrowRep = new ArrayList<>();
        FullBorrowRep.add("borrow report");

        String selectedDate = selectedYear + "-" + selectedMonth;

        String query = 
        	    "SELECT br1.SubscriberID, br1.BookName, " +
        	    "MIN(br1.ActionDate) AS BorrowDate, " +
        	    "COALESCE(MIN(br2.ActionDate), '__-__-____ __') AS ReturnDate, " +
        	    "br1.deadline, " +
        	    "COALESCE(MAX(br2.additionalInfo), 'Not returned yet') AS Status " + // Using MAX to ensure aggregation
        	    "FROM activityhistory br1 " +
        	    "LEFT JOIN activityhistory br2 " +
        	    "ON br1.SubscriberID = br2.SubscriberID " +
        	    "AND br1.BookName = br2.BookName " +
        	    "AND br2.ActionType = 'Return' " +
        	    "AND br1.ActionDate < br2.ActionDate " +
        	    "WHERE br1.ActionType = 'Borrow' " +
        	    "AND DATE_FORMAT(br1.ActionDate, '%Y-%m') = ? " +
        	    "GROUP BY br1.SubscriberID, br1.BookName, br1.deadline " +
        	    "ORDER BY br1.SubscriberID";


        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, selectedDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FullBorrowRep.add(String.format(
                        "Subscriber ID: %s , Book Name: %s , Borrow Date: %s , Return Date: %s , Deadline: %s , Status: %s",
                        rs.getString("SubscriberID"), 
                        rs.getString("BookName"), 
                        rs.getString("BorrowDate"), 
                        rs.getString("ReturnDate"), 
                        rs.getString("Deadline"), 
                        rs.getString("Status")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error fetching borrow report", e);
        }

        return FullBorrowRep;
    }


	/**
	 * Retrieves a status report of subscribers for a given month and year.
	 *
	 * The report includes subscribers who are late (more than 7 days) and those who are on time.
	 *
	 * @param selectedMonth the selected month (e.g., "01" for January).
	 * @param selectedYear  the selected year (e.g., "2025").
	 * @return an ArrayList of formatted strings representing the status report.
	 * @throws SQLException if an error occurs during the database queries.
	 * @throws IllegalArgumentException if the selected month or year is null.
	 */
	public ArrayList<String> bringStatusRepInfo(String selectedMonth, String selectedYear) throws SQLException {
	    if (selectedMonth == null || selectedYear == null) {
	        throw new IllegalArgumentException("Selected month and year cannot be null.");
	    }

	    ArrayList<String> statusReport = new ArrayList<>();

	    String selectedDate = selectedYear + "-" + selectedMonth;

	    // Query to fetch join dates and relevant freeze dates dynamically
	    String query = "SELECT " +
	                   "DATE_FORMAT(s.join_date, '%Y-%m-%d') AS join_date, " +
	                   "COALESCE((SELECT DATE_FORMAT(MIN(f.start_date), '%Y-%m-%d') " +
	                   "          FROM frozen_subs f " +
	                   "          WHERE f.subscriber_id = s.subscriber_id " +
	                   "          AND DATE_FORMAT(f.start_date, '%Y-%m') <= ? " +
	                   "          AND (f.finish_date IS NULL OR DATE_FORMAT(f.finish_date, '%Y-%m') >= ?) " +
	                   "          ORDER BY f.start_date ASC LIMIT 1), 'None') AS freeze_date " +
	                   "FROM subscriber s " +
	                   "WHERE DATE_FORMAT(s.join_date, '%Y-%m') <= ? " +
	                   "ORDER BY s.join_date";

	    try (PreparedStatement ps = conn.prepareStatement(query)) {
	        ps.setString(1, selectedDate);
	        ps.setString(2, selectedDate);
	        ps.setString(3, selectedDate);

	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                String joinDate = rs.getString("join_date");
	                String freezeDate = rs.getString("freeze_date");

	                // Format and add to report
	                StringBuilder recordBuilder = new StringBuilder();
	                recordBuilder.append("Join Date: ").append(joinDate);
	                
	                if (!"None".equals(freezeDate)) {
	                    recordBuilder.append(", Freeze Date: ").append(freezeDate);
	                }

	                statusReport.add(recordBuilder.toString());
	            }
	        }
	    }
	    
	    return statusReport;
	}

	/**
	 * Retrieves the name of a book based on its barcode ID.
	 *
	 * @param bookId the ID of the book.
	 * @return the name of the book, or an empty string if no matching book is found.
	 * @throws SQLException if an error occurs while querying the database.
	 */
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

	/**
	 * Retrieves the borrow and return deadline for a specific book and subscriber.
	 *
	 * @param borrowerId the ID of the borrower.
	 * @param bookName   the name of the borrowed book.
	 * @return an ArrayList containing the borrow date and return deadline, or null if not found.
	 * @throws SQLException if an error occurs while querying the database.
	 */
	public ArrayList<String> getBorrowDateAndReturnDate(String borrowerId, String bookName) throws SQLException {
        ArrayList<String> borrowAndReturnDate = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement(
                "SELECT ActionDate, deadline " +
                        "FROM activityhistory " +
                        "WHERE SubscriberID = ? " +
                        "  AND BookName = ? " +
                        "  AND ActionType = 'Borrow' " +
                        "  AND hasReturned = 0 " +
                        "ORDER BY ActionDate ASC " +
                        "LIMIT 1"
        );

        ps.setString(1, borrowerId);
        ps.setString(2, bookName);

        ResultSet resultSet = ps.executeQuery();
        if (resultSet.next()) {  // Fetch the first result (LIMIT ensures only one row is returned)
            borrowAndReturnDate.add(resultSet.getString("ActionDate"));
            borrowAndReturnDate.add(resultSet.getString("deadline"));
        } else {
            return null;  // No matching record found
        }

        return borrowAndReturnDate;
    }

	/**
	 * Checks if a specific subscriber has borrowed a specific book.
	 *
	 * @param borrowerId the ID of the borrower.
	 * @param bookName   the name of the borrowed book.
	 * @return {@code true} if the subscriber has borrowed the book, {@code false} otherwise.
	 * @throws SQLException if an error occurs while querying the database.
	 */
	public String checkIfBorrowerFound(String borrowerId, String bookName) throws SQLException {

        PreparedStatement ps = conn.prepareStatement(
                "SELECT bookName FROM activityhistory where SubscriberID=? AND LOWER(bookName)=LOWER(?) AND ActionType='Borrow' AND hasReturned=0");
        ps.setString(1, borrowerId);
        ps.setString(2, bookName);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getString("BookName"); // Retrieve the result from the first column
        }
        return "";
    }

	/**
	 * Checks if a specific book has already been returned by a subscriber.
	 *
	 * @param borrowerId the ID of the borrower.
	 * @param bookName   the name of the book.
	 * @return {@code true} if the book has been returned, {@code false} otherwise.
	 * @throws SQLException if an error occurs while querying the database.
	 */
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

	/**
	 * Inserts a return record into the activity history for a specific subscriber and book.
	 *
	 * @param borrowerId    the ID of the subscriber as a string.
	 * @param bookName      the name of the book being returned.
	 * @param dateDifference the difference in days between the return date and the deadline.
	 * @param isLate        indicates if the return is late.
	 * @return {@code true} if the row was successfully inserted, {@code false} otherwise.
	 * @throws SQLException if an error occurs while inserting the record.
	 */
	public Boolean insertReturnBookRowInActivityHistory(String borrowerId, String bookName, String dateDifference,
			Boolean isLate) throws SQLException {

		int borrowerIdAsInt = Integer.parseInt(borrowerId);
		int rowsAffected = 0;
		ResultSet resultSet;
		LocalDate actionDate = LocalDate.now();
		String insertQuary = "INSERT INTO activityhistory (SubScriberID, BookName, ActionType, ActionDate,"
				+ "additionalInfo) VALUES (?,?,?,?,?)";
		PreparedStatement ps = conn.prepareStatement(insertQuary);
		
		ps.setInt(1, borrowerIdAsInt);
		ps.setString(2, bookName);
		ps.setString(3, "Return");
		ps.setDate(4, Date.valueOf(actionDate));
		ps.setString(5, dateDifference);
		//ps.setInt(6, 1);
		rowsAffected = ps.executeUpdate();
		
		if(rowsAffected==0) {
			return false;
		}
		String selectQuery="SELECT ActionID "
				+ "FROM activityhistory "
				+ "WHERE SubscriberID = ? "
				+ "  AND BookName = ? AND ActionType = 'Borrow' AND hasReturned = 0 ORDER BY ActionID ASC";
		
		PreparedStatement select = conn.prepareStatement(selectQuery);
		
		select.setInt(1, borrowerIdAsInt);
		select.setString(2, bookName);
		
		resultSet = select.executeQuery();
		// resultSet is in row 1
		System.out.println("Result Set row is:"+resultSet.getRow());

		if (!resultSet.next()) {
	        return false; // Empty list
		}
		String updateQuery = "UPDATE activityhistory "
				+ "SET hasReturned = 1 "
				+ "WHERE SubscriberID = ? "
				+ "  AND BookName = ? "
				+ "  AND ActionID = ?";
		
		PreparedStatement update=conn.prepareStatement(updateQuery);
		
		update.setInt(1, borrowerIdAsInt);
		update.setString(2, bookName);
		update.setInt(3, resultSet.getInt("ActionID"));
		
		
		rowsAffected = update.executeUpdate();

		return rowsAffected > 0;
	}

	/**
	 * Updates the subscription status of a subscriber to a specified status.
	 *
	 * @param subscriberId the ID of the subscriber.
	 * @param IsFrozen     the new subscription status (e.g., "Frozen").
	 * @return {@code true} if the update was successful, {@code false} otherwise.
	 * @throws SQLException if an error occurs while updating the status.
	 */
	public Boolean updateSubscriberStatusToFrozen(String subscriberId, String IsFrozen) throws SQLException  {

		String updateQuary = "UPDATE subscriber SET subscription_status=? WHERE subscriber_id = ?";
		
		String insertFrozenTable = "INSERT INTO frozen_subs (subscriber_id, start_date, finish_date)"
				+ " VALUES (?,?,?)";
				
		Integer subIdInt = null;
		LocalDate  localDate = LocalDate.now();
		
		Date currentDate = Date.valueOf(localDate);
		localDate = localDate.plusMonths(1);
		Date unfreezeDate = Date.valueOf(localDate);
		
		try{
		subIdInt = Integer.parseInt(subscriberId);
		}
		
		catch(NumberFormatException e){
			e.printStackTrace();
			
		}
		Boolean bool = checkIsFrozen(subIdInt).equals("frozen");
		if(bool) {
			insertFrozenTable = "UPDATE frozen_subs SET start_date=?, finish_date=? WHERE subscriber_id = ?";
			try(PreparedStatement insertPs= conn.prepareStatement(insertFrozenTable)){				
					insertPs.setDate(1, currentDate);
					insertPs.setDate(2, unfreezeDate);
					insertPs.setInt(3, subIdInt);
					insertPs.executeUpdate();				
				return true;
			}
			catch(SQLException e){
				e.printStackTrace();
				throw e;
			}
		}
		else {
			try(PreparedStatement updatePs = conn.prepareStatement(updateQuary)){
				PreparedStatement insertPs= conn.prepareStatement(insertFrozenTable);
			
				updatePs.setString(1, IsFrozen);
				updatePs.setString(2, subscriberId);

				int updateResult = updatePs.executeUpdate();
				
				if (updateResult == 1) {
				
					insertPs.setInt(1, subIdInt);
					insertPs.setDate(2, currentDate);
					insertPs.setDate(3, unfreezeDate);
					int insertResult = insertPs.executeUpdate();
					
					return insertResult == 1;
				}
				return false;
			}
			catch(SQLException e){
				e.printStackTrace();
				throw e;
			}
		}
		
	}

	/**
	 * Increments the availability count of a specific book in the inventory.
	 *
	 * @param bookName the name of the book to increment availability for.
	 * @return {@code true} if the availability was successfully incremented, {@code false} otherwise.
	 * @throws SQLException if an error occurs while updating the availability.
	 */
	public Boolean incrimentBookAvailability(String bookName) throws SQLException {
		String insertQuary = "UPDATE books SET copysAvailable=copysAvailable + 1 WHERE bookName = ?";
		PreparedStatement ps = conn.prepareStatement(insertQuary);
		ps.setString(1, bookName);
		if (ps.executeUpdate() == 1)
			return true;
		return false;
	}

	/**
	 * Checks the subscription status of a subscriber.
	 *
	 * @param id the ID of the subscriber.
	 * @return the subscription status as a string, or {@code null} if no status is found.
	 */
	public String checkIsFrozen(int id) {
	    String status = null;
	    String query = "SELECT subscription_status FROM subscriber WHERE subscriber_id = ?;";
	    try (PreparedStatement stmt = conn.prepareStatement(query)) { // Use try-with-resources for proper cleanup
	        stmt.setInt(1, id);
	        try (ResultSet resultSet = stmt.executeQuery()) { // Use try-with-resources for ResultSet
	            if (resultSet.next()) { // Check if a row exists
	                status = resultSet.getString("subscription_status");
	            } 
	        }
	    } catch (SQLException e) {
	        e.printStackTrace(); // Log the exception for debugging
	    }
	    return status; // This will return null if no rows were found
	}


	/**
	 * Updates the arrival time for the earliest order of a specific book and notifies the subscriber.
	 *
	 * @param bookName the name of the book.
	 * @return {@code true} if the arrival time was successfully updated, {@code false} otherwise.
	 */
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

	/**
	 * Adds a notification message for a subscriber about the arrival of their ordered book.
	 *
	 * @param subID    the ID of the subscriber.
	 * @param bookName the name of the book that arrived.
	 */
	public void addArrivedMessage(int subID, String bookName) {
		String query = "INSERT INTO sub_messages (subID, note) VALUES (?, ?);";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, subID);
			ps.setString(2,
					"Your order of the book: " + bookName + " has arrived! Please take it in less than two days");
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retrieves the list of books ordered by a specific subscriber that have already arrived.
	 *
	 * @param id the ID of the subscriber.
	 * @return an ArrayList of book names that the subscriber has ordered and have already arrived.
	 */
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

	/**
	 * Deletes canceled orders from the database.
	 *
	 * @param ordersToDelete a map containing subscriber IDs as keys and book names as values,
	 *                       representing the orders to delete.
	 */
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

	/**
	 * Updates the messages table for orders that were not picked up within 2 days of arrival
	 * and deletes these orders from the database. Also updates the inventory for canceled orders.
	 */
	public void timeDidntTakeOrder() {
		Map<Integer, String> ordersToDelete = new HashMap<>();
		String query = "SELECT subID, bookName FROM orders WHERE arrivalTime IS NOT NULL AND arrivalTime < CURDATE() - INTERVAL 2 DAY;";
		String insertMessage = "INSERT INTO sub_messages (subID, note) VALUES (?, ?);";
		try {
			PreparedStatement checkOrderStmt = conn.prepareStatement(query);
			PreparedStatement insertMessageStmt = conn.prepareStatement(insertMessage);
			ResultSet resultSet = checkOrderStmt.executeQuery();
			while (resultSet.next()) {
				int subID = resultSet.getInt("subID");
				String bookName = resultSet.getString("bookName");
				// Insert into messages table
				insertMessageStmt.setInt(1, subID);
				insertMessageStmt.setString(2, "Your order of the book: " + bookName + " is canceled!");
				insertMessageStmt.executeUpdate();
				ordersToDelete.put(subID, bookName); // add the order to the HashMap
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		deleteOrders(ordersToDelete); // call the method to delete the non taken orders from the DB
		addDeletedToInventory(ordersToDelete); //add the books that didnt pick to the copysAvailable in 'books'
	}
	
	/**
	 * Sends reminders to subscribers whose book return deadlines are the next day.
	 * Updates the reminderSent field to prevent duplicate reminders.
	 */
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
	        e.printStackTrace(); 
	    }
	}

	/**
	 * Retrieves all messages for a specific subscriber from the database.
	 *
	 * @param subID the ID of the subscriber.
	 * @return an ArrayList containing all messages associated with the subscriber.
	 */
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
	
	/**
	 * Retrieves all messages for librarians from the database.
	 *
	 * @return an ArrayList containing all librarian messages.
	 */
	public ArrayList<String> librarianMessages() {
		ArrayList<String> messages = new ArrayList<>();
		String query = "SELECT * FROM lib_messages";

		try (PreparedStatement ps = conn.prepareStatement(query)) {
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
	
	/**
	 * Retrieves a list of books borrowed by a subscriber that are near their return deadline.
	 *
	 * @param subscriberId the ID of the subscriber.
	 * @return an ArrayList containing book names and their deadlines.
	 */
	public ArrayList<String> getBooksNearDeadlineForSubscriber(int subscriberId) {
	    ArrayList<String> booksNearDeadline = new ArrayList<>();
	    String query = "SELECT BookName, MAX(deadline) AS deadline " +
	               "FROM activityhistory " +
	               "WHERE ActionType = 'Borrow' " +
	               "AND SubscriberID = ? " +
	               "AND DATE(deadline) <= CURDATE() + INTERVAL 7 DAY " +
	               "GROUP BY BookName";


	    try (PreparedStatement ps = conn.prepareStatement(query)) {
	        ps.setInt(1, subscriberId); // Set the subscriber ID in the query
	        ResultSet rs = ps.executeQuery();
	        while (rs.next()) {
	            String bookName = rs.getString("BookName");
	            String deadline = rs.getString("deadline");
	            booksNearDeadline.add(bookName);
	            booksNearDeadline.add(deadline);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return booksNearDeadline;
	}


	/**
	 * Updates the status of subscribers from 'Frozen' to 'Active' if their last return action
	 * was over a month ago.
	 * 
	 * This method retrieves all subscribers with the 'Frozen' status and checks their last return date
	 * from the activity history. If the last return date is more than a month ago, the subscriber's
	 * status is updated to 'Active'.
	 */

	public void unfreezeAfterMonthStatus()  {
        ArrayList<Integer> frozenSubscribers = new ArrayList<Integer>();

        String getsubscriberID = "SELECT subscriber_id FROM frozen_subs WHERE finish_date<?";

        String updateSubscribersStatus = "UPDATE subscriber SET subscription_status = 'active' WHERE subscriber_id=?";

        String deleteFrozeSubFromTable = "DELETE FROM frozen_subs WHERE subscriber_id = ? ";

        PreparedStatement deleteStatus = null;
        PreparedStatement updateStatus = null;
        PreparedStatement selectSubscribersID = null;
        ResultSet subscribersID = null;

        int rowsUpdated;
        int rowsDeleted;
        try {
            selectSubscribersID = conn.prepareStatement(getsubscriberID);

            selectSubscribersID.setDate(1, Date.valueOf(LocalDate.now()));
            subscribersID = selectSubscribersID.executeQuery();
            while(subscribersID.next()) {
                frozenSubscribers.add(subscribersID.getInt("subscriber_id"));
            }

            updateStatus = conn.prepareStatement(updateSubscribersStatus);
            deleteStatus =  conn.prepareStatement(deleteFrozeSubFromTable);

            for(Integer id : frozenSubscribers) {


                updateStatus.setInt(1, id);
                rowsUpdated = updateStatus.executeUpdate();

                if(rowsUpdated >0) {
                    deleteStatus.setInt(1, id);
                    rowsDeleted = deleteStatus.executeUpdate();                   
                }
            }

        }catch (SQLException e) {
            e.printStackTrace();
        }


    }

	/**
	 * Counts the number of new subscribers who joined in a given month and year.
	 *
	 * @param selectedMonth the selected month (e.g., "01" for January).
	 * @param selectedYear  the selected year (e.g., "2025").
	 * @return the count of new subscribers for the given month and year.
	 * @throws SQLException if an error occurs while querying the database.
	 */
	public int getNewSubscriberCount(String selectedMonth, String selectedYear) throws SQLException {
	    String query = "SELECT COUNT(*) AS total_new_subscribers " +
	                   "FROM subscriber " +
	                   "WHERE DATE_FORMAT(join_date, '%Y-%m') = ?";

	    String selectedDate = selectedYear + "-" + selectedMonth;
	    int count = 0;

	    try (PreparedStatement ps = conn.prepareStatement(query)) {
	        ps.setString(1, selectedDate);
	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                count = rs.getInt("total_new_subscribers");
	            }
	        }
	    }
	    
	    return count;
	}
	
	/**
	 * Updates the inventory by incrementing the availability of books that were not picked up
	 * by subscribers.
	 *
	 * @param ordersToDelete a map where the key is the subscriber ID and the value is the book name.
	 */
	public void addDeletedToInventory(Map<Integer, String> ordersToDelete) {
	    String query = "UPDATE books SET copysAvailable = copysAvailable + 1 WHERE bookName = ?";
	    try (PreparedStatement stmt = conn.prepareStatement(query)) {
	        // Loop through the values of the map (book names)
	        for (String bookName : ordersToDelete.values()) {
	            stmt.setString(1, bookName);
	            stmt.executeUpdate();
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Operations if a book is lost by a subscriber
	 * @param subID the subscriber ID who lost the book
	 * @param bookName the book name that was lost
	 * @return a String to know which message to show on the GUI
	 */
	public String lostBook(String subID, String bookName) {
		String bookExists = isAvailable(bookName);
		int intSubID = Integer.parseInt(subID);
		boolean bookExist = bookExists.equals("notExist"); //check if the book exists
		boolean ID = !isSubscriberExist(intSubID); // check if the subscriber exists
		ResultSet resultSet = null;
		Boolean frozen = true;
		if(bookExist && ID) { //if the sub and book dont exist
			return "bookAndIDNotExists";
		}
		if(bookExists.equals("notExist")) { //checks if book exists
			return "bookNotExists";
		}
		if(ID) { // checks if subID doesnt exist
			return "subID";
		}
		//check if this borrow even exists
		String checkBorrow = "SELECT * FROM activityhistory WHERE SubscriberID = ? AND BookName = ? AND ActionType = ? AND hasReturned = ?";
		try (PreparedStatement stmt = conn.prepareStatement(checkBorrow)) {
	        // Loop through the values of the map (book names)
	            stmt.setInt(1, intSubID);
	            stmt.setString(2,bookName);
	            stmt.setString(3,"Borrow");
	            stmt.setBoolean(4, false);
	            resultSet = stmt.executeQuery();
	            if(!resultSet.next()) {
	        		return "BorrowNotExist";
	        	}
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		//update the inventory of the book to -1
		String query = "UPDATE books SET totalCopys = totalCopys - 1 WHERE bookName = ?";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
	        // Loop through the values of the map (book names)
	            stmt.setString(1, bookName);
	            stmt.executeUpdate();
	            frozen = updateSubscriberStatusToFrozen(subID,"frozen");
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		//update the hasReturned to be 1 if the book is lost, so that cannot click lost multiple times
		String hasRet = "UPDATE activityhistory SET hasReturned = ? WHERE SubscriberID = ? AND BookName = ? AND ActionType = ? AND hasReturned = ?";
		try (PreparedStatement stmt = conn.prepareStatement(hasRet)) {
	        // Loop through the values of the map (book names)
				stmt.setBoolean(1, true);
	            stmt.setInt(2, intSubID);
	            stmt.setString(3,bookName);
	            stmt.setString(4,"Borrow");
	            stmt.setBoolean(5, false);
	            stmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		//add action type lost to the activity history
		String addQuery = "INSERT INTO activityhistory (SubscriberID, BookName, ActionType, ActionDate) VALUES (?, ?, ?, ?);";
	    try {
	        PreparedStatement stmt = conn.prepareStatement(addQuery);
	        stmt.setInt(1, intSubID);
	        stmt.setString(2, bookName);
	        stmt.setString(3, "Lost");
	        stmt.setString(4, LocalDateTime.now().toString());
	        stmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    //if account was already frozen
	    if(!frozen) {
	    	String refreeze = "UPDATE frozen_subs SET start_date = ?, finish_date = ? WHERE subscriber_id = ?";
	    	try (PreparedStatement stmt = conn.prepareStatement(refreeze)) {
	            // Loop through the values of the map (book names)
	                stmt.setInt(3, intSubID);
	                stmt.setDate(1,Date.valueOf(LocalDate.now()));
	                stmt.setDate(2,Date.valueOf(LocalDate.now().plusMonths(1)));
	                stmt.executeUpdate();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    	return "SubAlreadyFrozen";
	    }
		return "";
	}

}

