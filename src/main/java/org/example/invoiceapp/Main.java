package org.example.invoiceapp;

import org.example.invoiceapp.billing.BillGeneratorTxtAndPDF;
import org.example.invoiceapp.billing.MonthlyUsageCalculator;
import org.example.invoiceapp.data.CustomerUsage;
import org.example.invoiceapp.data.DataReaderFromInputFiles;
import org.example.invoiceapp.data.DataValidator;
import org.example.invoiceapp.util.ConfigLoader;
import org.example.invoiceapp.util.Initializer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.logging.*;

import static org.example.invoiceapp.data.FileProcessor.processFiles;

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
    private static final Path INPUT_FILE = Path.of(ConfigLoader.getProperty("consumption.data.path"));
    private static final Path LOOKUP_FILE = Path.of(ConfigLoader.getProperty("customer.lookup.path"));

    public static void main(String[] args) {

        // Check if required files exist
        if (!Files.exists(INPUT_FILE) || !Files.exists(LOOKUP_FILE)) {
            LOGGER.severe("Required input files are missing. Terminating the program.");
            System.exit(1);
        }

        // Ensure required directories exist and load file paths
        Initializer.initialize();


        // Read data from files
        List<String> records = DataReaderFromInputFiles.readConsumptionData();
        Map<String, String> customerNames = DataReaderFromInputFiles.readCustomerLookup();

        // Validate and filter records
        List<String> validRecords = DataValidator.validateRecords(records);
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
                BillGeneratorTxtAndPDF.generateTxtBill(customerId, customerName, usage, issueDate);
            } else {
                LOGGER.warning("Customer name not found for ID: " + customerId);
            }
        }

        // Process files at the end
        processFiles();
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
