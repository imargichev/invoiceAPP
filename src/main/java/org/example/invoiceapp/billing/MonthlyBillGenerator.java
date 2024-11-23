package org.example.invoiceapp.billing;

import org.example.invoiceapp.data.CustomerUsage;
import org.example.invoiceapp.util.ConfigLoader;

import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;

/**
 * The MonthlyBillGenerator class is responsible for:
 * - Loading customer data from a lookup file.
 * - Generating monthly electricity bills for customers.
 * - Saving bill details into a MySQL database.
 */
public class MonthlyBillGenerator {
    // Paths and configurations loaded from the application configuration file
    private static final String LOOKUP_FILE = ConfigLoader.getProperty("customer.lookup.path");
    private static final String OUTPUT_DIR = ConfigLoader.getProperty("mainDir.output");
    private static final String DB_URL = ConfigLoader.getProperty("db.url");
    private static final String DB_USER = ConfigLoader.getProperty("db.username");
    private static final String DB_PASSWORD = ConfigLoader.getProperty("db.password");
    private static final Logger LOGGER = Logger.getLogger(MonthlyBillGenerator.class.getName());
    private static final String DAY_PRICE = ConfigLoader.getProperty("day.price");
    private static final String NIGHT_PRICE = ConfigLoader.getProperty("night.price");




    /**
     * Loads customer names from the lookup file.
     *
     * @return A map of customer IDs to customer names.
     *         Example: { "C001" -> "John Doe", "C002" -> "Jane Smith" }
     */
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

    /**
     * Generates a bill file for a specific customer and saves the details to the database.
     *
     * @param customerId    The customer's unique ID.
     * @param customerName  The customer's name.
     * @param usage         An object containing the customer's electricity usage.
     */
    public static void generateBill(String customerId, String customerName, CustomerUsage usage) {
        String outputFileName = OUTPUT_DIR + customerId + ".txt";

        // Generate a bill text file for the customer
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

        // Save the bill details to the database
        saveBillToDatabase(customerId, customerName, usage);
    }

    /**
     * Saves the bill details into the "bills" table in the MySQL database.
     *
     * @param customerId    The customer's unique ID.
     * @param customerName  The customer's name.
     * @param usage         An object containing the customer's electricity usage.
     */
    private static void saveBillToDatabase(String customerId, String customerName, CustomerUsage usage) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO bills (customer_id, customer_name, daytime_usage, nighttime_usage, daytime_cost, nighttime_cost) VALUES (?, ?, ?, ?, ?, ?)")) {
            // Bind parameters to the prepared statement
            pstmt.setString(1, customerId);
            pstmt.setString(2, customerName);
            pstmt.setInt(3, usage.getDaytimeUsage());
            pstmt.setInt(4, usage.getNighttimeUsage());
            pstmt.setDouble(5, usage.getDaytimeUsage() * Double.parseDouble(DAY_PRICE));
            pstmt.setDouble(6, usage.getNighttimeUsage() * Double.parseDouble(NIGHT_PRICE));
            pstmt.executeUpdate();
            LOGGER.info("Bill saved to database for customer ID: " + customerId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to save bill to database", e);
        }
    }
}
