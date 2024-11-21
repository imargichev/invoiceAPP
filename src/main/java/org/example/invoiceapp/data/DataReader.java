package org.example.invoiceapp.data;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;

/*The DataReader class is responsible for reading data from files.
 It provides methods to read consumption data and customer lookup data from specified file paths.*/
public class DataReader {
    private static final Logger LOGGER = Logger.getLogger(DataReader.class.getName());

    /*Purpose: Reads consumption data from a specified file and returns it as a list of strings.
      Parameters: String filePath - the path to the file containing the consumption data.
      Returns: List<String> - a list of strings, each representing a line of consumption data.*/
    public static List<String> readConsumptionData(String filePath) {
        List<String> records = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                records.add(line);
            }
            LOGGER.info("Consumption data loaded successfully.");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load consumption data", e);
        }
        return records;
    }

    /*Purpose: Reads customer lookup data from a specified file and returns it as a map of customer IDs to customer names.
      Parameters: String filePath - the path to the file containing the customer lookup data.
      Returns: Map<String, String> - a map where the key is the customer ID and the value is the customer name.*/
    public static Map<String, String> readCustomerLookup(String filePath) {
        Map<String, String> customerNames = new HashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                customerNames.put(fields[0], fields[1]);
            }
            LOGGER.info("Customer lookup data loaded successfully.");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load customer lookup data", e);
        }
        return customerNames;
    }
}
