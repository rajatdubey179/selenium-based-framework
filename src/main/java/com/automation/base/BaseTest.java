package com.automation.base;

import com.automation.utils.ConfigReader;
import com.automation.utils.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * BaseTest — the parent class every test class extends.
 *
 * WHY a base class?
 *   Every test needs a browser, a URL, and a cleanup step.
 *   Putting that logic here means each test class gets it for free via inheritance
 *   and we never duplicate setup/teardown code.
 *
 * TESTNG LIFECYCLE (order of execution):
 *   @BeforeSuite → @BeforeClass → @BeforeMethod → @Test → @AfterMethod → @AfterClass → @AfterSuite
 *
 *   We use @BeforeMethod so each @Test gets a fresh browser session.
 *   Use @BeforeClass if you want one session shared across all tests in a class.
 */
public class BaseTest {

    // 'protected' — subclasses (test classes) can access 'driver' directly
    protected WebDriver driver;

    /**
     * Runs before EVERY @Test method.
     * 1. Creates a new WebDriver via DriverFactory
     * 2. Navigates to the baseUrl from config.properties
     */
    @BeforeMethod
    public void setUp() {
        // Delegate driver creation to the factory — BaseTest doesn't care which browser
        driver = DriverFactory.initDriver();

        String baseUrl = ConfigReader.getBaseUrl();
        driver.get(baseUrl);  // navigate() vs get() — both navigate; get() waits for page load

        System.out.println("[BaseTest] Navigated to: " + baseUrl);
    }

    /**
     * Runs after EVERY @Test method — even if the test fails.
     * Always quit the driver to free OS resources and avoid zombie browser processes.
     */
    @AfterMethod
    public void tearDown() {
        DriverFactory.quitDriver();
        System.out.println("[BaseTest] Tear-down complete.");
    }
}
