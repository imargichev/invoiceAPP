package org.example.invoiceapp.data;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;
/*The RecordValidator class is responsible for validating records from a file.
 It checks each record for specific criteria and separates valid and invalid records.*/

public class RecordValidator {
    private static final String ERROR_FILE = "src/main/resources/output/error_records/E_records.txt";
    private static final Logger LOGGER = Logger.getLogger(RecordValidator.class.getName());


    // Validates records based on specific criteria and writes invalid records to a separate file.
    public static List<String> validateRecords(String filePath) {
        List<String> validRecords = new ArrayList<>();
        try {
            // Ensure the directory exists
            Path errorFilePath = Paths.get(ERROR_FILE);
            Files.createDirectories(errorFilePath.getParent());

            try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath));
                 BufferedWriter writer = Files.newBufferedWriter(errorFilePath)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] fields = line.split(",");
                    if (fields.length < 7) {
                        writer.write(line);
                        writer.newLine();
                        continue;
                    }
                    String quality = fields[6];
                    int usage1 = Integer.parseInt(fields[2]);
                    int usage2 = Integer.parseInt(fields[3]);
                    int usage3 = Integer.parseInt(fields[4]);
                    int usage4 = Integer.parseInt(fields[5]);

                    if (("A".equals(quality) || "E".equals(quality)) && usage1 > 0 && usage2 > 0 && usage3 > 0 && usage4 > 0) {
                        validRecords.add(line);
                    } else {
                        writer.write(line);
                        writer.newLine();
                    }
                }
                LOGGER.info("Records validated. Invalid records dumped to E_records.txt.");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to validate records", e);
        }
        return validRecords;
    }
}
