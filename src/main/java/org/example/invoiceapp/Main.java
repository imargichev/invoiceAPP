package org.example.invoiceapp;

import org.example.invoiceapp.billing.BillGenerator;
import org.example.invoiceapp.billing.MonthlyUsageCalculator;
import org.example.invoiceapp.data.CustomerUsage;
import org.example.invoiceapp.data.DataReader;
import org.example.invoiceapp.data.RecordValidator;
import org.example.invoiceapp.util.ConfigLoader;
import org.example.invoiceapp.util.Initializer;


import java.util.List;
import java.util.Map;
import java.util.logging.*;
/**
 * Main class for the InvoiceApp application.
 *
 * <p>
 * This class orchestrates the processing of electricity consumption data and
 * customer lookup information to generate monthly usage summaries and bills
 * for each customer. The application validates records, calculates usage,
 * and generates text-based bills for valid customer records.
 * </p>
 *
 */
public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        // Ensure required directories exist and load file paths
        Initializer.initialize();
        String consumptionDataFile = ConfigLoader.getProperty("consumption.data.path");
        String lookupFile = ConfigLoader.getProperty("customer.lookup.path");

        // Read data from files
        List<String> records = DataReader.readConsumptionData(consumptionDataFile);
        Map<String, String> customerNames = DataReader.readCustomerLookup(lookupFile);

        // Validate and filter records
        List<String> validRecords = RecordValidator.validateRecords(records);
        Map<String, CustomerUsage> usageMap = MonthlyUsageCalculator.calculateMonthlyUsage(validRecords);

        // Generate bills for customers
        for (Map.Entry<String, CustomerUsage> entry : usageMap.entrySet()) {
            String customerId = entry.getKey();
            CustomerUsage usage = entry.getValue();
            String customerName = customerNames.get(customerId);

            if (customerName != null) {
                // Extract issue date from records
                String issueDate = extractIssueDate(records, customerId);
                // Generate text bill
                BillGenerator.generateTxtBill(customerId, customerName, usage, issueDate);
            } else {
                LOGGER.warning("Customer name not found for ID: " + customerId);
            }
        }
    }

    /**
     * Extracts the issue date for a given customer from the consumption records.
     *
     * @param records    the list of consumption records
     * @param customerId the ID of the customer whose issue date is to be retrieved
     * @return the issue date in `dd.mm.yyyy` format if found, or a default date of `01.01.1970`
     *         if the date is not found in the records
     */
    private static String extractIssueDate(List<String> records, String customerId) {
        for (String record : records) {
            String[] fields = record.split(",");
            if (fields[0].equals(customerId)) {
                return fields[1]; // Assuming the date is the second field in the record
            }
        }
        return "01.01.1970"; // Default date if no match is found
    }
}
