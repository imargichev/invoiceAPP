package org.example.invoiceapp.data;

import org.example.invoiceapp.util.ConfigLoader;

import java.io.*;
import java.nio.file.*;
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
public class RecordValidator {

    /**
     * Path to the error file where invalid records will be written.
     * The path is loaded from the configuration.
     */
    private static final String ERROR_FILE = ConfigLoader.getProperty("error.file.path");

    /**
     * Logger instance for logging validation events.
     */
    private static final Logger LOGGER = Logger.getLogger(RecordValidator.class.getName());

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
        try {
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(ERROR_FILE))) {

                // Process each record in the provided list
                for (String line : records) {
                    String[] fields = line.split(",");

                    // Check if the record has at least 7 fields, else consider it invalid
                    if (fields.length < 7) {
                        writer.write(line);  // Write invalid record to the error file
                        writer.newLine();
                        continue;
                    }

                    // Extract quality and usage values from the record
                    String quality = fields[6];
                    int usage1 = Integer.parseInt(fields[2]);
                    int usage2 = Integer.parseInt(fields[3]);
                    int usage3 = Integer.parseInt(fields[4]);
                    int usage4 = Integer.parseInt(fields[5]);

                    // Validate the record based on the criteria
                    if (("A".equals(quality) || "E".equals(quality)) && usage1 > 0 && usage2 > 0 && usage3 > 0 && usage4 > 0) {
                        validRecords.add(line);  // Add valid record to the result list
                    } else {
                        writer.write(line);  // Write invalid record to the error file
                        writer.newLine();
                    }
                }

                // Log a message when the validation process is complete
                LOGGER.info("Records validated. Invalid records dumped to E_records.txt.");
            }
        } catch (IOException e) {
            // Log a SEVERE error if file writing or reading fails
            LOGGER.log(Level.SEVERE, "Failed to validate records", e);
        }

        return validRecords;
    }
}
