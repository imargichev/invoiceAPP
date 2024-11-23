package org.example.invoiceapp.data;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;
import org.example.invoiceapp.util.ConfigLoader;

/**
 * The `DataReader` class is responsible for reading data files related to consumption
 */
public class DataReader {

    /**
     * Logger instance to log messages for the DataReader class.
     */
    private static final Logger LOGGER = Logger.getLogger(DataReader.class.getName());

    /**
     * Path to the consumption data file, loaded from configuration properties.
     */
    private static final String CONSUMPTION_DATA_PATH = ConfigLoader.getProperty("consumption.data.path");

    /**
     * Path to the customer lookup data file, loaded from configuration properties.
     */
    private static final String CUSTOMER_LOOKUP_PATH = ConfigLoader.getProperty("customer.lookup.path");

    /**
     * Reads consumption data from a file and returns it as a list of strings.
     * Each line in the file represents a separate record.
     *
     * @param consumptionDataFile the path to the consumption data file (not used here as the path is loaded from config)
     * @return a list of strings representing the consumption records.
     */
    public static List<String> readConsumptionData(String consumptionDataFile) {
        List<String> records = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(CONSUMPTION_DATA_PATH))) {
            String line;
            // Read each line of the consumption data file and add it to the records list.
            while ((line = reader.readLine()) != null) {
                records.add(line);
            }
            LOGGER.info("Consumption data loaded successfully.");
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
     * @param lookupFile the path to the customer lookup file (not used here as the path is loaded from config)
     * @return a map containing customer IDs and names.
     */
    public static Map<String, String> readCustomerLookup(String lookupFile) {
        Map<String, String> customerNames = new HashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(CUSTOMER_LOOKUP_PATH))) {
            String line;
            // Read each line of the customer lookup file, split by comma, and add the customer ID and name to the map.
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                customerNames.put(fields[0], fields[1]);
            }
            LOGGER.info("Customer lookup data loaded successfully.");
        } catch (IOException e) {
            // Log an error message if reading the customer lookup data fails.
            LOGGER.log(Level.SEVERE, "Failed to load customer lookup data", e);
        }
        return customerNames;
    }
}
