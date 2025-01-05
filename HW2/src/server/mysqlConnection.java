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
            conn = DriverManager.getConnection("jdbc:mysql://localhost/hw2-shitot?serverTimezone=IST", "root", "yaniv1234");
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
                    activityHistory.add("Book Name: " + bookName + ", Action: " + actionType + ", Date: " + actionDate);
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
                    borrowHistory.add("Book Name: " + bookName + ", Date: " + actionDate);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return borrowHistory;
    }

    public static boolean ChangeReturnDate(String subscriberId, String BookName, String OldDate, String NewDate, String Librarian_name) {
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
                + "MIN(br2.ActionDate) AS ReturnDate "
                + "FROM activityhistory br1 JOIN activityhistory br2 "
                + "ON br1.SubscriberID = br2.SubscriberID AND br1.BookName = br2.BookName "
                + "AND br1.ActionType = 'Borrow' AND br2.ActionType = 'Return' AND br1.ActionDate < br2.ActionDate "
                + "GROUP BY br1.SubscriberID, br1.BookName";

        try (PreparedStatement ps = conn.prepareStatement(query1)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FullBorrowRep.add(String.format(
                            "Subscriber ID: %s Book Name: %s Borrow Date: %s Return Date: %s",
                            rs.getString("SubscriberID"), rs.getString("BookName"), rs.getString("BorrowDate"),
                            rs.getString("ReturnDate")));
                }
            }
        }

        // Query for Borrowed but Not Returned Books
        String query2 = "SELECT br1.SubscriberID, br1.BookName, MIN(br1.ActionDate) AS BorrowDate "
                + "FROM activityhistory br1 LEFT JOIN activityhistory br2 "
                + "ON br1.SubscriberID = br2.SubscriberID AND br1.BookName = br2.BookName "
                + "AND br2.ActionType = 'Return' "
                + "WHERE br1.ActionType = 'Borrow' AND br2.ActionType IS NULL "
                + "GROUP BY br1.SubscriberID, br1.BookName";

        try (PreparedStatement ps = conn.prepareStatement(query2)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FullBorrowRep.add(String.format(
                            "Subscriber ID: %s Book Name: %s Borrow Date: %s Return Date: __-__-____ __:__:__",
                            rs.getString("SubscriberID"), rs.getString("BookName"), rs.getString("BorrowDate")));
                }
            }
        }

        return FullBorrowRep;
    }
}