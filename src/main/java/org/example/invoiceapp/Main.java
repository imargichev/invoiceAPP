package org.example.invoiceapp;

import org.example.invoiceapp.billing.BillGenerator;
import org.example.invoiceapp.billing.MonthlyUsageCalculator;
import org.example.invoiceapp.data.CustomerUsage;
import org.example.invoiceapp.data.DataReader;
import org.example.invoiceapp.data.RecordValidator;
import org.example.invoiceapp.util.ConfigLoader;
import org.example.invoiceapp.util.Initializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.logging.*;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        // Ensure that all directory exists
        Initializer.initialize();

        // Fetch the file paths from the application.properties file
        //Here we get the path to  input.txt
        String consumptionDataFile = ConfigLoader.getProperty("consumption.data.path");
        //Here we get the date for each client
        String lookupFile = ConfigLoader.getProperty("customer.lookup.path");




        String outputDir = "output";
        try {
            Files.createDirectories(Paths.get(outputDir));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to create output directory", e);
        }


        // Reading the consumption and customer data
        List<String> records = DataReader.readConsumptionData(consumptionDataFile);
        Map<String, String> customerNames = DataReader.readCustomerLookup(lookupFile);

        // Validate records directly from the records list
        List<String> validRecords = RecordValidator.validateRecords(records);
        Map<String, CustomerUsage> usageMap = MonthlyUsageCalculator.calculateMonthlyUsage(validRecords);

        // Generate bills for each customer
        for (Map.Entry<String, CustomerUsage> entry : usageMap.entrySet()) {
            String customerId = entry.getKey();
            CustomerUsage usage = entry.getValue();
            String customerName = customerNames.get(customerId);

            if (customerName != null) {
                // Extract the issue date from the records for the customer
                String issueDate = extractIssueDate(records, customerId);
                // Generate the text bill
                BillGenerator.generateTxtBill(customerId, customerName, usage, issueDate);
            } else {
                LOGGER.warning("Customer name not found for ID: " + customerId);
            }
        }
    }

    private static String extractIssueDate(List<String> records, String customerId) {
        for (String record : records) {
            String[] fields = record.split(",");
            if (fields[0].equals(customerId)) {
                return fields[1]; // Assuming the date is the second field in the record
            }
        }
        return "01.01.1970"; // Default date if not found
    }
}
