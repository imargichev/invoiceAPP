/*
package org.example.invoiceapp;

import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class RecordValidatorTest {

    @Test
    void testValidateRecords() {
        List<String> validRecords = RecordValidator.validateRecords("input.txt");
        assertFalse(validRecords.isEmpty(), "Valid records should not be empty");
    }
}

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
        Path pdfPath = Paths.get("src/main/resources/output/pdf/123.pdf");
        assertTrue(Files.exists(txtPath), "TXT bill should be generated");
        assertTrue(Files.exists(pdfPath), "PDF bill should be generated");
    }
}
*/
