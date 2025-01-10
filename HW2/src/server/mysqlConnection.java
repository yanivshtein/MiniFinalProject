package server;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
            conn = DriverManager.getConnection("jdbc:mysql://localhost/hw2-shitot?serverTimezone=IST", "root", "!vex123S");
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
                String phone = rs.getString("subscriber_phone_number");
                String email = rs.getString("subscriber_email");
                String status = rs.getString("Subscription_status");
                String password = rs.getString("password");
                sub = new Subscriber1(sub_id, sub_name, phone, email, status,password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sub;
    }

    public static Boolean searchSubId(String email, String password) {
        String searchQuery = "SELECT subscriber_id FROM subscriber WHERE subscriber_email = ? AND password = ?";
        try (PreparedStatement ps = conn.prepareStatement(searchQuery)) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static Boolean searchLibId(String email, String password) {
        String searchQuery = "SELECT 1 FROM librarian WHERE librarian_email = ? AND librarian_password = ?";
        try (PreparedStatement ps = conn.prepareStatement(searchQuery)) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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

    public static String canAddOrder(String id, String bookName) {
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

    public static void addOrder(String bookName, String id) {
        String addQuery = "INSERT INTO orders (time, bookName, subID) VALUES (?, ?, ?);";
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);
        try {
            PreparedStatement stmt = conn.prepareStatement(addQuery);
            stmt.setTimestamp(1, timestamp);
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
            stmt.setString(1, id);
            stmt.setString(2, bookName);
            stmt.setString(3, "Reservation");
            stmt.setString(4, LocalDateTime.now().toString());
            stmt.setString(5, "");
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

    public static ArrayList<String> getBorrowHistory(String subscriberId) {
        ArrayList<String> borrowHistory = new ArrayList<>();
        String query = "SELECT * FROM ActivityHistory WHERE SubscriberID = ? AND ActionType = 'Borrow'";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, subscriberId);
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

    public static boolean ChangeReturnDate(String subscriberId, String BookName, String OldDate, String NewDate, String Librarian_name) { 
        String checkQuery = "SELECT additionalInfo FROM activityhistory WHERE SubscriberID = ? AND BookName = ? AND deadline = ? AND ActionType = 'Borrow'";
        String updateQuery = "UPDATE activityhistory SET deadline = ?, additionalInfo = ? WHERE SubscriberID = ? AND BookName = ? AND deadline = ? AND ActionType = 'Borrow'";

        try (PreparedStatement checkPs = conn.prepareStatement(checkQuery)) {
            // Set parameters for the check query
            checkPs.setString(1, subscriberId);
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
                updatePs.setString(3, subscriberId);
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
                            "Subscriber ID: %s Book Name: %s Borrow Date: %s Return Date: %s Deadline: %s Status: %s",
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
                            "Subscriber ID: %s Book Name: %s Borrow Date: %s Return Date: __-__-____ __:__:__ Deadline: %s",
                            rs.getString("SubscriberID"), rs.getString("BookName"), rs.getString("BorrowDate"),
                            rs.getString("Deadline")));
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
                    System.out.println(LateSubs);
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

}