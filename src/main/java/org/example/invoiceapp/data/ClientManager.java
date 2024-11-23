package org.example.invoiceapp.data;

import org.example.invoiceapp.util.ConfigLoader;

import java.sql.*;
import java.util.logging.*;

public class ClientManager {
    private static final String DB_URL = ConfigLoader.getProperty("db.url");
    private static final String DB_USER = ConfigLoader.getProperty("db.username");
    private static final String DB_PASSWORD = ConfigLoader.getProperty("db.password");
    private static final Logger LOGGER = Logger.getLogger(ClientManager.class.getName());

    public static void addClient(String clientId, String clientName) {
        String sql = "INSERT INTO clients (client_id, client_name) VALUES (?, ?) ON DUPLICATE KEY UPDATE client_name = VALUES(client_name)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, clientId);
            pstmt.setString(2, clientName);
            pstmt.executeUpdate();
            LOGGER.info("Client added or updated: " + clientId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to add or update client", e);
        }
    }
    //this method checks if a client exists in the database, but it is not used in the application code
    public static boolean isClientExists(String clientId) {
        String sql = "SELECT COUNT(*) FROM clients WHERE client_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, clientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to check if client exists", e);
        }
        return false;
    }
}
