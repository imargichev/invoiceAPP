import org.example.invoiceapp.billing.MonthlyBillGenerator;
import org.example.invoiceapp.data.CustomerUsage;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MonthlyBillGeneratorTest {

    @Test
    void testLoadCustomerNames() {
        Map<String, String> customerNames = MonthlyBillGenerator.loadCustomerNames();
        assertFalse(customerNames.isEmpty(), "Customer names should not be empty");
    }

    @Test
    void testGenerateBill() {
        CustomerUsage usage = new CustomerUsage(100, 50);
        MonthlyBillGenerator.generateBill("123", "John Doe", usage);
        Path txtPath = Paths.get("src/main/resources/output/txt/123.txt");
        assertTrue(Files.exists(txtPath), "TXT bill should be generated");
    }

    @Test
    void testGenerateBillWithInvalidCustomer() {
        CustomerUsage usage = new CustomerUsage(100, 50);
        MonthlyBillGenerator.generateBill("999", "Invalid Customer", usage);
        Path txtPath = Paths.get("src/main/resources/output/txt/999.txt");
        assertFalse(Files.exists(txtPath), "TXT bill should not be generated for an invalid customer");
    }

    @Test
    void testGenerateBillWithZeroUsage() {
        CustomerUsage usage = new CustomerUsage(0, 0);
        MonthlyBillGenerator.generateBill("123", "John Doe", usage);
        Path txtPath = Paths.get("src/main/resources/output/txt/123.txt");
        assertTrue(Files.exists(txtPath), "TXT bill should be generated even with zero usage");
    }

    @Test
    void testGenerateBillWithNegativeUsage() {
        CustomerUsage usage = new CustomerUsage(-10, -5);
        MonthlyBillGenerator.generateBill("123", "John Doe", usage);
        Path txtPath = Paths.get("src/main/resources/output/txt/123.txt");
        assertTrue(Files.exists(txtPath), "TXT bill should be generated even with negative usage");
    }

    @Test
    void testGenerateBillWithLargeUsage() {
        CustomerUsage usage = new CustomerUsage(1000000, 500000);
        MonthlyBillGenerator.generateBill("123", "John Doe", usage);
        Path txtPath = Paths.get("src/main/resources/output/txt/123.txt");
        assertTrue(Files.exists(txtPath), "TXT bill should be generated even with large usage");
    }

    @Test
    void testLoadCustomerNamesWithEmptyFile() {
        // Assuming an empty lookup file is used for this test
        Map<String, String> customerNames = MonthlyBillGenerator.loadCustomerNames();
        assertTrue(customerNames.isEmpty(), "Customer names should be empty for an empty file");
    }

    @Test
    void testLoadCustomerNamesWithInvalidFile() {
        // Assuming an invalid lookup file path is used for this test
        Map<String, String> customerNames = MonthlyBillGenerator.loadCustomerNames();
        assertTrue(customerNames.isEmpty(), "Customer names should be empty for an invalid file");
    }
}
