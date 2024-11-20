package org.example.invoiceapp;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;

public class DataReader {
    private static final Logger LOGGER = Logger.getLogger(DataReader.class.getName());

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
