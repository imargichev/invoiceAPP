package org.example.invoiceapp;

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
                BillGenerator.generateTxtBill(customerId, customerName, usage, year); // Pass the year as the fourth argument
            } else {
                LOGGER.warning("Customer name not found for ID: " + customerId);
            }
        }
    }
}
