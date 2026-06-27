package com.automation.tests;

import com.automation.base.BaseTest;
import com.automation.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * LoginTest — verifies the login functionality of the SauceDemo application.
 *
 * KEY CONCEPTS demonstrated here:
 *   1. Extending BaseTest — inherits setUp() / tearDown() automatically
 *   2. Page Object Model — tests talk to LoginPage, never to raw Selenium APIs
 *   3. TestNG @Test — annotations replace JUnit's @Test; same idea, richer options
 *   4. Assert — TestNG assertions; test fails immediately on a false condition
 *
 * VALID CREDENTIALS for https://www.saucedemo.com:
 *   username : standard_user
 *   password : secret_sauce
 *
 *   Other built-in users:
 *     locked_out_user  — always returns "locked out" error (used in negative test)
 *     problem_user     — logs in but some UI features are broken
 */
public class LoginTest extends BaseTest {

    // -----------------------------------------------------------------------
    // POSITIVE TEST — happy path
    // -----------------------------------------------------------------------

    /**
     * Verifies that a valid user can log in and land on the products page.
     *
     * @Test attributes:
     *   description — shown in TestNG reports; keep it human-readable
     *   priority    — lower number runs first when multiple tests exist
     */
    @Test(description = "Valid credentials should log in successfully", priority = 1)
    public void testSuccessfulLogin() {
        // 1. Arrange — create the page object (driver comes from BaseTest.setUp)
        LoginPage loginPage = new LoginPage(driver);

        // 2. Act — perform the login using the composite method
        loginPage.login("standard_user", "secret_sauce");

        // 3. Assert — verify the expected outcome
        //    Assert.assertTrue(condition, message) — message appears in the report on failure
        Assert.assertTrue(loginPage.isLoginSuccessful(),
                "Expected to be on the inventory page after successful login.");

        System.out.println("[LoginTest] testSuccessfulLogin PASSED.");
    }

    // -----------------------------------------------------------------------
    // NEGATIVE TEST — wrong password
    // -----------------------------------------------------------------------

    /**
     * Verifies that wrong credentials display an error message.
     *
     * WHY negative tests matter in interviews:
     *   Interviewers expect you to test both valid AND invalid scenarios.
     *   Error messages, boundary values, and locked accounts are common examples.
     */
    @Test(description = "Invalid password should show an error message", priority = 2)
    public void testLoginWithInvalidPassword() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("standard_user", "wrong_password");

        // The login should NOT have succeeded
        Assert.assertFalse(loginPage.isLoginSuccessful(),
                "Login should fail with an incorrect password.");

        // The error message should be visible and contain meaningful text
        String error = loginPage.getErrorMessage();
        Assert.assertFalse(error.isEmpty(),
                "An error message should be displayed after failed login.");

        System.out.println("[LoginTest] testLoginWithInvalidPassword PASSED. Error: " + error);
    }

    // -----------------------------------------------------------------------
    // NEGATIVE TEST — locked-out user
    // -----------------------------------------------------------------------

    /**
     * Verifies the error message for a locked-out account.
     *
     * Assert.assertTrue + String.contains() is a common pattern to check
     * that a message CONTAINS expected text without asserting the full string.
     */
    @Test(description = "Locked-out user should see a specific error message", priority = 3)
    public void testLockedOutUserLogin() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("locked_out_user", "secret_sauce");

        String error = loginPage.getErrorMessage();

        Assert.assertTrue(error.contains("locked out"),
                "Expected 'locked out' in error message but got: " + error);

        System.out.println("[LoginTest] testLockedOutUserLogin PASSED. Error: " + error);
    }

    // -----------------------------------------------------------------------
    // NEGATIVE TEST — empty credentials
    // -----------------------------------------------------------------------

    /**
     * Verifies that submitting an empty form shows a validation error.
     * This tests the boundary: what happens with no input at all.
     */
    @Test(description = "Empty credentials should show a validation error", priority = 4)
    public void testLoginWithEmptyCredentials() {
        LoginPage loginPage = new LoginPage(driver);

        // Login with empty strings — no input at all
        loginPage.login("", "");

        String error = loginPage.getErrorMessage();

        Assert.assertFalse(error.isEmpty(),
                "A validation error should appear when credentials are empty.");

        System.out.println("[LoginTest] testLoginWithEmptyCredentials PASSED. Error: " + error);
    }
}
