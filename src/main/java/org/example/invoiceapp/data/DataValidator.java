package org.example.invoiceapp.data;

import org.example.invoiceapp.util.ConfigLoader;

import java.io.*;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.*;

/**
 * The `RecordValidator` class is responsible for validating consumption records from a file.
 * It ensures that the records meet specified validation criteria, separating valid and invalid records.
 * Invalid records are written to an error file for further review.
 *
 * <p>
 * Key responsibilities:
 * - Validates each record based on specific criteria.
 * - Separates valid records from invalid ones.
 * - Writes invalid records to a designated error file.
 * - Logs validation success and failure events.
 * </p>
 */
public class DataValidator {
    // Static configuration properties loaded via ConfigLoader
    private static final String DB_URL = ConfigLoader.getProperty("db.url");
    private static final String DB_USER = ConfigLoader.getProperty("db.username");
    private static final String DB_PASSWORD = ConfigLoader.getProperty("db.password");
    private static final String ERROR_FILE = ConfigLoader.getProperty("error.file.path");
    private static final Logger LOGGER = Logger.getLogger(DataValidator.class.getName());

    /**
     * Validates a list of consumption records and separates valid records from invalid ones.
     *
     * <p>
     * The method processes a list of records, checking each record against the following criteria:
     * - The record must contain at least 7 fields.
     * - The "Quality" field (index 6) must be either "A" (actual) or "E" (estimated).
     * - The usage values (Usage1, Usage2, Usage3, Usage4) must all be greater than 0.
     * </p>
     *
     * <p>
     * Invalid records that do not meet the criteria are written to the error file (`E_records.txt`),
     * and their validation failure is logged. Only records that pass all validation checks are
     * returned in the list of valid records.
     * </p>
     *
     * @param records a list of strings representing consumption records in the format:
     *                "Customer ID,Date,Usage1,Usage2,Usage3,Usage4,Quality,..."
     * @return a list of valid records that meet the validation criteria
     */
    public static List<String> validateRecords(List<String> records) {
        List<String> validRecords = new ArrayList<>();

        // Try-with-resources block for writing invalid records to the error file
        try (BufferedWriter writerForE_Errors = Files.newBufferedWriter(Paths.get(ERROR_FILE));
             Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

            // Prepare the SQL statement once
            String sql = "INSERT INTO error_records (customer_id, reading_date, usage1, usage2, usage3, usage4, quality, error_code, error_description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                // Process each record in the provided list
                for (String line : records) {
                    String[] fields = line.split(",");

                    // Check if the record has at least 7 fields, else consider it invalid
                    if (fields.length < 7) {
                        writerForE_Errors.write(line);  // Write invalid record to the error file
                        writerForE_Errors.newLine();
                        continue;
                    }

                    // Extract quality and usage values from the record
                    String quality = fields[6];
                    int energyUsageEarlyMorning = Integer.parseInt(fields[2]);
                    int energyUsageMorning = Integer.parseInt(fields[3]);
                    int energyUsageAfternoon = Integer.parseInt(fields[4]);
                    int energyUsageEvening = Integer.parseInt(fields[5]);

                    // Validate the record based on the criteria
                    if (("A".equals(quality) || "E".equals(quality)) && energyUsageEarlyMorning > 0 && energyUsageMorning > 0 && energyUsageAfternoon > 0 && energyUsageEvening > 0) {
                        validRecords.add(line);  // Add valid record to the result list
                    } else {
                        writerForE_Errors.write(line);  // Write invalid record to the error file
                        writerForE_Errors.newLine();

                        // Extract necessary fields for the error record
                        String customerId = fields[0];
                        String readingDate = fields[1];
                        int errorCode = 0000;  // Example error code by default
                        String errorDescription = "Invalid record";

                        // Convert readingDate to java.sql.Date
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                        LocalDate localDate = LocalDate.parse(readingDate, formatter);
                        java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);

                        // Bind parameters to the prepared statement
                        pstmt.setString(1, customerId);
                        pstmt.setDate(2, sqlDate);
                        pstmt.setInt(3, energyUsageEarlyMorning);
                        pstmt.setInt(4, energyUsageMorning);
                        pstmt.setInt(5, energyUsageAfternoon);
                        pstmt.setInt(6, energyUsageEvening);
                        pstmt.setString(7, quality);
                        pstmt.setInt(8, errorCode);
                        pstmt.setString(9, errorDescription);
                        pstmt.executeUpdate();
                        LOGGER.info("Error record saved to database for customer ID: " + customerId);
                    }
                }
            }

            // Log a message when the validation process is complete
            LOGGER.info("Records validated. Invalid records dumped to E_records.txt.");
        } catch (IOException | SQLException e) {
            // Log a SEVERE error if file writing or reading fails
            LOGGER.log(Level.SEVERE, "Failed to validate records", e);
        }

        return validRecords;
    }
}
