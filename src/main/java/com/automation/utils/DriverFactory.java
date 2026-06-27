package com.automation.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.time.Duration;

/**
 * DriverFactory — the single place that creates and destroys WebDriver instances.
 *
 * WHY centralise driver creation?
 *   If Chrome upgrades overnight and the driver path changes, you fix it here
 *   and every test automatically benefits.  Tests never call "new ChromeDriver()"
 *   directly — they ask the factory.
 *
 * THREAD SAFETY NOTE (for later learning):
 *   ThreadLocal<WebDriver> would make this safe for parallel execution.
 *   We skip that here to keep things simple — add it when you learn parallel TestNG.
 */
public class DriverFactory {

    // Holds the driver for the current test thread
    private static WebDriver driver;

    /**
     * Creates a WebDriver for the browser specified in config.properties.
     * WebDriverManager auto-downloads the matching browser driver binary —
     * no manual chromedriver.exe setup required.
     *
     * @return a fresh, configured WebDriver instance
     */
    public static WebDriver initDriver() {
        String browser = ConfigReader.getBrowser().toLowerCase();
        int implicitWait = ConfigReader.getImplicitWait();

        switch (browser) {

            case "chrome" -> {
                // WebDriverManager checks the installed Chrome version and downloads
                // the matching ChromeDriver binary automatically.
                WebDriverManager.chromedriver().setup();

                ChromeOptions options = new ChromeOptions();
                // --start-maximized opens the window full screen — avoids element
                // visibility issues caused by a small default window.
                options.addArguments("--start-maximized");

                driver = new ChromeDriver(options);
            }

            case "firefox" -> {
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
            }

            case "edge" -> {
                WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver();
            }

            default -> throw new RuntimeException(
                    "[DriverFactory] Unsupported browser: '" + browser
                    + "'. Use chrome, firefox, or edge in config.properties.");
        }

        // Implicit wait: Selenium waits up to N seconds when it can't find an element
        // before throwing NoSuchElementException.
        // Use explicit waits (WebDriverWait + ExpectedConditions) for more control.
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));

        System.out.println("[DriverFactory] Browser launched: " + browser);
        return driver;
    }

    /**
     * Returns the current WebDriver instance.
     * Call this only after initDriver() has been called.
     */
    public static WebDriver getDriver() {
        if (driver == null) {
            throw new RuntimeException("[DriverFactory] Driver not initialised. "
                    + "Call initDriver() first.");
        }
        return driver;
    }

    /**
     * Closes all browser windows and ends the WebDriver session.
     * Always call this in @AfterMethod / @AfterClass to prevent browser leaks.
     */
    public static void quitDriver() {
        if (driver != null) {
            driver.quit();  // quit() closes ALL windows; close() closes only the current one
            driver = null;
            System.out.println("[DriverFactory] Browser closed.");
        }
    }
}
