package org.example.invoiceapp;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.logging.*;

public class BillGenerator {
    private static final String OUTPUT_DIR = "src/main/resources/output/txt/";
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




    //This is very nice version
    private static void generatePdfBill(String customerId, String sanitizedCustomerName, String customerName, CustomerUsage usage, double daytimeCost, double nighttimeCost, double totalCost, String year) {

        String outputFileName = PDF_OUTPUT_DIR + customerId + "_" + sanitizedCustomerName + "_" + year + "_bill.pdf";
        Document document = new Document();

        try {
            Files.createDirectories(Paths.get(PDF_OUTPUT_DIR));
            PdfWriter.getInstance(document, new FileOutputStream(outputFileName));
            document.open();

            // Load the custom font that supports Cyrillic
            String fontPath = "src/main/resources/fonts/DejaVuSans.ttf"; // Path to your .ttf file
            BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font titleFont = new Font(baseFont, 18, Font.BOLD);
            Font sectionTitleFont = new Font(baseFont, 14, Font.BOLD);
            Font regularFont = new Font(baseFont, 12);

            // Title
            Paragraph title = new Paragraph("Фактура за електроенергия", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Issue date and invoice number
            document.add(new Paragraph("Дата на издаване: 21.11.2024", regularFont));
            document.add(new Paragraph("Номер на фактура: INV-379208", regularFont));
            document.add(new Paragraph(" ")); // Empty line

            // Client details
            document.add(new Paragraph("Клиент:", sectionTitleFont));
            document.add(new Paragraph("Име: " + customerName, regularFont));
            document.add(new Paragraph("Клиентски номер: " + customerId, regularFont));
            document.add(new Paragraph(" ")); // Empty line

            // Consumption data
            document.add(new Paragraph("Данни за потребление:", sectionTitleFont));
            PdfPTable consumptionTable = new PdfPTable(4);
            consumptionTable.setWidthPercentage(100);
            consumptionTable.addCell(new PdfPCell(new Phrase("Година", regularFont)));
            consumptionTable.addCell(new PdfPCell(new Phrase("Дневна консумация", regularFont)));
            consumptionTable.addCell(new PdfPCell(new Phrase("Нощна консумация", regularFont)));
            consumptionTable.addCell(new PdfPCell(new Phrase("Общо потребление", regularFont)));
            consumptionTable.addCell(new PdfPCell(new Phrase(year, regularFont)));
            consumptionTable.addCell(new PdfPCell(new Phrase(usage.getDaytimeUsage() + " kWh", regularFont)));
            consumptionTable.addCell(new PdfPCell(new Phrase(usage.getNighttimeUsage() + " kWh", regularFont)));
            consumptionTable.addCell(new PdfPCell(new Phrase((usage.getDaytimeUsage() + usage.getNighttimeUsage()) + " kWh", regularFont)));
            document.add(consumptionTable);
            document.add(new Paragraph(" ")); // Empty line

            // Cost calculation
            document.add(new Paragraph("Изчисление на разходите:", sectionTitleFont));
            document.add(new Paragraph("Дневна консумация: " + usage.getDaytimeUsage() + " kWh × 0,15 лв./kWh = " + String.format("%.2f", daytimeCost) + " лв.", regularFont));
            document.add(new Paragraph("Нощна консумация: " + usage.getNighttimeUsage() + " kWh × 0,05 лв./kWh = " + String.format("%.2f", nighttimeCost) + " лв.", regularFont));
            document.add(new Paragraph("Обща сума за плащане: " + String.format("%.2f", totalCost) + " лв.", new Font(baseFont, 12, Font.BOLD)));
            document.add(new Paragraph(" ")); // Empty line

            // Payment method
            document.add(new Paragraph("Метод на плащане:", sectionTitleFont));
            document.add(new Paragraph("Плащането може да се извърши чрез банков превод или на каса. Моля, упоменете номера на фактурата при извършване на плащането.", regularFont));
            document.add(new Paragraph(" ")); // Empty line

            // Thank you note
            document.add(new Paragraph("Благодарим Ви, че използвате нашите услуги!", regularFont));

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
