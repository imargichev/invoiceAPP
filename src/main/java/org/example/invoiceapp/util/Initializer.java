package org.example.invoiceapp.util;

import org.example.invoiceapp.billing.MonthlyBillGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.*;

public class Initializer {

    private static final String OUTPUT_DIR_PDF = ConfigLoader.getProperty("pdf.output.path");
    private static final String OUTPUT_DIR = ConfigLoader.getProperty("mainDir.output");
    private static final String OUTPUT_DIR_TXT = ConfigLoader.getProperty("txt.output.path");
    private static final String ERROR_FILE = ConfigLoader.getProperty("errorTxt.file.path");
    private static final Logger LOGGER = Logger.getLogger(Initializer.class.getName());
    private static final String DB_URL = ConfigLoader.getProperty("db.url");
    private static final String DB_USER = ConfigLoader.getProperty("db.username");
    private static final String DB_PASSWORD = ConfigLoader.getProperty("db.password");

    public static void initialize() {
        try {
            // Create other necessary directories if they do not exist
            createDirectoryIfNotExists(Paths.get(OUTPUT_DIR));
            createDirectoryIfNotExists(Paths.get(OUTPUT_DIR_TXT));
            createDirectoryIfNotExists(Paths.get(OUTPUT_DIR_PDF));
            createDirectoryIfNotExists(Paths.get(ERROR_FILE));

            // Create database tables
            createDatabaseTables();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to create output directory", e);
        }
        MonthlyBillGenerator.loadCustomerNames();
    }

    private static void createDirectoryIfNotExists(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    private static void createDatabaseTables() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            String createErrorRecordsTable = "CREATE TABLE IF NOT EXISTS error_records (" +
                    "error_id INT AUTO_INCREMENT PRIMARY KEY," +
                    "customer_id VARCHAR(255) NOT NULL," +
                    "reading_date DATE NOT NULL," +
                    "usage1 INT," +
                    "usage2 INT," +
                    "usage3 INT," +
                    "usage4 INT," +
                    "quality VARCHAR(1)," +
                    "error_code INT," +
                    "error_description TEXT)";
            stmt.execute(createErrorRecordsTable);
            LOGGER.info("Database table 'error_records' created or already exists.");

            String createBillsTable = "CREATE TABLE IF NOT EXISTS bills (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "customer_id VARCHAR(255) NOT NULL," +
                    "customer_name VARCHAR(255) NOT NULL," +
                    "daytime_usage INT NOT NULL," +
                    "nighttime_usage INT NOT NULL," +
                    "daytime_cost DOUBLE NOT NULL," +
                    "nighttime_cost DOUBLE NOT NULL)";
            stmt.execute(createBillsTable);
            LOGGER.info("Database table 'bills' created or already exists.");


            String createClientsTable = "CREATE TABLE IF NOT EXISTS clients (" +
                        "client_id VARCHAR(255) PRIMARY KEY," +
                        "client_name VARCHAR(255) NOT NULL)";
                stmt.execute(createClientsTable);
                LOGGER.info("Database table 'clients' created or already exists.");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database table creation failed", e);
        }
    }
}
