package com.automation.tests;

import com.automation.base.BaseTest;
import com.automation.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * LoginTest — verifies the login functionality of automationexercise.com
 *
 * The login page has two forms:
 *   - Left:  "Login to your account"  → email + password
 *   - Right: "New User Signup!"       → name + email
 *
 * VALID TEST CREDENTIALS (pre-registered on the site):
 *   Create a free account at https://automationexercise.com/login → Signup
 *   Then use those credentials below, or use:
 *     email    : test@test.com
 *     password : test@123
 *
 * TESTNG ANNOTATIONS USED:
 *   @Test(priority)     — lower number runs first
 *   @Test(description)  — shown in reports
 *   @Test(groups)       — tag tests as smoke / regression for selective runs
 */
public class LoginTest extends BaseTest {

    // Credentials — in a real project these come from DataProvider or ExcelReader
    private static final String VALID_EMAIL    = "rajatdubey179@gmail.com ";
    private static final String VALID_PASSWORD = "Rajat@123";

    // -----------------------------------------------------------------------
    // POSITIVE TESTS
    // -----------------------------------------------------------------------

    /**
     * TC_001 — Valid credentials should log in and show "Logged in as <name>" in navbar.
     *
     * Verification strategy:
     *   After login the URL stays at / but the navbar changes to show the username.
     *   isLoggedIn() waits for that <b> element to appear.
     */
    @Test(description = "Valid credentials should log in successfully",
          priority = 1, groups = {"smoke", "regression"})
    public void testSuccessfulLogin() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateTo();

        loginPage.login(VALID_EMAIL, VALID_PASSWORD);

        Assert.assertTrue(loginPage.isLoggedIn(),
                "Navbar should show 'Logged in as <user>' after valid login.");

        Assert.assertTrue(loginPage.isLogoutVisible(),
                "Logout link should be visible in the navbar after login.");

        System.out.println("[LoginTest] Logged in as: " + loginPage.getLoggedInUsername());
    }

    // -----------------------------------------------------------------------
    // NEGATIVE TESTS
    // -----------------------------------------------------------------------

    /**
     * TC_002 — Wrong password should show the error message.
     *
     * The site shows: "Your email or password is incorrect!"
     */
    @Test(description = "Wrong password should display an error message",
          priority = 2, groups = {"regression"})
    public void testLoginWithWrongPassword() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateTo();

        loginPage.login(VALID_EMAIL, "wrongpassword123");

        Assert.assertFalse(loginPage.isLoggedIn(),
                "Login should NOT succeed with an incorrect password.");

        String error = loginPage.getLoginErrorMessage();
        Assert.assertFalse(error.isEmpty(),
                "An error message should be displayed for wrong credentials.");

        Assert.assertTrue(error.contains("incorrect"),
                "Error should say 'incorrect'. Actual: " + error);

        System.out.println("[LoginTest] Error shown: " + error);
    }

    /**
     * TC_003 — Non-existent email should show the same incorrect-credentials error.
     *
     * WHY test this separately from wrong password?
     *   Some apps reveal whether an email exists (a security issue).
     *   Both cases should return the SAME generic error message.
     */
    @Test(description = "Non-existent email should display an error message",
          priority = 3, groups = {"regression"})
    public void testLoginWithNonExistentEmail() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateTo();

        loginPage.login("nouser_xyz@fake.com", "anypassword");

        Assert.assertFalse(loginPage.isLoggedIn(),
                "Login should NOT succeed with a non-existent email.");

        String error = loginPage.getLoginErrorMessage();
        Assert.assertFalse(error.isEmpty(),
                "An error message should be shown for a non-existent email.");
    }

    /**
     * TC_004 — Empty credentials should show a validation error.
     */
    @Test(description = "Empty credentials should show a validation error",
          priority = 4, groups = {"regression"})
    public void testLoginWithEmptyCredentials() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateTo();

        loginPage.login("", "");

        Assert.assertFalse(loginPage.isLoggedIn(),
                "Login should NOT succeed with empty credentials.");
    }

    /**
     * TC_005 — Already-registered email in signup form should show duplicate error.
     *
     * The site shows: "Email Address already exist!"
     * This tests the SIGNUP form on the same login page.
     */
    @Test(description = "Signup with existing email should show duplicate error",
          priority = 5, groups = {"regression"})
    public void testSignupWithExistingEmail() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateTo();

        loginPage.initiateSignup("Test User", VALID_EMAIL);

        String error = loginPage.getSignupErrorMessage();
        Assert.assertFalse(error.isEmpty(),
                "An error should appear when signing up with an already registered email.");

        Assert.assertTrue(error.contains("Email Address already exist"),
                "Error should mention 'Email Address already exist'. Actual: " + error);

        System.out.println("[LoginTest] Signup duplicate error: " + error);
    }
}
