package org.example.invoiceapp.data;

import org.example.invoiceapp.util.ConfigLoader;

import java.io.*;
import java.nio.file.*;
import java.util.logging.*;

public class FileProcessor {
    private static final Logger LOGGER = Logger.getLogger(FileProcessor.class.getName());
    private static final Path INPUT_DIR = Path.of(ConfigLoader.getProperty("input.data.path"));
    private static final Path DONE_DIR = Paths.get(ConfigLoader.getProperty("done.dir"));
    private static final Path INTERRUPTED_DIR = Paths.get(ConfigLoader.getProperty("error.dir"));

    public static void processFiles() {
        try {
            // Check if both input files exist
            if (Files.exists(INPUT_DIR.resolve("input.txt")) && Files.exists(INPUT_DIR.resolve("lookup.txt"))) {
                // Process the files (input.txt and lookup.txt)
                processFile(INPUT_DIR.resolve("input.txt"));
                processFile(INPUT_DIR.resolve("lookup.txt"));

                // Move files to the "done" directory if everything is fine
                moveFiles(DONE_DIR);
                LOGGER.info("Files processed successfully and moved to 'done' directory.");
            } else {
                LOGGER.warning("One or both input files do not exist. No files to process.");
                System.exit(1);
            }
        } catch (Exception e) {
            // Move files to the "interrupted" directory if an exception occurs
            moveFiles(INTERRUPTED_DIR);
            LOGGER.log(Level.SEVERE, "An error occurred during file processing. Files moved to 'interrupted' directory.", e);
        }
    }

    private static void processFile(Path filePath) throws IOException {
        // Implement your file processing logic here
        // For example, read the file and perform some operations
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Process each line
            }
        }
    }

    private static void moveFiles(Path targetDir) {
        try {
            Files.createDirectories(targetDir);
            Files.move(INPUT_DIR.resolve("input.txt"), targetDir.resolve("input.txt"), StandardCopyOption.REPLACE_EXISTING);
            Files.move(INPUT_DIR.resolve("lookup.txt"), targetDir.resolve("lookup.txt"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to move files", e);
        }
    }

    public static void main(String[] args) {
        processFiles();
    }
}
