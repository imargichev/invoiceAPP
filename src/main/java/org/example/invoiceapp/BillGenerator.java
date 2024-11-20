package org.example.invoiceapp;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.logging.*;

public class BillGenerator {
    private static final String OUTPUT_DIR = "src/main/resources/output/";
    private static final String PDF_OUTPUT_DIR = "src/main/resources/output/pdf/";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/invoiceapp";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "SUP3R_p@ss";
    private static final Logger LOGGER = Logger.getLogger(BillGenerator.class.getName());

    public static void generateTxtBill(String customerId, String customerName, CustomerUsage usage, String year) {
        String sanitizedCustomerName = customerName.replaceAll("[^a-zA-Z0-9]", "_");
        String outputFileName = OUTPUT_DIR + customerId + "_" + sanitizedCustomerName + "_" + year + "_bill.txt";
        double daytimeCost = usage.getDaytimeUsage() * 0.15;
        double nighttimeCost = usage.getNighttimeUsage() * 0.05;
        double totalCost = daytimeCost + nighttimeCost;

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFileName))) {
            writer.write("Customer Name: " + customerName);
            writer.newLine();
            writer.write("Customer ID: " + customerId);
            writer.newLine();
            writer.newLine();
            writer.write("Daytime Usage: " + usage.getDaytimeUsage() + " units @ 0.15 BGN/unit = " + String.format("%.2f", daytimeCost) + " BGN");
            writer.newLine();
            writer.write("Nighttime Usage: " + usage.getNighttimeUsage() + " units @ 0.05 BGN/unit = " + String.format("%.2f", nighttimeCost) + " BGN");
            writer.newLine();
            writer.newLine();
            writer.write("Total Usage: " + (usage.getDaytimeUsage() + usage.getNighttimeUsage()) + " units");
            writer.newLine();
            writer.write("Total Cost: " + String.format("%.2f", totalCost) + " BGN");
            writer.newLine();
            LOGGER.info("TXT bill generated: " + outputFileName);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to generate TXT bill", e);
        }

        generatePdfBill(customerId, sanitizedCustomerName, customerName, usage, daytimeCost, nighttimeCost, totalCost, year);
        saveBillToDatabase(customerId, customerName, usage);
    }

    private static void generatePdfBill(String customerId, String sanitizedCustomerName, String customerName, CustomerUsage usage, double daytimeCost, double nighttimeCost, double totalCost, String year) {
        String outputFileName = PDF_OUTPUT_DIR + customerId + "_" + sanitizedCustomerName + "_" + year + "_bill.pdf";
        Document document = new Document();

        try {
            Files.createDirectories(Paths.get(PDF_OUTPUT_DIR));
            PdfWriter.getInstance(document, new FileOutputStream(outputFileName));
            document.open();
            document.add(new Paragraph("Customer Name: " + customerName));
            document.add(new Paragraph("Customer ID: " + customerId));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Daytime Usage: " + usage.getDaytimeUsage() + " units @ 0.15 BGN/unit = " + String.format("%.2f", daytimeCost) + " BGN"));
            document.add(new Paragraph("Nighttime Usage: " + usage.getNighttimeUsage() + " units @ 0.05 BGN/unit = " + String.format("%.2f", nighttimeCost) + " BGN"));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Total Usage: " + (usage.getDaytimeUsage() + usage.getNighttimeUsage()) + " units"));
            document.add(new Paragraph("Total Cost: " + String.format("%.2f", totalCost) + " BGN"));
            LOGGER.info("PDF bill generated: " + outputFileName);
        } catch (DocumentException | IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to generate PDF bill", e);
        } finally {
            document.close();
        }
    }

    private static void saveBillToDatabase(String customerId, String customerName, CustomerUsage usage) {
        double daytimeCost = usage.getDaytimeUsage() * 0.15;
        double nighttimeCost = usage.getNighttimeUsage() * 0.05;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO bills (customer_id, customer_name, daytime_usage, nighttime_usage, daytime_cost, nighttime_cost) VALUES (?, ?, ?, ?, ?, ?)")) {
            pstmt.setString(1, customerId);
            pstmt.setString(2, customerName);
            pstmt.setInt(3, usage.getDaytimeUsage());
            pstmt.setInt(4, usage.getNighttimeUsage());
            pstmt.setDouble(5, daytimeCost);
            pstmt.setDouble(6, nighttimeCost);
            pstmt.executeUpdate();
            LOGGER.info("Bill saved to database for customer ID: " + customerId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to save bill to database", e);
        }
    }
}
