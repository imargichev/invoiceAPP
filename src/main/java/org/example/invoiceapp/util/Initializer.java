package org.example.invoiceapp.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.*;

public class Initializer {

    private static final String OUTPUT_DIR_PDF = ConfigLoader.getProperty("pdf.output.path");
    private static final String OUTPUT_DIR = ConfigLoader.getProperty("mainDir.output");
    private static final String OUTPUT_DIR_TXT = ConfigLoader.getProperty("txt.output.path");
    private static final String ERROR_FILE = ConfigLoader.getProperty("errorTxt.file.path");
    private static final Logger LOGGER = Logger.getLogger(Initializer.class.getName());

    public static void initialize() {
        try {
            // Create other necessary directories if they do not exist
            createDirectoryIfNotExists(Paths.get(OUTPUT_DIR));
            createDirectoryIfNotExists(Paths.get(OUTPUT_DIR_TXT));
            createDirectoryIfNotExists(Paths.get(OUTPUT_DIR_PDF));
            createDirectoryIfNotExists(Paths.get(ERROR_FILE));

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to create output directory", e);
        }
    }

    private static void createDirectoryIfNotExists(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }
}
