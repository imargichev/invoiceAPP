package org.example.invoiceapp;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.*;
import java.util.logging.*;

public class PdfBillGenerator {
    private static final String OUTPUT_DIR = "src/main/resources/output/";
    private static final Logger LOGGER = Logger.getLogger(PdfBillGenerator.class.getName());

    public static void generatePdfBill(String customerId, String customerName, CustomerUsage usage) {
        String outputFileName = OUTPUT_DIR + customerId + ".pdf";
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(outputFileName));
            document.open();
            document.add(new Paragraph("Customer ID: " + customerId));
            document.add(new Paragraph("Customer Name: " + customerName));
            document.add(new Paragraph("Daytime Usage: " + usage.getDaytimeUsage() + " kWh"));
            document.add(new Paragraph("Nighttime Usage: " + usage.getNighttimeUsage() + " kWh"));
            document.add(new Paragraph("Total Daytime Cost: " + (usage.getDaytimeUsage() * 0.15) + " BGN"));
            document.add(new Paragraph("Total Nighttime Cost: " + (usage.getNighttimeUsage() * 0.05) + " BGN"));
            LOGGER.info("PDF bill generated: " + outputFileName);
        } catch (DocumentException | IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to generate PDF bill", e);
        } finally {
            document.close();
        }
    }
}
