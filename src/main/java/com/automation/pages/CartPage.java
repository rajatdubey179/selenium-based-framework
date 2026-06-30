package com.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * CartPage — Page Object for https://automationexercise.com/view_cart
 *
 * CART TABLE STRUCTURE (verified from live DOM):
 *   <table id="cart_info_table">
 *     <thead> — header: Item | Description | Price | Quantity | Total | (delete col)
 *     <tbody> — one <tr> per cart item:
 *       td.cart_product     → product image
 *       td.cart_description → product name (h4 > a) + category (p)
 *       td.cart_price       → unit price (p)
 *       td.cart_quantity    → quantity (button whose text = qty value)
 *       td.cart_total       → line total (p)
 *       td.cart_delete      → delete link (a.cart_quantity_delete)
 *
 * SKILLS DEMONSTRATED:
 *   - CSS table cell scoping: #cart_info_table td.cart_description h4 a
 *   - List<WebElement> iteration for multi-row data extraction
 *   - Paired list traversal (names + delete buttons at same index)
 *   - Row-scoped child element lookup (row.findElement)
 *   - Conditional state check (empty cart vs populated cart)
 */
public class CartPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // -------------------------------------------------------------------------
    // Locators — page identification
    // -------------------------------------------------------------------------

    // CSS: last breadcrumb item reads "Shopping Cart" when on this page
    private final By shoppingCartBreadcrumb = By.cssSelector(".breadcrumb li:last-child");

    // CSS: shown only when cart has no items
    private final By emptyCartMessage = By.cssSelector("#empty_cart");

    // CSS: "Proceed To Checkout" button — only visible when cart has items
    private final By proceedToCheckoutBtn = By.cssSelector(".btn.btn-default.check_out");

    // -------------------------------------------------------------------------
    // Locators — cart table rows
    // -------------------------------------------------------------------------

    // CSS: every product row inside the cart table body
    private final By cartRows = By.cssSelector("#cart_info_table tbody tr");

    // ---- Per-row child locators (used with row.findElement) -----------------

    // CSS: product name link inside a row
    private final By productNameInRow = By.cssSelector("td.cart_description h4 a");

    // CSS: unit price paragraph inside a row
    private final By productPriceInRow = By.cssSelector("td.cart_price p");

    // CSS: quantity button — its getText() gives the quantity value
    private final By productQuantityInRow = By.cssSelector("td.cart_quantity button");

    // CSS: line total paragraph inside a row
    private final By productTotalInRow = By.cssSelector("td.cart_total p");

    // CSS: delete (X) anchor inside a row
    private final By deleteButtonInRow = By.cssSelector("td.cart_delete a.cart_quantity_delete");

    // ---- Full-table locators (for bulk List extraction) ---------------------

    private final By allProductNames      = By.cssSelector("#cart_info_table td.cart_description h4 a");
    private final By allProductPrices     = By.cssSelector("#cart_info_table td.cart_price p");
    private final By allProductQuantities = By.cssSelector("#cart_info_table td.cart_quantity button");
    private final By allProductTotals     = By.cssSelector("#cart_info_table td.cart_total p");
    private final By allDeleteButtons     = By.cssSelector("#cart_info_table td.cart_delete a");

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // -------------------------------------------------------------------------
    // Navigation
    // -------------------------------------------------------------------------

    public void navigateTo() {
        driver.get("https://automationexercise.com/view_cart");
    }

    // -------------------------------------------------------------------------
    // Page state queries
    // -------------------------------------------------------------------------

    /**
     * Returns true if the "Shopping Cart" breadcrumb is visible.
     * Use this to confirm navigation landed on the cart page.
     */
    public boolean isOnCartPage() {
        try {
            WebElement crumb = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(shoppingCartBreadcrumb));
            return crumb.getText().contains("Shopping Cart");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns true when the cart contains NO items.
     * The site renders a "Cart is empty!" paragraph with id="empty_cart".
     */
    public boolean isCartEmpty() {
        try {
            return driver.findElement(emptyCartMessage).isDisplayed();
        } catch (Exception e) {
            // Element absent means cart has items
            return false;
        }
    }

    /**
     * Returns the number of distinct product rows in the cart.
     */
    public int getCartItemCount() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(cartRows));
            return driver.findElements(cartRows).size();
        } catch (Exception e) {
            return 0;
        }
    }

    // -------------------------------------------------------------------------
    // Data extraction — bulk (all rows at once)
    // -------------------------------------------------------------------------

    /**
     * Returns all product names currently in the cart.
     *
     * Demonstrates the stream + map pattern for extracting text
     * from a List<WebElement> — a very common interview pattern.
     */
    public List<String> getCartProductNames() {
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(allProductNames));
        return driver.findElements(allProductNames)
                .stream()
                .map(WebElement::getText)
                .toList();
    }

    /** Returns all unit prices in the cart (e.g. ["Rs. 500", "Rs. 400"]). */
    public List<String> getCartProductPrices() {
        return driver.findElements(allProductPrices)
                .stream()
                .map(WebElement::getText)
                .toList();
    }

    /** Returns all quantities in the cart (e.g. ["1", "2"]). */
    public List<String> getCartProductQuantities() {
        return driver.findElements(allProductQuantities)
                .stream()
                .map(WebElement::getText)
                .toList();
    }

    /** Returns all line totals in the cart (e.g. ["Rs. 500", "Rs. 800"]). */
    public List<String> getCartProductTotals() {
        return driver.findElements(allProductTotals)
                .stream()
                .map(WebElement::getText)
                .toList();
    }

    // -------------------------------------------------------------------------
    // Data extraction — by row index
    // -------------------------------------------------------------------------

    /**
     * Returns the product name at the given 1-based row index.
     *
     * Row scoping pattern:
     *   1. Get all <tr> rows as a List
     *   2. Pick the row at (index - 1)
     *   3. Call row.findElement() — searches ONLY inside that row
     *   This avoids fragile nth-child CSS and is easy to read.
     *
     * @param rowIndex 1-based (1 = first item in cart)
     */
    public String getProductNameByRow(int rowIndex) {
        List<WebElement> rows = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(cartRows));
        return rows.get(rowIndex - 1).findElement(productNameInRow).getText();
    }

    /** Returns the unit price at the given 1-based row index. */
    public String getProductPriceByRow(int rowIndex) {
        List<WebElement> rows = driver.findElements(cartRows);
        return rows.get(rowIndex - 1).findElement(productPriceInRow).getText();
    }

    /** Returns the quantity at the given 1-based row index. */
    public String getProductQuantityByRow(int rowIndex) {
        List<WebElement> rows = driver.findElements(cartRows);
        return rows.get(rowIndex - 1).findElement(productQuantityInRow).getText();
    }

    /** Returns the line total at the given 1-based row index. */
    public String getProductTotalByRow(int rowIndex) {
        List<WebElement> rows = driver.findElements(cartRows);
        return rows.get(rowIndex - 1).findElement(productTotalInRow).getText();
    }

    // -------------------------------------------------------------------------
    // Cart actions
    // -------------------------------------------------------------------------

    /**
     * Removes the cart item at the given 1-based row index.
     *
     * After clicking the delete (X) link the row is removed from the DOM.
     * A short sleep is used here for simplicity — in production replace with:
     *   wait.until(ExpectedConditions.stalenessOf(deletedRow))
     *
     * @param rowIndex 1-based index of the row to remove
     */
    public void removeProductByRow(int rowIndex) {
        List<WebElement> deleteButtons = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(allDeleteButtons));
        deleteButtons.get(rowIndex - 1).click();

        // Brief pause to let the DOM update after the row is removed
        try { Thread.sleep(600); } catch (InterruptedException ignored) {}
    }

    /**
     * Removes a product from the cart by its exact name.
     *
     * Paired List traversal:
     *   allProductNames and allDeleteButtons are parallel lists — index i in
     *   names corresponds to index i in delete buttons. This is a common
     *   pattern when two related element lists share the same row order.
     *
     * @param productName exact product name as shown in the cart
     */
    public void removeProductByName(String productName) {
        List<WebElement> names   = driver.findElements(allProductNames);
        List<WebElement> deletes = driver.findElements(allDeleteButtons);

        for (int i = 0; i < names.size(); i++) {
            if (names.get(i).getText().equalsIgnoreCase(productName)) {
                deletes.get(i).click();
                return;
            }
        }
        throw new RuntimeException("[CartPage] Product not found in cart: " + productName);
    }

    /**
     * Returns true if a product with the given name is present in the cart.
     */
    public boolean isProductInCart(String productName) {
        try {
            List<WebElement> names = driver.findElements(allProductNames);
            return names.stream()
                    .anyMatch(e -> e.getText().equalsIgnoreCase(productName));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Clicks "Proceed To Checkout" button.
     * Requires the user to be logged in — otherwise a login modal appears.
     */
    public void proceedToCheckout() {
        wait.until(ExpectedConditions.elementToBeClickable(proceedToCheckoutBtn)).click();
    }
}
