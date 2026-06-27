package com.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * LoginPage — Page Object Model (POM) for the login screen.
 *
 * WHY Page Object Model?
 *   Without POM, every test that clicks the login button duplicates the locator.
 *   When the button's ID changes, you update dozens of tests.
 *   With POM, you update ONE line here and all tests heal automatically.
 *
 * RULE: A Page Object contains locators and actions — NEVER assertions.
 *   Assertions belong in the test class so failures point to the right test.
 *
 * TARGET SITE: https://www.saucedemo.com (free public practice site)
 */
public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // -------------------------------------------------------------------------
    // Locators — By objects stored as constants
    //
    // CSS SELECTOR cheat-sheet used below:
    //   #id              → element with that id
    //   .class           → element with that class
    //   tag              → element by tag name
    //   [attr='value']   → element with that attribute value
    //   tag[attr]        → tag that has the attribute (regardless of value)
    //
    // PREFER CSS over XPath — CSS is faster and easier to read.
    // Use XPath only when CSS cannot express the relationship (e.g. parent lookup,
    // text-based matching without an id/class, or deeply nested shadow DOM).
    // -------------------------------------------------------------------------

    // CSS: select <input> element whose 'id' attribute equals 'user-name'
    private final By usernameField = By.cssSelector("#user-name");

    // CSS: attribute selector — works even if the element has no id
    private final By passwordField = By.cssSelector("input[type='password']");

    // CSS: <input> with id='login-button'
    private final By loginButton = By.cssSelector("#login-button");

    // XPath example: text() matching — CSS cannot select by visible text alone
    // This finds any element whose exact text is "Epic sadface..."
    private final By errorMessage = By.xpath(
            "//*[contains(@class,'error-message-container')]//h3");

    // CSS: the inventory page container — present only after a successful login
    private final By inventoryContainer = By.cssSelector(".inventory_container");

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Every Page Object receives the WebDriver from the test/BaseTest.
     * The page never creates its own driver — this keeps control in one place.
     *
     * @param driver the active WebDriver session
     */
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        // WebDriverWait replaces Thread.sleep() — it polls until a condition is true
        // or the timeout expires, making tests faster and less flaky.
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // -------------------------------------------------------------------------
    // Actions — each method represents ONE user interaction
    // -------------------------------------------------------------------------

    /**
     * Types text into the username input.
     * wait.until ensures the field is clickable before we interact with it.
     */
    public void enterUsername(String username) {
        WebElement field = wait.until(ExpectedConditions.elementToBeClickable(usernameField));
        field.clear();          // clear any pre-filled value first
        field.sendKeys(username);
    }

    /**
     * Types text into the password input.
     */
    public void enterPassword(String password) {
        WebElement field = wait.until(ExpectedConditions.elementToBeClickable(passwordField));
        field.clear();
        field.sendKeys(password);
    }

    /**
     * Clicks the login button.
     */
    public void clickLoginButton() {
        wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();
    }

    // -------------------------------------------------------------------------
    // Composite action — combines individual steps into one reusable method
    // -------------------------------------------------------------------------

    /**
     * Performs a full login in one call.
     * Tests use this instead of calling the three individual steps every time.
     *
     * @param username credential to enter
     * @param password credential to enter
     */
    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
    }

    // -------------------------------------------------------------------------
    // State queries — boolean / String methods tests use in assertions
    // -------------------------------------------------------------------------

    /**
     * Returns true if the product inventory page loaded after login.
     * ExpectedConditions.urlContains is a common way to verify navigation.
     */
    public boolean isLoginSuccessful() {
        try {
            // Wait up to 5 seconds for the inventory container to appear
            wait.until(ExpectedConditions.visibilityOfElementLocated(inventoryContainer));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns the error message text shown on a failed login attempt.
     * Returns an empty string if no error message is present.
     */
    public String getErrorMessage() {
        try {
            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(errorMessage)).getText();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Returns the current page title — useful for a quick sanity assertion.
     */
    public String getPageTitle() {
        return driver.getTitle();
    }
}
