package org.example.invoiceapp.data;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;
import org.example.invoiceapp.util.ConfigLoader;

public class DataReader {
    private static final Logger LOGGER = Logger.getLogger(DataReader.class.getName());

    private static final String CONSUMPTION_DATA_PATH = ConfigLoader.getProperty("consumption.data.path");
    private static final String CUSTOMER_LOOKUP_PATH = ConfigLoader.getProperty("customer.lookup.path");

    public static List<String> readConsumptionData(String consumptionDataFile) {
        List<String> records = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(CONSUMPTION_DATA_PATH))) {
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

    public static Map<String, String> readCustomerLookup(String lookupFile) {
        Map<String, String> customerNames = new HashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(CUSTOMER_LOOKUP_PATH))) {
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
