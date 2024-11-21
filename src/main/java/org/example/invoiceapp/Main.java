package org.example.invoiceapp;

import org.example.invoiceapp.billing.BillGenerator;
import org.example.invoiceapp.billing.MonthlyUsageCalculator;
import org.example.invoiceapp.data.CustomerUsage;
import org.example.invoiceapp.data.DataReader;
import org.example.invoiceapp.data.RecordValidator;

import java.util.List;
import java.util.Map;
import java.util.logging.*;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        String consumptionDataFile = "src/main/resources/input.txt";
        String lookupFile = "src/main/resources/lookup.txt";
        String year = "2023"; // Add the year here

        List<String> records = DataReader.readConsumptionData(consumptionDataFile);
        Map<String, String> customerNames = DataReader.readCustomerLookup(lookupFile);
        List<String> validRecords = RecordValidator.validateRecords(consumptionDataFile);
        Map<String, CustomerUsage> usageMap = MonthlyUsageCalculator.calculateMonthlyUsage(validRecords);

        for (Map.Entry<String, CustomerUsage> entry : usageMap.entrySet()) {
            String customerId = entry.getKey();
            CustomerUsage usage = entry.getValue();
            String customerName = customerNames.get(customerId);

            if (customerName != null) {
                // Extract the issue date from the record
                String issueDate = extractIssueDate(records, customerId);
                BillGenerator.generateTxtBill(customerId, customerName, usage, year, issueDate); // Pass the issue date as the fifth argument
            } else {
                LOGGER.warning("Customer name not found for ID: " + customerId);
            }
        }
    }

    private static String extractIssueDate(List<String> records, String customerId) {
        for (String record : records) {
            String[] fields = record.split(",");
            if (fields[0].equals(customerId)) {
                return fields[1]; // Assuming the date is the second field
            }
        }
        return "01.01.1970"; // Default date if not found
    }
}
