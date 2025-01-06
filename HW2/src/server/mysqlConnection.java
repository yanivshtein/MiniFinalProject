package server;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
            conn = DriverManager.getConnection("jdbc:mysql://localhost/hw2-shitot?serverTimezone=IST", "root", "Aa123456");
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

    public static Boolean searchSubId(String id, String password) {
        String searchQuery = "SELECT 1 FROM subscriber WHERE subscriber_email = ? AND password = ?";
        try (PreparedStatement ps = conn.prepareStatement(searchQuery)) {
            ps.setString(1, id);
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
        String dateTime = LocalDateTime.now().toString();
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
                    String bookName = rs.getString("BookName");
                    String actionDate = rs.getString("ActionDate");
                    String deadline = rs.getString("deadline");
                    borrowHistory.add("Book Name: " + bookName + ", Date: " + actionDate + ", Deadline: " + deadline);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return borrowHistory;
    }

    public static boolean ChangeReturnDate(String subscriberId, String BookName, String OldDate, String NewDate, String Librarian_name) { 
        String query = "UPDATE activityhistory SET deadline = ? WHERE SubscriberID = ? AND BookName = ? AND deadline = ? AND ActionType = 'Borrow'"; 
        try (PreparedStatement ps = conn.prepareStatement(query)) { 
        	ps.setString(1, NewDate);
            ps.setString(2, subscriberId);
            ps.setString(3, BookName);
            ps.setString(4, OldDate);
            
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) { 
            e.printStackTrace(); 
            return false; 
        } 
    }

    public static ArrayList<String> BringBorrowRepInfo(String SelectedMonth , String SelectedYear) throws SQLException {
        ArrayList<String> FullBorrowRep = new ArrayList<>();

     // Query for Borrowed and Returned Books
        String query1 = "SELECT br1.SubscriberID, br1.BookName, MIN(br1.ActionDate) AS BorrowDate, "
                + "MIN(br2.ActionDate) AS ReturnDate, br1.Deadline , br2.returned_late "
                + "FROM activityhistory br1 JOIN activityhistory br2 "
                + "ON br1.SubscriberID = br2.SubscriberID AND br1.BookName = br2.BookName "
                + "AND br1.ActionType = 'Borrow' AND br2.ActionType = 'Return' AND br1.ActionDate < br2.ActionDate "
                + "WHERE DATE_FORMAT(br1.ActionDate, '%Y-%m') = ? "
                + "GROUP BY br1.SubscriberID, br1.BookName, br1.Deadline , br2.returned_late";

        try (PreparedStatement ps = conn.prepareStatement(query1)) {
            // Set the SelectedMonth parameter for the query
            ps.setString(1, SelectedYear + "-" + SelectedMonth);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Check if the book was returned late or on time
                    String lateStatus = rs.getInt("returned_late") == 1 ? "Late" : "On Time";
                    
                    // Add the information to the list, including the late status
                    FullBorrowRep.add(String.format(
                            "Subscriber ID: %s Book Name: %s Borrow Date: %s Return Date: %s Deadline: %s Status: %s",
                            rs.getString("SubscriberID"), rs.getString("BookName"), rs.getString("BorrowDate"),
                            rs.getString("ReturnDate"), rs.getString("Deadline"), lateStatus));
                }
            }
        }


        // Query for Borrowed but Not Returned Books
        String query2 = "SELECT br1.SubscriberID, br1.BookName, MIN(br1.ActionDate) AS BorrowDate, br1.Deadline "
                + "FROM activityhistory br1 LEFT JOIN activityhistory br2 "
                + "ON br1.SubscriberID = br2.SubscriberID AND br1.BookName = br2.BookName "
                + "AND br2.ActionType = 'Return' "
                + "WHERE br1.ActionType = 'Borrow' AND br2.ActionType IS NULL "
                + "AND DATE_FORMAT(br1.ActionDate, '%Y-%m') = ? "
                + "GROUP BY br1.SubscriberID, br1.BookName, br1.Deadline";

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


}