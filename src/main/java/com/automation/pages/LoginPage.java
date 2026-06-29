package com.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * LoginPage — Page Object for https://automationexercise.com/login
 *
 * The page has TWO forms side by side:
 *   Left  → "Login to your account"  (email + password + Login button)
 *   Right → "New User Signup!"        (name + email + Signup button)
 *
 * LOCATOR STRATEGY:
 *   The site uses data-qa attributes on form elements — the most stable
 *   selector type because it is decoupled from CSS styling and layout.
 *   We use [data-qa='value'] CSS attribute selectors throughout.
 *
 * CSS SELECTOR reference used here:
 *   [data-qa='value']    → attribute selector — best for test automation
 *   .classname           → class selector
 *   a[href='/logout']    → anchor with specific href
 *   li a b               → <b> inside <a> inside <li> (nav username display)
 */
public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // -------------------------------------------------------------------------
    // Locators — LOGIN form (left panel)
    // -------------------------------------------------------------------------

    // data-qa attributes are purpose-built for automation — prefer over id/class
    private final By loginEmailField    = By.cssSelector("[data-qa='login-email']");
    private final By loginPasswordField = By.cssSelector("[data-qa='login-password']");
    private final By loginButton        = By.cssSelector("[data-qa='login-button']");

    // -------------------------------------------------------------------------
    // Locators — SIGNUP form (right panel)
    // -------------------------------------------------------------------------

    private final By signupNameField  = By.cssSelector("[data-qa='signup-name']");
    private final By signupEmailField = By.cssSelector("[data-qa='signup-email']");
    private final By signupButton     = By.cssSelector("[data-qa='signup-button']");

    // -------------------------------------------------------------------------
    // Locators — post-action verification
    // -------------------------------------------------------------------------

    // XPath: contains() on text — CSS cannot match visible text content directly.
    // Used here because the error <p> has no id or data-qa attribute.
    private final By loginErrorMessage = By.xpath(
            "//p[contains(text(),'Your email or password is incorrect')]");

    // XPath: same reason — no stable attribute on this error element
    private final By signupErrorMessage = By.xpath(
            "//p[contains(text(),'Email Address already exist')]");

    // CSS: navbar shows "Logged in as <b>username</b>" after successful login
    private final By loggedInUserText = By.cssSelector("li a b");

    // CSS: logout link is only present in the navbar when a user is logged in
    private final By logoutLink = By.cssSelector("a[href='/logout']");

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        // WebDriverWait polls the DOM up to 10s before throwing TimeoutException.
        // Always prefer this over Thread.sleep() — it makes tests faster and stable.
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // -------------------------------------------------------------------------
    // Navigation
    // -------------------------------------------------------------------------

    /** Navigates directly to the login page URL. */
    public void navigateTo() {
        driver.get("https://automationexercise.com/login");
    }

    // -------------------------------------------------------------------------
    // LOGIN form actions
    // -------------------------------------------------------------------------

    public void enterLoginEmail(String email) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(loginEmailField));
        field.clear();
        field.sendKeys(email);
    }

    public void enterLoginPassword(String password) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(loginPasswordField));
        field.clear();
        field.sendKeys(password);
    }

    public void clickLoginButton() {
        wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();
    }

    /** Composite login — fills email + password then clicks Login. */
    public void login(String email, String password) {
        enterLoginEmail(email);
        enterLoginPassword(password);
        clickLoginButton();
    }

    // -------------------------------------------------------------------------
    // SIGNUP form actions
    // -------------------------------------------------------------------------

    public void enterSignupName(String name) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(signupNameField));
        field.clear();
        field.sendKeys(name);
    }

    public void enterSignupEmail(String email) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(signupEmailField));
        field.clear();
        field.sendKeys(email);
    }

    public void clickSignupButton() {
        wait.until(ExpectedConditions.elementToBeClickable(signupButton)).click();
    }

    /** Composite signup — fills name + email then clicks Signup. */
    public void initiateSignup(String name, String email) {
        enterSignupName(name);
        enterSignupEmail(email);
        clickSignupButton();
    }

    // -------------------------------------------------------------------------
    // State queries — used by tests for assertions
    // -------------------------------------------------------------------------

    /** Returns true when the navbar shows "Logged in as <user>". */
    public boolean isLoggedIn() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(loggedInUserText));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Returns the username text shown in the navbar after login. */
    public String getLoggedInUsername() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(loggedInUserText)).getText();
    }

    /** Returns the login error message text, or empty string if not shown. */
    public String getLoginErrorMessage() {
        try {
            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(loginErrorMessage)).getText();
        } catch (Exception e) {
            return "";
        }
    }

    /** Returns the signup error message text, or empty string if not shown. */
    public String getSignupErrorMessage() {
        try {
            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(signupErrorMessage)).getText();
        } catch (Exception e) {
            return "";
        }
    }

    /** Returns true if the logout link is visible — secondary login confirmation. */
    public boolean isLogoutVisible() {
        try {
            return driver.findElement(logoutLink).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /** Returns the current page title. */
    public String getPageTitle() {
        return driver.getTitle();
    }
}
