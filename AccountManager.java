package NotesApp;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountManager {
    public static Account createAccount(String username, String password) throws Exception {
        String passwordHash = hashPassword(password);

        try (Connection conn = DatabaseManager.getConnection()) {
            String insertUserQuery = "INSERT INTO Accounts (username, passwordHash) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertUserQuery)) {
                stmt.setString(1, username);
                stmt.setString(2, passwordHash);
                stmt.executeUpdate();
            }
            return new Account(username, passwordHash);
        } catch (SQLException e) {
            throw new Exception("Username already exists or database error.");
        }
    }

    public static Account login(String username, String password) throws Exception {
        String passwordHash = hashPassword(password);
        try (Connection conn = DatabaseManager.getConnection()) {
            String query = "SELECT passwordHash FROM Accounts WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                if (rs.next() && rs.getString("passwordHash").equals(passwordHash)) {
                    return new Account(username, passwordHash);
                }
            }
        } catch (SQLException e) {
            throw new Exception("Database error during login.");
        }
        throw new Exception("Invalid username or password.");
    }

    private static String hashPassword(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
}
