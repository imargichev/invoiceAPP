package org.example.invoiceapp.data;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;
import org.example.invoiceapp.util.ConfigLoader;

import static org.example.invoiceapp.data.ClientManager.addClient;

/**
 * The `DataReader` class is responsible for reading data files related to consumption
 */
public class DataReaderFromInputFiles {
    // Static configuration properties loaded via ConfigLoader
    private static final Logger LOGGER = Logger.getLogger(DataReaderFromInputFiles.class.getName());
    private static final String CONSUMPTION_DATA_PATH = ConfigLoader.getProperty("consumption.data.path");
    private static final String CUSTOMER_LOOKUP_PATH = ConfigLoader.getProperty("customer.lookup.path");

    /**
     * Reads consumption data from a file and returns it as a list of strings.
     * Each line in the file represents a separate record.
     *
     *
     * @return a list of strings representing the consumption records.
     */
    public static List<String> readConsumptionData() {
        List<String> records = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(CONSUMPTION_DATA_PATH))) {
            String line;
            // Read each line of the consumption data file and add it to the records list.
            while ((line = reader.readLine()) != null) {
                records.add(line);
            }
            LOGGER.info("Consumption data loaded successfully in memory.");
        } catch (IOException e) {
            // Log an error message if reading the consumption data fails.
            LOGGER.log(Level.SEVERE, "Failed to load consumption data", e);
        }
        return records;
    }

    /**
     * Reads customer lookup data from a file and returns it as a map.
     * The map keys are customer IDs (as strings), and the values are customer names (as strings).
     *
     *
     * @return a map containing customer IDs and names.
     */
    public static Map<String, String> readCustomerLookup() {
        Map<String, String> customerNames = new HashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(CUSTOMER_LOOKUP_PATH))) {
            String line;
            // Read each line of the customer lookup file, split by comma, and add the customer ID and name to the map.
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                customerNames.put(fields[0], fields[1]);
                addClient(fields[0], fields[1]);
            }
            LOGGER.info("Customer lookup data loaded successfully in memory.");
        } catch (IOException e) {
            // Log an error message if reading the customer lookup data fails.
            LOGGER.log(Level.SEVERE, "Failed to load customer lookup data", e);
        }
        return customerNames;
    }
}
