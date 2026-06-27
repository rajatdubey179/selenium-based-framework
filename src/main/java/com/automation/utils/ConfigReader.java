package com.automation.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * ConfigReader — reads key/value pairs from config.properties.
 *
 * WHY a separate class for this?
 *   Hard-coding URLs or browser names inside tests makes them brittle.
 *   A properties file lets you change any setting without touching Java code,
 *   and this reader gives every class a single, safe way to access those settings.
 *
 * PATTERN: Utility class with only static methods — no need to instantiate it.
 */
public class ConfigReader {

    // Properties object holds all key=value pairs loaded from the file
    private static Properties properties;

    // static block runs once when the class is first loaded by the JVM
    static {
        loadProperties();
    }

    /**
     * Reads config.properties from the classpath (src/main/resources).
     * Using FileInputStream so we can handle the checked IOException explicitly.
     */
    private static void loadProperties() {
        properties = new Properties();

        // getResourceAsStream looks inside the compiled classpath — works in Maven
        // without needing an absolute file path.
        try (FileInputStream fis = new FileInputStream(
                "src/main/resources/config.properties")) {

            properties.load(fis);
            System.out.println("[ConfigReader] config.properties loaded successfully.");

        } catch (IOException e) {
            // Wrap in RuntimeException so callers don't need try/catch everywhere
            throw new RuntimeException("[ConfigReader] Failed to load config.properties: "
                    + e.getMessage(), e);
        }
    }

    /**
     * Returns the value for the given key.
     * Throws if the key is missing — fail fast is better than a silent null.
     */
    public static String get(String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new RuntimeException("[ConfigReader] Key '" + key
                    + "' not found in config.properties");
        }
        return value.trim();
    }

    // ---- Convenience getters ------------------------------------------------

    public static String getBaseUrl() {
        return get("baseUrl");
    }

    public static String getBrowser() {
        return get("browser");
    }

    /**
     * Returns implicitWait as an int.
     * parseInt will throw NumberFormatException if the value is not a number —
     * which is exactly what we want: catch config mistakes early.
     */
    public static int getImplicitWait() {
        return Integer.parseInt(get("implicitWait"));
    }
}
