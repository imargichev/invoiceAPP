package org.example.invoiceapp.billing;

import org.example.invoiceapp.data.CustomerUsage;

import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;

/*The MonthlyBillGenerator class is responsible for generating monthly electricity bills for customers.
  It reads customer data, generates bill files, and saves the bill details to a database.*/

public class MonthlyBillGenerator {
    private static final String LOOKUP_FILE = "src/main/resources/lookup.txt";
    private static final String OUTPUT_DIR = "src/main/resources/output/";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/invoiceapp";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "SUP3R_p@ss";
    private static final Logger LOGGER = Logger.getLogger(MonthlyBillGenerator.class.getName());

    /*Purpose: Loads the MySQL JDBC driver and creates the bills table if it does not exist.
    Example Usage: Automatically executed when the class is loaded.*/
    static {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement()) {
                String sql = "CREATE TABLE IF NOT EXISTS bills (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "customer_id VARCHAR(255) NOT NULL," +
                        "customer_name VARCHAR(255) NOT NULL," +
                        "daytime_usage INT NOT NULL," +
                        "nighttime_usage INT NOT NULL," +
                        "daytime_cost DOUBLE NOT NULL," +
                        "nighttime_cost DOUBLE NOT NULL)";
                stmt.execute(sql);
                LOGGER.info("Database table created or already exists.");
            }
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "MySQL JDBC Driver not found", e);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database table creation failed", e);
        }
    }

    //Reads customer lookup data from a file and returns it as a map of customer IDs to customer names.
    public static Map<String, String> loadCustomerNames() {
        Map<String, String> customerNames = new HashMap<>();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(LOOKUP_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                customerNames.put(fields[0], fields[1]);
            }
            LOGGER.info("Customer names loaded successfully.");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load customer names", e);
        }

        return customerNames;
    }

    //Generates a bill file for a customer and saves the bill details to a database.
    public static void generateBill(String customerId, String customerName, CustomerUsage usage) {
        String outputFileName = OUTPUT_DIR + customerId + ".txt";
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFileName))) {
            writer.write("Customer ID: " + customerId);
            writer.newLine();
            writer.write("Customer Name: " + customerName);
            writer.newLine();
            writer.write("Daytime Usage: " + usage.getDaytimeUsage() + " kWh");
            writer.newLine();
            writer.write("Nighttime Usage: " + usage.getNighttimeUsage() + " kWh");
            writer.newLine();
            writer.write("Total Daytime Cost: " + (usage.getDaytimeUsage() * 0.15) + " BGN");
            writer.newLine();
            writer.write("Total Nighttime Cost: " + (usage.getNighttimeUsage() * 0.05) + " BGN");
            writer.newLine();
            LOGGER.info("Bill generated: " + outputFileName);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to generate bill", e);
        }

        saveBillToDatabase(customerId, customerName, usage);
    }

    //Saves the bill details to a MySQL database.
    private static void saveBillToDatabase(String customerId, String customerName, CustomerUsage usage) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO bills (customer_id, customer_name, daytime_usage, nighttime_usage, daytime_cost, nighttime_cost) VALUES (?, ?, ?, ?, ?, ?)")) {
            pstmt.setString(1, customerId);
            pstmt.setString(2, customerName);
            pstmt.setInt(3, usage.getDaytimeUsage());
            pstmt.setInt(4, usage.getNighttimeUsage());
            pstmt.setDouble(5, usage.getDaytimeUsage() * 0.15);
            pstmt.setDouble(6, usage.getNighttimeUsage() * 0.05);
            pstmt.executeUpdate();
            LOGGER.info("Bill saved to database for customer ID: " + customerId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to save bill to database", e);
        }
    }
}
