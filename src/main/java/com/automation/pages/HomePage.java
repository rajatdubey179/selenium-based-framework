package com.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * HomePage — Page Object for https://automationexercise.com
 *
 * Covers:
 *   - Top navigation bar (Products, Cart, Login, etc.)
 *   - Featured product cards (each has "Add to cart" + "View Product")
 *   - Category sidebar (Women / Men / Kids)
 *   - Brand sidebar (Polo, H&M, Madame, etc.)
 *   - Footer subscription form
 *
 * SKILLS DEMONSTRATED:
 *   - CSS child / descendant selectors
 *   - XPath with text() for nav links
 *   - WebDriverWait with ExpectedConditions
 *   - List<WebElement> for iterating multiple similar elements
 */
public class HomePage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // -------------------------------------------------------------------------
    // Navigation bar locators
    // -------------------------------------------------------------------------

    // CSS: <a> tag whose href ends with '/products' — nth-child not needed
    private final By navProducts    = By.cssSelector("a[href='/products']");
    private final By navCart        = By.cssSelector("a[href='/view_cart']");
    private final By navLogin       = By.cssSelector("a[href='/login']");
    private final By navContactUs   = By.cssSelector("a[href='/contact_us']");
    private final By navTestCases   = By.cssSelector("a[href='/test_cases']");

    // CSS: <b> inside navbar anchor — shows username when logged in
    private final By loggedInUser   = By.cssSelector("li a b");
    private final By logoutLink     = By.cssSelector("a[href='/logout']");
    private final By deleteAccount  = By.cssSelector("a[href='/delete_account']");

    // -------------------------------------------------------------------------
    // Home page body locators
    // -------------------------------------------------------------------------

    // CSS: the main slider / hero section heading
    private final By heroHeading = By.cssSelector("#slider-carousel");

    // CSS: all product cards on the page — each card wraps in .product-image-wrapper
    private final By allProductCards = By.cssSelector(".product-image-wrapper");

    // CSS: "Features Items" section heading
    private final By featuredItemsHeading = By.cssSelector(".features_items h2");

    // -------------------------------------------------------------------------
    // Category sidebar locators
    // -------------------------------------------------------------------------

    // CSS: category panel heading
    private final By categoryPanel = By.cssSelector(".left-sidebar h2");

    // XPath: find the Women category link by its visible text
    // CSS alone cannot match by visible text — XPath text() is needed here
    private final By womenCategory = By.xpath("//a[contains(text(),'Women')][@href='#Women']");
    private final By menCategory   = By.xpath("//a[contains(text(),'Men')][@href='#Men']");
    private final By kidsCategory  = By.xpath("//a[contains(text(),'Kids')][@href='#Kids']");

    // -------------------------------------------------------------------------
    // Brand sidebar locators
    // -------------------------------------------------------------------------

    // CSS: all brand links are inside .brands-name ul li a
    private final By allBrandLinks = By.cssSelector(".brands-name ul li a");

    // CSS: specific brand by href — generated from the brand name
    private final By poloBrand = By.cssSelector("a[href='/brand_products/Polo']");
    private final By hmBrand   = By.cssSelector("a[href='/brand_products/H&M']");

    // -------------------------------------------------------------------------
    // Footer locators
    // -------------------------------------------------------------------------

    private final By subscriptionEmailField = By.cssSelector("#susbscribe_email");
    private final By subscribeButton        = By.cssSelector("#subscribe");

    // Success message after subscribing
    // XPath: <div> with id containing the word 'success' is not present here;
    // the site shows an alert-style <div> — matched by its id
    private final By subscribeSuccessAlert = By.cssSelector("#success-subscribe");

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // -------------------------------------------------------------------------
    // Navigation actions
    // -------------------------------------------------------------------------

    public void clickProducts() {
        wait.until(ExpectedConditions.elementToBeClickable(navProducts)).click();
    }

    public void clickCart() {
        wait.until(ExpectedConditions.elementToBeClickable(navCart)).click();
    }

    public void clickSignupLogin() {
        wait.until(ExpectedConditions.elementToBeClickable(navLogin)).click();
    }

    public void clickContactUs() {
        wait.until(ExpectedConditions.elementToBeClickable(navContactUs)).click();
    }

    public void clickLogout() {
        wait.until(ExpectedConditions.elementToBeClickable(logoutLink)).click();
    }

    public void clickDeleteAccount() {
        wait.until(ExpectedConditions.elementToBeClickable(deleteAccount)).click();
    }

    // -------------------------------------------------------------------------
    // Page state queries
    // -------------------------------------------------------------------------

    /** Returns true when the home page is fully loaded (hero section visible). */
    public boolean isHomePageVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(featuredItemsHeading));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Returns true if the navbar shows "Logged in as <user>". */
    public boolean isLoggedIn() {
        try {
            return driver.findElement(loggedInUser).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /** Returns the logged-in username from the navbar. */
    public String getLoggedInUsername() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(loggedInUser)).getText();
    }

    /**
     * Returns the count of product cards visible on the home page.
     * Demonstrates List<WebElement> — finding multiple elements at once.
     */
    public int getProductCardCount() {
        List<WebElement> cards = driver.findElements(allProductCards);
        return cards.size();
    }

    // -------------------------------------------------------------------------
    // Category sidebar actions
    // -------------------------------------------------------------------------

    public void clickWomenCategory() {
        wait.until(ExpectedConditions.elementToBeClickable(womenCategory)).click();
    }

    public void clickMenCategory() {
        wait.until(ExpectedConditions.elementToBeClickable(menCategory)).click();
    }

    public void clickKidsCategory() {
        wait.until(ExpectedConditions.elementToBeClickable(kidsCategory)).click();
    }

    // -------------------------------------------------------------------------
    // Brand sidebar actions
    // -------------------------------------------------------------------------

    public void clickPoloBrand() {
        wait.until(ExpectedConditions.elementToBeClickable(poloBrand)).click();
    }

    public void clickHMBrand() {
        wait.until(ExpectedConditions.elementToBeClickable(hmBrand)).click();
    }

    /**
     * Returns all brand names listed in the sidebar.
     * Demonstrates iterating a List<WebElement> and extracting text.
     */
    public List<String> getAllBrandNames() {
        List<WebElement> brandLinks = driver.findElements(allBrandLinks);
        return brandLinks.stream()
                .map(WebElement::getText)
                .toList();
    }

    // -------------------------------------------------------------------------
    // Footer subscription
    // -------------------------------------------------------------------------

    /**
     * Subscribes with the given email using the footer form.
     * Demonstrates scrolling via JavascriptExecutor (the subscribe button
     * is at the bottom of the page and may not be in the viewport).
     */
    public void subscribeWithEmail(String email) {
        WebElement emailBox = wait.until(
                ExpectedConditions.visibilityOfElementLocated(subscriptionEmailField));
        emailBox.sendKeys(email);
        wait.until(ExpectedConditions.elementToBeClickable(subscribeButton)).click();
    }

    /** Returns true if the subscription success message is shown. */
    public boolean isSubscriptionSuccessful() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(subscribeSuccessAlert));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
