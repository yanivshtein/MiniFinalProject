package server;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
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


            conn = DriverManager.getConnection("jdbc:mysql://localhost/hw2-shitot?serverTimezone=IST", "root", "Sheli123");

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
        String selectQuery = "SELECT * FROM subscriber WHERE subscriber_id = ?"; // Use a placeholder (?) for the parameter
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


    public static Subscriber1 searchSubId(String email, String password) {
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
        return sub; //no subscriber found
    }

    
    public static Librarian searchLibId(String email, String password) {
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
    public static Integer getBookAvailability(String bookName) {
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
    public static String getNearestReturnDate(String bookName) {
        // Query to select the nearest return date for a borrowed book
        String query = "SELECT deadline FROM activityhistory WHERE BookName = ? AND ActionType = 'Borrow' ORDER BY deadline ASC LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, bookName);  // Set the book name in the query
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
    public static String getSubscriptionStatus(int subscriberId) {
        // Query to check the subscription status of the subscriber by subscriber ID
        String query = "SELECT subscription_status FROM subscriber WHERE subscriber_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, subscriberId);  // Set the subscriber ID in the query
            
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



    public static String isAvailable(String bookName) {
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

    public static String canAddOrder(int id, String bookName) {
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

    public static void addOrder(String bookName, int id) {
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

    public static void addOrderToActivityHistory(String bookName, int id) {
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
    
    public static String canExtend(int id, String bookName) {
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
    	boolean orderExists = orderExists(bookName); //check if there is an order for this book
    	if (orderExists ==true)
    		return "order exists";
    	addExtension(id, bookName);
    	//need to send a message to the librarian
    	return "can extend";
    }
    
    private static boolean orderExists (String bookName) {
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
    
    //this method updates the Borrow's deadline in more 7 days
    private static void addExtension(int id, String bookName) {
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
    }
    
    //add addSubcriber
    public static void addSubscriber(int subId, String subName, String phone, String email,String status , String password) {
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

    public static ArrayList getActivityHistory(String subscriberEmail) {
        System.out.println(subscriberEmail);
        ArrayList activityHistory = new ArrayList<>();
        
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
                                activityHistory.add("Book Name: " + bookName + 
                                                 ", Action: " + actionType + 
                                                 ", Date: " + actionDate);
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

    public static ArrayList<String> getBorrowHistory(int subscriberId) {
        ArrayList<String> borrowHistory = new ArrayList<>();
        String query = "SELECT * FROM ActivityHistory WHERE SubscriberID = ? AND ActionType = 'Borrow'";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, subscriberId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                	String deadline = rs.getString("deadline");
                	//System.out.println(deadline);
                    String bookName = rs.getString("BookName");
                    String actionDate = rs.getString("ActionDate");
                    borrowHistory.add("Book Name: " + bookName + ",Borrow Date: " + actionDate + ", Deadline: "+ deadline);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return borrowHistory;
    }

    public static boolean ChangeReturnDate(int subscriberId, String BookName, String OldDate, String NewDate, String Librarian_name) { 
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
                updatePs.setString(2, "Extended by: " + Librarian_name + " , at: ");
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

    public static boolean isSubscriberExist(int id) {
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
    public static boolean decrementBookAvailability(String bookName) {
       
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
    public static void addActivityToHistory(int subscriberId, String bookName) {
        // SQL query to insert a record into the activityhistory table
        String insertQuery = "INSERT INTO activityhistory (SubscriberID, BookName, ActionType, ActionDate, Deadline) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
            // Set values for the columns in the table
            ps.setInt(1, subscriberId);  // SubscriberID (Subscriber's ID)
            ps.setString(2, bookName);    // BookName (Name of the book)
            ps.setString(3, "borrow");    // ActionType (The action is "borrow")
            
            // ActionDate (Date of the action) - the current date
            LocalDate currentDate = LocalDate.now();
            ps.setDate(4, Date.valueOf(currentDate));  // Convert the current date to `DATE` type
            
            // Deadline (The due date - two weeks later)
            LocalDate deadlineDate = currentDate.plusWeeks(2);  // Add two weeks
            ps.setDate(5, Date.valueOf(deadlineDate));  // Convert the deadline date to `DATE` type

            // Execute the insertion into the database
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();  // Print any SQL exceptions that occur
        }
    }
    public static ArrayList<String> getAllBookNames() {
        ArrayList<String> bookNames = new ArrayList<>();
        String query = "SELECT bookName FROM books";
        
        try (PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String name = rs.getString("bookName");
                bookNames.add(name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return bookNames;
    }

    
    public static ArrayList<String> getBorrowedBooks(int id) {
    	System.out.println(id);
    	ArrayList<String> borrowedBooks = new ArrayList<>();
        String query = "SELECT BookName FROM activityhistory WHERE SubscriberID = ? AND ActionType = 'Borrow'";
        
        try (PreparedStatement ps = conn.prepareStatement(query)){
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
    public static ArrayList<String> fetchBooksByCriteria(String criteria, String value) {
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


    public static ArrayList<String> BringBorrowRepInfo(String SelectedMonth , String SelectedYear) throws SQLException {
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
                + "AND br2.ActionType = 'Return' "
                + "WHERE br1.ActionType = 'Borrow' AND br2.ActionType IS NULL "
                + "AND DATE_FORMAT(br1.ActionDate, '%Y-%m') = ? "
                + "GROUP BY br1.SubscriberID, br1.BookName, br1.deadline";

        try (PreparedStatement ps = conn.prepareStatement(query2)) {
            // Set the SelectedMonth parameter for the query
            ps.setString(1,SelectedYear + "-" + SelectedMonth);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FullBorrowRep.add(String.format(
                            "Subscriber ID: %s , Book Name: %s , Borrow Date: %s , Return Date: __-__-____ __:__:__  , Deadline: %s",
                            rs.getString("SubscriberID"), rs.getString("BookName"), rs.getString("BorrowDate"),
                            rs.getString("deadline")));
                }
            }
        }

        return FullBorrowRep;
    }
    
    public static ArrayList<String> BringStatusRepInfo(String selectedMonth, String selectedYear) throws SQLException {
        // Input validation
        if (selectedMonth == null || selectedYear == null) {
            throw new IllegalArgumentException("Selected month and year cannot be null.");
        }

        // Initialize list to store subscribers who are late with submissions
        ArrayList<Integer> LateSubs = new ArrayList<>();

        // First query: Find subscribers who are more than 7 days late in the selected month
        String query1 = "SELECT SubscriberID " +
                "FROM activityhistory " +
                "WHERE additionalInfo LIKE '% days late' " +
                "AND CAST(SUBSTRING_INDEX(additionalInfo, ' ', 1) AS UNSIGNED) > 7 " +
                "AND DATE_FORMAT(ActionDate, '%Y-%m') = ?";
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
        
        // Second query: Find subscribers who are NOT late (complement of late subscribers)
        String query2 = "SELECT subscriber_id " +
                "FROM subscriber " +
                "WHERE subscriber_id NOT IN (" +
                " SELECT SubscriberID " +
                " FROM activityhistory " +
                " WHERE additionalInfo LIKE '% days late' " +
                " AND CAST(SUBSTRING_INDEX(additionalInfo, ' ', 1) AS UNSIGNED) > 7 " +
                " AND DATE_FORMAT(ActionDate"
                + ", '%Y-%m') = ?" +
                ")";
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
                        statusReport.add(subscriberName + " (ID: " + subscriberId + ") - Frozen");
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
                        statusReport.add(subscriberName + " (ID: " + subscriberId + ") - Active");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return statusReport;
    }
    
    
    public static String BringBarCodeBookName(int bookId) throws SQLException {
    	String bookName = "";
         String query = "SELECT bookName FROM books WHERE BookID = ?";
         try (PreparedStatement ps = conn.prepareStatement(query)){
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

    public static ArrayList<Object>  getBorrowDateAndReturnDate(String borrowerId,String bookName) throws SQLException {
        ArrayList<Object> borrowAndReturnDate = new ArrayList<>();
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

    public static Boolean checkBookAlreadyReturned(String borrowerId,String bookName) throws SQLException {

        String query=" SELECT "
                + "COUNT(CASE WHEN ActionType ='Borrow' THEN 1 END) AS borrow_count,"
                + "COUNT(CASE WHEN ActionType = 'Return' THEN 1 END) AS return_count "
                + "FROM activityhistory WHERE SubscriberID=? AND BookName=? " ;

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

                // Return true if the book has been returned (when borrowed and returned count are the same)
                return countBorrowed == countReturned;
            }
        }
    }

    public static Boolean insertReturnBookRowInActivityHistory(String borrowerId,String bookName,String dateDifference,Boolean isLate) throws SQLException {

        int borrowerIdAsInt = Integer.parseInt(borrowerId);
        int rowsAffected=0;
        LocalDate actionDate = LocalDate.now();
        String insertQuary = "INSERT INTO activityhistory (SubScriberID, BookName, ActionType, ActionDate,"
                + "additionalInfo) VALUES (?,?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(insertQuary);

        ps.setInt(1,borrowerIdAsInt);
        ps.setString(2,bookName);
        ps.setString(3,"Return");
        ps.setDate(4,Date.valueOf(actionDate));
        ps.setString(5, dateDifference);

        rowsAffected = ps.executeUpdate();

        return rowsAffected>0;
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
	public static Boolean incrimentBookAvailability(String bookName) throws SQLException {
        PreparedStatement checkIfFull= conn.prepareStatement("SELECT * From books WHERE bookName=? AND copysAvailable<totalCopys");
        //int numOfCopiesAvailable=getBookAvailality(bookName);        //need to add to code
        ResultSet rs;
        checkIfFull.setString(1, bookName);
        rs= checkIfFull.executeQuery();


        if(rs==null)    {    // if there is a row that book copies available is equal or greater then total copies of the book
            System.err.println("there shouldn't be a book borrow in the first place");
            return false;    // then it is an error.

        }


        String insertQuary = "UPDATE books SET copysAvailable=copysAvailable + 1 WHERE bookName = ?";
        PreparedStatement ps = conn.prepareStatement(insertQuary);
        ps.setString(1, bookName);

        if(ps.executeUpdate()==1)
            return true;

        return false;
    }

}