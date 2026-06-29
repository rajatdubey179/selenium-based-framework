package com.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * ProductsPage — Page Object for https://automationexercise.com/products
 *
 * Covers:
 *   - Search bar (search by product name)
 *   - Product list (all products, individual cards)
 *   - "Add to cart" overlay button on each card (hover-triggered)
 *   - "View Product" link per card
 *   - Modal dialog: "Added!" confirmation after adding to cart
 *
 * SKILLS DEMONSTRATED:
 *   - Hover with Actions class (mouseover to reveal "Add to cart")
 *   - JavascriptExecutor scroll to bring elements into view
 *   - nth-child / nth-of-type CSS selectors
 *   - XPath with position() for indexed element selection
 *   - Handling a modal overlay
 *   - List<WebElement> iteration for product names
 */
public class ProductsPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final Actions actions;
    private final JavascriptExecutor js;

    // -------------------------------------------------------------------------
    // Locators — search
    // -------------------------------------------------------------------------

    private final By searchInput   = By.cssSelector("#search_product");
    private final By searchButton  = By.cssSelector("#submit_search");

    // CSS: all product name <p> tags inside search results
    private final By searchResultNames = By.cssSelector(".productinfo p");

    // -------------------------------------------------------------------------
    // Locators — product list
    // -------------------------------------------------------------------------

    // CSS: every product card wrapper on the page
    private final By allProductCards = By.cssSelector(".product-image-wrapper");

    // CSS: "View Product" links — one per card
    private final By viewProductLinks = By.cssSelector("a[href*='/product_details/']");

    // CSS: "Add to cart" button inside .productinfo overlay
    private final By addToCartButtons = By.cssSelector(".productinfo a.add-to-cart");

    // -------------------------------------------------------------------------
    // Locators — "Added to Cart" modal
    // -------------------------------------------------------------------------

    // CSS: the modal container shown after adding a product
    private final By addedToCartModal       = By.cssSelector("#cartModal");
    private final By continueShoppingButton = By.cssSelector("#cartModal button");
    private final By viewCartLinkInModal    = By.cssSelector("#cartModal a[href='/view_cart']");

    // -------------------------------------------------------------------------
    // Locators — page heading (used to confirm we're on the right page)
    // -------------------------------------------------------------------------

    private final By allProductsHeading = By.cssSelector(".features_items h2");

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public ProductsPage(WebDriver driver) {
        this.driver  = driver;
        this.wait    = new WebDriverWait(driver, Duration.ofSeconds(10));
        // Actions class handles complex mouse/keyboard interactions
        this.actions = new Actions(driver);
        // JavascriptExecutor runs JS in the browser context — used for scroll
        this.js      = (JavascriptExecutor) driver;
    }

    // -------------------------------------------------------------------------
    // Navigation
    // -------------------------------------------------------------------------

    public void navigateTo() {
        driver.get("https://automationexercise.com/products");
    }

    // -------------------------------------------------------------------------
    // Page state
    // -------------------------------------------------------------------------

    /** Returns true if the "All Products" heading is visible. */
    public boolean isOnProductsPage() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(allProductsHeading));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Returns the total count of product cards on the current page. */
    public int getProductCount() {
        return driver.findElements(allProductCards).size();
    }

    // -------------------------------------------------------------------------
    // Search actions
    // -------------------------------------------------------------------------

    /**
     * Searches for a product by name.
     * Clears the field first to avoid appending to a previous search.
     */
    public void searchProduct(String productName) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(searchInput));
        input.clear();
        input.sendKeys(productName);
        wait.until(ExpectedConditions.elementToBeClickable(searchButton)).click();
    }

    /**
     * Returns names of all products shown in search results.
     * Demonstrates List<WebElement> → stream → text extraction.
     */
    public List<String> getSearchResultProductNames() {
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(searchResultNames));
        return driver.findElements(searchResultNames)
                .stream()
                .map(WebElement::getText)
                .toList();
    }

    // -------------------------------------------------------------------------
    // Product card actions
    // -------------------------------------------------------------------------

    /**
     * Hovers over the first product card and clicks "Add to cart".
     *
     * WHY hover?
     *   The "Add to cart" button is hidden inside an overlay (.product-overlay)
     *   that only appears on mouseover. Directly clicking the hidden element
     *   throws ElementNotInteractableException.
     *   Actions.moveToElement() triggers the CSS :hover state, making it visible.
     */
    public void addFirstProductToCart() {
        List<WebElement> cards = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(allProductCards));

        WebElement firstCard = cards.get(0);

        // Scroll the card into view — required if the element is below the fold
        js.executeScript("arguments[0].scrollIntoView(true);", firstCard);

        // Hover over the card to reveal the overlay buttons
        actions.moveToElement(firstCard).perform();

        // Now the "Add to cart" button inside this card is interactable
        // XPath: find the add-to-cart link that is a descendant of this specific card
        WebElement addToCartBtn = firstCard.findElement(By.cssSelector(".productinfo a.add-to-cart"));
        wait.until(ExpectedConditions.elementToBeClickable(addToCartBtn)).click();
    }

    /**
     * Adds the product at the given 1-based index to the cart.
     * Index 1 = first product, Index 2 = second product, etc.
     *
     * @param index 1-based position in the product list
     */
    public void addProductToCartByIndex(int index) {
        List<WebElement> cards = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(allProductCards));

        if (index < 1 || index > cards.size()) {
            throw new IllegalArgumentException(
                    "Index " + index + " out of range. Total products: " + cards.size());
        }

        WebElement card = cards.get(index - 1);
        js.executeScript("arguments[0].scrollIntoView(true);", card);
        actions.moveToElement(card).perform();

        WebElement btn = card.findElement(By.cssSelector(".productinfo a.add-to-cart"));
        wait.until(ExpectedConditions.elementToBeClickable(btn)).click();
    }

    /**
     * Clicks "View Product" for the product at the given 1-based index.
     * Navigates to the product detail page.
     */
    public void viewProductByIndex(int index) {
        List<WebElement> links = driver.findElements(viewProductLinks);
        WebElement link = links.get(index - 1);
        js.executeScript("arguments[0].scrollIntoView(true);", link);
        link.click();
    }

    // -------------------------------------------------------------------------
    // Modal actions (shown after "Add to cart")
    // -------------------------------------------------------------------------

    /** Returns true if the "Added to Cart" modal is visible. */
    public boolean isAddedToCartModalVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(addedToCartModal));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** Clicks "Continue Shopping" in the modal to dismiss it. */
    public void continueShopping() {
        wait.until(ExpectedConditions.elementToBeClickable(continueShoppingButton)).click();
    }

    /** Clicks "View Cart" in the modal to go to the cart page. */
    public void goToCartFromModal() {
        wait.until(ExpectedConditions.elementToBeClickable(viewCartLinkInModal)).click();
    }
}
