package org.example.invoiceapp.billing;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.example.invoiceapp.data.CustomerUsage;
import org.example.invoiceapp.util.ConfigLoader;

import java.io.*;
import java.nio.file.*;
import java.util.logging.*;

/*The PdfBillGenerator class is responsible for generating PDF bills for customers based on their electricity usage.
 It provides a method to generate a PDF bill for a specific customer.*/
public class PdfBillGenerator {
    private static final String OUTPUT_DIR_PDF = ConfigLoader.getProperty("pdf.output.path");
    private static final Logger LOGGER = Logger.getLogger(PdfBillGenerator.class.getName());

    /*Purpose: Generates a PDF bill for a specific customer based on their electricity usage data.
      Parameters: String customerId - the ID of the customer.
                  String customerName - the name of the customer.
                  CustomerUsage usage - the electricity usage data for the customer.
                  String year - the year for which the bill is generated.*/
    public static void generatePdfBill(String customerId, String customerName, CustomerUsage usage, String year) {


        String outputFileName = OUTPUT_DIR_PDF + customerId + ".pdf";
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(outputFileName));
            document.open();

            // Заглавие
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Фактура за електроенергия", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Дата на издаване и номер на фактура
            document.add(new Paragraph("Дата на издаване: 21.11.2024"));
            document.add(new Paragraph("Номер на фактура: INV-379208"));
            document.add(new Paragraph(" ")); // Празен ред

            // Клиент
            Font sectionTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            document.add(new Paragraph("Клиент:", sectionTitleFont));
            document.add(new Paragraph("Име: " + customerName));
            document.add(new Paragraph("Клиентски номер: " + customerId));
            document.add(new Paragraph(" ")); // Празен ред

            // Данни за потребление
            document.add(new Paragraph("Данни за потребление:", sectionTitleFont));
            PdfPTable consumptionTable = new PdfPTable(4);
            consumptionTable.setWidthPercentage(100);
            consumptionTable.addCell("Година");
            consumptionTable.addCell("Дневна консумация");
            consumptionTable.addCell("Нощна консумация");
            consumptionTable.addCell("Общо потребление");
            consumptionTable.addCell(year);
            consumptionTable.addCell(usage.getDaytimeUsage() + " kWh");
            consumptionTable.addCell(usage.getNighttimeUsage() + " kWh");
            consumptionTable.addCell((usage.getDaytimeUsage() + usage.getNighttimeUsage()) + " kWh");
            document.add(consumptionTable);
            document.add(new Paragraph(" ")); // Празен ред

            // Изчисление на разходите
            document.add(new Paragraph("Изчисление на разходите:", sectionTitleFont));
            double daytimeCost = usage.getDaytimeUsage() * 0.15;
            double nighttimeCost = usage.getNighttimeUsage() * 0.05;
            double totalCost = daytimeCost + nighttimeCost;
            document.add(new Paragraph("Дневна консумация: " + usage.getDaytimeUsage() + " kWh × 0,15 лв./kWh = " + String.format("%.2f", daytimeCost) + " лв."));
            document.add(new Paragraph("Нощна консумация: " + usage.getNighttimeUsage() + " kWh × 0,05 лв./kWh = " + String.format("%.2f", nighttimeCost) + " лв."));
            document.add(new Paragraph("Обща сума за плащане: " + String.format("%.2f", totalCost) + " лв.", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            document.add(new Paragraph(" ")); // Празен ред

            // Метод на плащане
            document.add(new Paragraph("Метод на плащане:", sectionTitleFont));
            document.add(new Paragraph("Плащането може да се извърши чрез банков превод или на каса. Моля, упоменете номера на фактурата при извършване на плащането."));
            document.add(new Paragraph(" ")); // Празен ред

            // Благодарност
            document.add(new Paragraph("Благодарим Ви, че използвате нашите услуги!"));

            LOGGER.info("PDF bill generated: " + outputFileName);
        } catch (DocumentException | IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to generate PDF bill", e);
        } finally {
            document.close();
        }
    }
}
