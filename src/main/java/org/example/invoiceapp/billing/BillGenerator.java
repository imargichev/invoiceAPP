package org.example.invoiceapp.billing;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.example.invoiceapp.data.CustomerUsage;
import org.example.invoiceapp.util.ConfigLoader;

import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.logging.*;

/*The BillGenerator class is responsible for generating text and PDF bills for customers
 based on their usage data and saving the bill information to a database. */
public class BillGenerator {
    private static final String OUTPUT_DIR = ConfigLoader.getProperty("txt.output.path");
    private static final String PDF_OUTPUT_DIR = ConfigLoader.getProperty("pdf.output.path");
    private static final String DB_URL = ConfigLoader.getProperty("db.url");
    private static final String DB_USER = ConfigLoader.getProperty("db.username");
    private static final String DB_PASSWORD = ConfigLoader.getProperty("db.password");
    private static final Logger LOGGER = Logger.getLogger(BillGenerator.class.getName());

    /*This method generates a text bill for a customer and saves it to a file.
     It also calls methods to generate a PDF bill and save the bill to a database.*/
    public static void generateTxtBill(String customerId, String customerName, CustomerUsage usage, String issueDate) {
        // Sanitize customer name for file naming
        String year = "2023"; // This is the default static year during file generation
        String sanitizedCustomerName = customerName.replaceAll("[^a-zA-Z0-9]", "_");
        // you should change the date here because it will be always the same
        String outputFileName = OUTPUT_DIR + customerId + "_" + sanitizedCustomerName + "_" + year + "_bill.txt";
        // Calculate costs
        double daytimeCost = usage.getDaytimeUsage() * 0.15;
        double nighttimeCost = usage.getNighttimeUsage() * 0.05;
        double totalCost = daytimeCost + nighttimeCost;

        // Записване на детайлите на фактурата в текстов файл
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFileName))) {
            writer.write("=========================================");
            writer.newLine();
            writer.write("              ФАКТУРА                   ");
            writer.newLine();
            writer.write("=========================================");
            writer.newLine();
            writer.write(String.format("%-20s: %s", "Име на клиент", customerName));
            writer.newLine();
            writer.write(String.format("%-20s: %s", "Клиентски номер", customerId));
            writer.newLine();
            writer.write("=========================================");
            writer.newLine();
            writer.write(String.format("%-20s: %d kWh @ 0.15 лв./kWh = %.2f лв.", "Дневна консумация", usage.getDaytimeUsage(), daytimeCost));
            writer.newLine();
            writer.write(String.format("%-20s: %d kWh @ 0.05 лв./kWh = %.2f лв.", "Нощна консумация", usage.getNighttimeUsage(), nighttimeCost));
            writer.newLine();
            writer.write("=========================================");
            writer.newLine();
            writer.write(String.format("%-20s: %d kWh", "Общо потребление", (usage.getDaytimeUsage() + usage.getNighttimeUsage())));
            writer.newLine();
            writer.write(String.format("%-20s: %.2f лв.", "Обща сума", totalCost));
            writer.newLine();
            writer.write("=========================================");
            writer.newLine();
            LOGGER.info("\u001B[32mThe invoice was generated successfully " + outputFileName + "\u001B[0m");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "The invoice was not generated ", e);
        }

        // Generate PDF bill and save to database
        generatePdfBill(customerId, sanitizedCustomerName, customerName, usage, daytimeCost, nighttimeCost, totalCost, year, issueDate);
        saveBillToDatabase(customerId, customerName, usage);
    }

    /*This method generates a PDF bill for a customer and saves it to a file.*/
    private static void generatePdfBill(String customerId, String sanitizedCustomerName, String customerName, CustomerUsage usage, double daytimeCost, double nighttimeCost, double totalCost, String year, String issueDate) {
        String formattedDate = issueDate.replace(".", "-");
        String outputFileName = PDF_OUTPUT_DIR + customerId + "_" + sanitizedCustomerName + "_" + formattedDate + "_bill.pdf";
        Document document = new Document();

        try {
            Files.createDirectories(Paths.get(PDF_OUTPUT_DIR));
            BaseFont baseFont = BaseFont.createFont("src/main/resources/fonts/DejaVuSans.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font titleFont = new Font(baseFont, 18, Font.BOLD);
            Font sectionTitleFont = new Font(baseFont, 14, Font.BOLD);
            Font regularFont = new Font(baseFont, 12);

            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputFileName));
            writer.setPageEvent(new FooterHandler(sectionTitleFont, regularFont));
            document.open();

            // Add header with logo and title
            String logoPath = "src/main/resources/logo/logo.png";
            Image logo = Image.getInstance(logoPath);
            logo.scaleToFit(100, 100);
            logo.setAlignment(Element.ALIGN_CENTER);
            document.add(logo);

            Paragraph title = new Paragraph("Фактура за електроенергия", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph(" ")); // Empty line for spacing

            // Issue date, invoice number, and client details in the same row
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingBefore(10f);
            infoTable.setSpacingAfter(10f);

            PdfPCell cell1 = new PdfPCell(new Phrase("Дата на издаване: " + issueDate, regularFont));
            cell1.setBorder(Rectangle.NO_BORDER);
            infoTable.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Phrase("Номер на фактура: INV-379208", regularFont));
            cell2.setBorder(Rectangle.NO_BORDER);
            infoTable.addCell(cell2);

            PdfPCell cell3 = new PdfPCell(new Phrase("Клиент:", sectionTitleFont));
            cell3.setBorder(Rectangle.NO_BORDER);
            infoTable.addCell(cell3);

            PdfPCell cell4 = new PdfPCell(new Phrase("Име: " + customerName, regularFont));
            cell4.setBorder(Rectangle.NO_BORDER);
            infoTable.addCell(cell4);

            PdfPCell cell5 = new PdfPCell(new Phrase("Клиентски номер: " + customerId, regularFont));
            cell5.setBorder(Rectangle.NO_BORDER);
            infoTable.addCell(cell5);

            document.add(infoTable);

            document.add(new Paragraph(" ")); // Empty line for spacing

            // Consumption data
            document.add(new Paragraph("Данни за потребление:", sectionTitleFont));
            PdfPTable consumptionTable = new PdfPTable(4);
            consumptionTable.setWidthPercentage(100);
            consumptionTable.setSpacingBefore(10f);
            consumptionTable.setSpacingAfter(10f);

            // Add table headers
            addTableHeader(consumptionTable, "Година", regularFont);
            addTableHeader(consumptionTable, "Дневна консумация", regularFont);
            addTableHeader(consumptionTable, "Нощна консумация", regularFont);
            addTableHeader(consumptionTable, "Общо потребление", regularFont);

            // Add table data
            addTableCell(consumptionTable, year, regularFont);
            addTableCell(consumptionTable, usage.getDaytimeUsage() + " kWh", regularFont);
            addTableCell(consumptionTable, usage.getNighttimeUsage() + " kWh", regularFont);
            addTableCell(consumptionTable, (usage.getDaytimeUsage() + usage.getNighttimeUsage()) + " kWh", regularFont);

            document.add(consumptionTable);
            document.add(new Paragraph(" ")); // Empty line for spacing

            // Cost calculation
            document.add(new Paragraph("Изчисление на разходите:", sectionTitleFont));
            document.add(new Paragraph("Дневна консумация: " + usage.getDaytimeUsage() + " kWh × 0,15 лв./kWh = " + String.format("%.2f", daytimeCost) + " лв.", regularFont));
            document.add(new Paragraph("Нощна консумация: " + usage.getNighttimeUsage() + " kWh × 0,05 лв./kWh = " + String.format("%.2f", nighttimeCost) + " лв.", regularFont));
            document.add(new Paragraph("Обща сума за плащане: " + String.format("%.2f", totalCost) + " лв.", new Font(baseFont, 12, Font.BOLD)));
            document.add(new Paragraph(" ")); // Empty line for spacing

            LOGGER.info("\u001B[37mPDF bill generated: " + outputFileName + "\u001B[0m");
        } catch (DocumentException | IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to generate PDF bill", e);
        } finally {
            document.close();
        }
    }

    // This method adds a header cell to a PDF table.
    private static void addTableHeader(PdfPTable table, String headerTitle, Font font) {
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
        header.setBorderWidth(2);
        header.setPhrase(new Phrase(headerTitle, font));
        table.addCell(header);
    }

    // This method adds a cell with text to a PDF table.
    private static void addTableCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        table.addCell(cell);
    }

    // This inner class handles the footer of each page in the PDF document
    private static class FooterHandler extends PdfPageEventHelper {
        private Font sectionTitleFont;
        private Font regularFont;

        public FooterHandler(Font sectionTitleFont, Font regularFont) {
            this.sectionTitleFont = sectionTitleFont;
            this.regularFont = regularFont;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            // Add payment method and thank you note
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, new Phrase("Метод на плащане:", sectionTitleFont), 36, 50, 0);
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, new Phrase("Плащането може да се извърши чрез банков превод или на каса.", regularFont), 36, 40, 0);
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, new Phrase("Благодарим Ви, че използвате нашите услуги!", regularFont), 36, 30, 0);

            PdfPTable footer = new PdfPTable(1);
            footer.setTotalWidth(523);
            footer.setLockedWidth(true);
            footer.getDefaultCell().setFixedHeight(20);
            footer.getDefaultCell().setBorder(Rectangle.TOP);
            footer.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            footer.addCell(new Phrase(String.format("Страница %d", writer.getPageNumber())));
            footer.writeSelectedRows(0, -1, 36, 20, writer.getDirectContent());
        }
    }

    /*This method saves the bill details to a database.
    The saveBillToDatabase method is responsible for saving the bill details to a database.
     It takes the customer ID, customer name, and usage data as parameters, calculates the costs,
     and inserts the bill information into the bills table in the database.*/
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

/*


package org.example.invoiceapp.billing;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.example.invoiceapp.data.CustomerUsage;
import org.example.invoiceapp.util.ConfigLoader;

import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.logging.*;
*/
/*The BillGenerator class is responsible for generating text and PDF bills for customers
 based on their usage data and saving the bill information to a database. *//*

public class BillGenerator {
    private static final String OUTPUT_DIR = ConfigLoader.getProperty("txt.output.path");
    private static final String PDF_OUTPUT_DIR = ConfigLoader.getProperty("pdf.output.path");
    private static final String DB_URL = ConfigLoader.getProperty("db.url");;
    private static final String DB_USER = ConfigLoader.getProperty("db.username");
    private static final String DB_PASSWORD = ConfigLoader.getProperty("db.password");
    private static final Logger LOGGER = Logger.getLogger(BillGenerator.class.getName());

    */
/*This method generates a text bill for a customer and saves it to a file.
     It also calls methods to generate a PDF bill and save the bill to a database.*//*

    public static void generateTxtBill(String customerId, String customerName, CustomerUsage usage, String issueDate) {

        // Sanitize customer name for file naming
        String year = "2023"; // This is the default static year during file generation
        String sanitizedCustomerName = customerName.replaceAll("[^a-zA-Z0-9]", "_");
        //you should change the date here because it will be always the same
        String outputFileName = OUTPUT_DIR + customerId + "_" + sanitizedCustomerName + "_" + year + "_bill.txt";
        // Calculate costs
        double daytimeCost = usage.getDaytimeUsage() * 0.15;
        double nighttimeCost = usage.getNighttimeUsage() * 0.05;
        double totalCost = daytimeCost + nighttimeCost;

// Записване на детайлите на фактурата в текстов файл
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFileName))) {
            writer.write("=========================================");
            writer.newLine();
            writer.write("              ФАКТУРА                   ");
            writer.newLine();
            writer.write("=========================================");
            writer.newLine();
            writer.write(String.format("%-20s: %s", "Име на клиент", customerName));
            writer.newLine();
            writer.write(String.format("%-20s: %s", "Клиентски номер", customerId));
            writer.newLine();
            writer.write("=========================================");
            writer.newLine();
            writer.write(String.format("%-20s: %d kWh @ 0.15 лв./kWh = %.2f лв.", "Дневна консумация", usage.getDaytimeUsage(), daytimeCost));
            writer.newLine();
            writer.write(String.format("%-20s: %d kWh @ 0.05 лв./kWh = %.2f лв.", "Нощна консумация", usage.getNighttimeUsage(), nighttimeCost));
            writer.newLine();
            writer.write("=========================================");
            writer.newLine();
            writer.write(String.format("%-20s: %d kWh", "Общо потребление", (usage.getDaytimeUsage() + usage.getNighttimeUsage())));
            writer.newLine();
            writer.write(String.format("%-20s: %.2f лв.", "Обща сума", totalCost));
            writer.newLine();
            writer.write("=========================================");
            writer.newLine();
            LOGGER.info("\u001B[32mThe invoice was generated successfully " + outputFileName + "\u001B[0m");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "The invoice was not generated ", e);        }

        // Generate PDF bill and save to database
        generatePdfBill(customerId, sanitizedCustomerName, customerName, usage, daytimeCost, nighttimeCost, totalCost, year, issueDate);
        saveBillToDatabase(customerId, customerName, usage);
    }
    */
/*This method generates a PDF bill for a customer and saves it to a file.*//*

    private static void generatePdfBill(String customerId, String sanitizedCustomerName, String customerName, CustomerUsage usage, double daytimeCost, double nighttimeCost, double totalCost, String year, String issueDate) {
        String formattedDate = issueDate.replace(".", "-");
        String outputFileName = PDF_OUTPUT_DIR + customerId + "_" + sanitizedCustomerName + "_" + formattedDate + "_bill.pdf";
        Document document = new Document();

        try {
            Files.createDirectories(Paths.get(PDF_OUTPUT_DIR));
            BaseFont baseFont = BaseFont.createFont("src/main/resources/fonts/DejaVuSans.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font titleFont = new Font(baseFont, 18, Font.BOLD);
            Font sectionTitleFont = new Font(baseFont, 14, Font.BOLD);
            Font regularFont = new Font(baseFont, 12);

            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputFileName));
            writer.setPageEvent(new FooterHandler(sectionTitleFont, regularFont));
            document.open();

            // Add header with logo and title
            String logoPath = "src/main/resources/logo/logo.png";
            Image logo = Image.getInstance(logoPath);
            logo.scaleToFit(100, 100);
            logo.setAlignment(Element.ALIGN_CENTER);
            document.add(logo);

            Paragraph title = new Paragraph("Фактура за електроенергия", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph(" ")); // Empty line for spacing

            // Issue date, invoice number, and client details in the same row
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingBefore(10f);
            infoTable.setSpacingAfter(10f);

            PdfPCell cell1 = new PdfPCell(new Phrase("Дата на издаване: " + issueDate, regularFont));
            cell1.setBorder(Rectangle.NO_BORDER);
            infoTable.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Phrase("Номер на фактура: INV-379208", regularFont));
            cell2.setBorder(Rectangle.NO_BORDER);
            infoTable.addCell(cell2);

            PdfPCell cell3 = new PdfPCell(new Phrase("Клиент:", sectionTitleFont));
            cell3.setBorder(Rectangle.NO_BORDER);
            infoTable.addCell(cell3);

            PdfPCell cell4 = new PdfPCell(new Phrase("Име: " + customerName, regularFont));
            cell4.setBorder(Rectangle.NO_BORDER);
            infoTable.addCell(cell4);

            PdfPCell cell5 = new PdfPCell(new Phrase("Клиентски номер: " + customerId, regularFont));
            cell5.setBorder(Rectangle.NO_BORDER);
            infoTable.addCell(cell5);

            document.add(infoTable);

            document.add(new Paragraph(" ")); // Empty line for spacing

            // Consumption data
            document.add(new Paragraph("Данни за потребление:", sectionTitleFont));
            PdfPTable consumptionTable = new PdfPTable(4);
            consumptionTable.setWidthPercentage(100);
            consumptionTable.setSpacingBefore(10f);
            consumptionTable.setSpacingAfter(10f);

            // Add table headers
            addTableHeader(consumptionTable, "Година", regularFont);
            addTableHeader(consumptionTable, "Дневна консумация", regularFont);
            addTableHeader(consumptionTable, "Нощна консумация", regularFont);
            addTableHeader(consumptionTable, "Общо потребление", regularFont);

            // Add table data
            addTableCell(consumptionTable, year, regularFont);
            addTableCell(consumptionTable, usage.getDaytimeUsage() + " kWh", regularFont);
            addTableCell(consumptionTable, usage.getNighttimeUsage() + " kWh", regularFont);
            addTableCell(consumptionTable, (usage.getDaytimeUsage() + usage.getNighttimeUsage()) + " kWh", regularFont);

            document.add(consumptionTable);
            document.add(new Paragraph(" ")); // Empty line for spacing

            // Cost calculation
            document.add(new Paragraph("Изчисление на разходите:", sectionTitleFont));
            document.add(new Paragraph("Дневна консумация: " + usage.getDaytimeUsage() + " kWh × 0,15 лв./kWh = " + String.format("%.2f", daytimeCost) + " лв.", regularFont));
            document.add(new Paragraph("Нощна консумация: " + usage.getNighttimeUsage() + " kWh × 0,05 лв./kWh = " + String.format("%.2f", nighttimeCost) + " лв.", regularFont));
            document.add(new Paragraph("Обща сума за плащане: " + String.format("%.2f", totalCost) + " лв.", new Font(baseFont, 12, Font.BOLD)));
            document.add(new Paragraph(" ")); // Empty line for spacing

            LOGGER.info("PDF bill generated: " + outputFileName);
        } catch (DocumentException | IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to generate PDF bill", e);
        } finally {
            document.close();
        }
    }

    //This method adds a header cell to a PDF table.
    private static void addTableHeader(PdfPTable table, String headerTitle, Font font) {
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
        header.setBorderWidth(2);
        header.setPhrase(new Phrase(headerTitle, font));
        table.addCell(header);
    }

    //This method adds a cell with text to a PDF table.
    private static void addTableCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        table.addCell(cell);
    }

    //This inner class handles the footer of each page in the PDF document
    private static class FooterHandler extends PdfPageEventHelper {
        private Font sectionTitleFont;
        private Font regularFont;

        public FooterHandler(Font sectionTitleFont, Font regularFont) {
            this.sectionTitleFont = sectionTitleFont;
            this.regularFont = regularFont;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            // Add payment method and thank you note
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, new Phrase("Метод на плащане:", sectionTitleFont), 36, 50, 0);
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, new Phrase("Плащането може да се извърши чрез банков превод или на каса.", regularFont), 36, 40, 0);
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, new Phrase("Благодарим Ви, че използвате нашите услуги!", regularFont), 36, 30, 0);

            PdfPTable footer = new PdfPTable(1);
            footer.setTotalWidth(523);
            footer.setLockedWidth(true);
            footer.getDefaultCell().setFixedHeight(20);
            footer.getDefaultCell().setBorder(Rectangle.TOP);
            footer.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            footer.addCell(new Phrase(String.format("Страница %d", writer.getPageNumber())));
            footer.writeSelectedRows(0, -1, 36, 20, writer.getDirectContent());
        }
    }

    */
/*This method saves the bill details to a database.
    The saveBillToDatabase method is responsible for saving the bill details to a database.
     It takes the customer ID, customer name, and usage data as parameters, calculates the costs,
     and inserts the bill information into the bills table in the database.*//*

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
*/
