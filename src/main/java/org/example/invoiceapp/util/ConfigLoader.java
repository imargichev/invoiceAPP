package org.example.invoiceapp.util;

import java.io.*;
import java.util.*;
import java.util.logging.*;

public class ConfigLoader {

    private static final Logger LOGGER = Logger.getLogger(ConfigLoader.class.getName());
    private static Properties properties = new Properties();

    static {
        try (InputStream input = new FileInputStream("src/main/resources/application.properties")) {
            // Load the properties from the file
            properties.load(input);
            LOGGER.info("Application properties loaded successfully.");
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Failed to load application properties", ex);
        }
    }

    // Utility method to get property values
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
// Compare this snippet from src/main/resources/application.properties:
