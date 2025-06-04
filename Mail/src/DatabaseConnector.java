import java.sql.*;

public class DatabaseConnector {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mail_app";
    private static final String DB_USER = "mail_app_user";
    private static final String DB_PASSWORD = "bezpieczne_haslo";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static boolean registerUser(String email, String password) {
        String query = "INSERT INTO users (email, password) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, password);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean DeleteUser(String email, String password) {
        String query = "DELETE FROM users WHERE email = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, password);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean resetPassword(String email, String newPassword) {
        String query = "UPDATE users SET password = ? WHERE email = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newPassword);
            stmt.setString(2, email);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean resetUser(String email, String noweNazwisko, Date nowaData) {
        String query = "UPDATE users SET nazwisko = ?, birth_date = ? WHERE email = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, noweNazwisko);
            stmt.setDate(2, nowaData);
            stmt.setString(3, email);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean registerUser(String email, String password, String nazwisko, String imie, Date dataUrodzenia ) {
        String query = "INSERT INTO users (email, password, nazwisko, imie, birth_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, password);
            stmt.setString(3, nazwisko);
            stmt.setString(4, imie);
            stmt.setString(5, String.valueOf(dataUrodzenia));
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean emailExists(String email) {
        String query = "SELECT COUNT(*) FROM users WHERE email = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean validateUser(String email, String password) {
        String query = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, password);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void moveToBin(String sender, String recipient, String title, String message) {
        String query = "INSERT INTO bin (sender_email, recipient_email, title, message) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, sender);
            stmt.setString(2, recipient);
            stmt.setString(3, title);
            stmt.setString(4, message);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void deleteFromReceived(String sender, String recipient, String title, String message) {
        String query = "DELETE FROM received_emails WHERE sender_email = ? AND recipient_email = ? AND title = ? AND message = ? LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, sender);
            stmt.setString(2, recipient);
            stmt.setString(3, title);
            stmt.setString(4, message);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean saveSentEmail(String senderEmail, String recipientEmail, String message, String title) {
        String query = "INSERT INTO sent_emails (sender_email,recipient_email, message, sent_at, title) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, senderEmail);
            stmt.setString(2, recipientEmail);
            stmt.setString(3, message);
            stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis())); // lub możesz pominąć jeśli masz DEFAULT CURRENT_TIMESTAMP
            stmt.setString(5, title);

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void saveReceivedEmailIfNotExists(String sender, String recipient, String title, String message) {
        String checkQuery = "SELECT COUNT(*) FROM received_emails WHERE sender_email = ? AND recipient_email = ? AND title = ? AND message = ?";
        String insertQuery = "INSERT INTO received_emails (sender_email, recipient_email, title, message) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {

            checkStmt.setString(1, sender);
            checkStmt.setString(2, recipient);
            checkStmt.setString(3, title);
            checkStmt.setString(4, message);

            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                insertStmt.setString(1, sender);
                insertStmt.setString(2, recipient);
                insertStmt.setString(3, title);
                insertStmt.setString(4, message);
                insertStmt.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
