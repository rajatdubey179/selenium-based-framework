package com.automation.tests;

import com.automation.base.BaseTest;
import com.automation.pages.CartPage;
import com.automation.pages.ProductsPage;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * CartTest — verifies the shopping cart of automationexercise.com
 *
 * TEST FLOW for most cases:
 *   Products page → add product(s) → navigate to cart → assert cart state
 *
 * SKILLS DEMONSTRATED:
 *   - Multi-step end-to-end flow across pages
 *   - List<WebElement> data extraction and assertions
 *   - Row-based table verification (name, price, quantity, total)
 *   - Remove product and verify cart updates
 *   - Empty cart state verification
 */
public class CartTest extends BaseTest {

    // -----------------------------------------------------------------------
    // TC_C001 — Cart page loads correctly
    // -----------------------------------------------------------------------

    /**
     * Navigates directly to the cart page and confirms we land correctly.
     * Tests both empty-cart state detection and page identity.
     */
    @Test(description = "Cart page should load and show correct breadcrumb",
          priority = 1, groups = {"smoke", "regression"})
    public void testCartPageLoads() {
        CartPage cartPage = new CartPage(driver);
        cartPage.navigateTo();

        Assert.assertTrue(cartPage.isOnCartPage(),
                "Breadcrumb should show 'Shopping Cart'.");

        System.out.println("[CartTest] Cart is empty: " + cartPage.isCartEmpty());
    }

    // -----------------------------------------------------------------------
    // TC_C002 — Adding a product from product details page appears in cart
    // -----------------------------------------------------------------------

    /**
     * Full flow:
     *   1. Go to product details page (Blue Top — id=1)
     *   2. Click "Add to cart"
     *   3. Navigate to cart
     *   4. Assert "Blue Top" is in the cart with correct price
     *
     * Demonstrates: cross-page flow + product name + price assertion
     */
    @Test(description = "Product added from detail page should appear in cart",
          priority = 2, groups = {"smoke", "regression"})
    public void testProductAppearsInCartAfterAdding() {
        // Navigate to product detail page for "Blue Top" (id=1)
        driver.get("https://automationexercise.com/product_details/1");

        // Click "Add to cart" on the product detail page
        driver.findElement(org.openqa.selenium.By.cssSelector("button.cart")).click();

        // Go to cart
        CartPage cartPage = new CartPage(driver);
        cartPage.navigateTo();

        Assert.assertFalse(cartPage.isCartEmpty(),
                "Cart should not be empty after adding a product.");

        Assert.assertTrue(cartPage.isProductInCart("Blue Top"),
                "'Blue Top' should be present in the cart.");

        // Verify price is correct
        String price = cartPage.getProductPriceByRow(1);
        Assert.assertEquals(price, "Rs. 500",
                "Unit price of Blue Top should be Rs. 500. Got: " + price);

        System.out.println("[CartTest] Cart contains: " + cartPage.getCartProductNames());
    }

    // -----------------------------------------------------------------------
    // TC_C003 — Cart item count is correct
    // -----------------------------------------------------------------------

    /**
     * Adds two different products and verifies the cart has 2 rows.
     *
     * Demonstrates: adding multiple products, getCartItemCount()
     */
    @Test(description = "Cart should show correct item count after adding multiple products",
          priority = 3, groups = {"regression"})
    public void testCartItemCount() {
        ProductsPage productsPage = new ProductsPage(driver);
        productsPage.navigateTo();

        // Add product at index 1 → modal appears → continue shopping
        productsPage.addFirstProductToCart();
        productsPage.continueShopping();

        // Add product at index 2 → modal appears → continue shopping
        productsPage.addProductToCartByIndex(2);
        productsPage.continueShopping();

        // Navigate to cart and verify 2 rows
        CartPage cartPage = new CartPage(driver);
        cartPage.navigateTo();

        int count = cartPage.getCartItemCount();
        Assert.assertEquals(count, 2,
                "Cart should have 2 items after adding 2 products. Got: " + count);

        System.out.println("[CartTest] Items in cart: " + count);
        System.out.println("[CartTest] Product names: " + cartPage.getCartProductNames());
    }

    // -----------------------------------------------------------------------
    // TC_C004 — Verify cart product names, prices, quantities, totals
    // -----------------------------------------------------------------------

    /**
     * Adds a product and verifies all four cart columns:
     *   - Name
     *   - Unit price
     *   - Quantity (default = 1)
     *   - Total (should equal price × quantity)
     *
     * Demonstrates: row-scoped data extraction + multiple Assert calls
     */
    @Test(description = "Cart row should display correct name, price, quantity and total",
          priority = 4, groups = {"regression"})
    public void testCartRowData() {
        driver.get("https://automationexercise.com/product_details/1");
        driver.findElement(org.openqa.selenium.By.cssSelector("button.cart")).click();

        CartPage cartPage = new CartPage(driver);
        cartPage.navigateTo();

        // Name
        String name = cartPage.getProductNameByRow(1);
        Assert.assertEquals(name, "Blue Top", "Product name mismatch.");

        // Price
        String price = cartPage.getProductPriceByRow(1);
        Assert.assertEquals(price, "Rs. 500", "Product price mismatch.");

        // Default quantity should be 1
        String qty = cartPage.getProductQuantityByRow(1);
        Assert.assertEquals(qty, "1", "Default quantity should be 1.");

        // Total = price × qty = Rs. 500 × 1 = Rs. 500
        String total = cartPage.getProductTotalByRow(1);
        Assert.assertEquals(total, "Rs. 500", "Line total mismatch.");

        System.out.println("[CartTest] Row data — Name: " + name
                + " | Price: " + price + " | Qty: " + qty + " | Total: " + total);
    }

    // -----------------------------------------------------------------------
    // TC_C005 — Remove product from cart
    // -----------------------------------------------------------------------

    /**
     * Adds a product, then removes it using the delete (X) button.
     * Verifies the cart is empty after removal.
     *
     * Demonstrates: removeProductByRow() + isCartEmpty() assertion
     */
    @Test(description = "Removing the only product should leave the cart empty",
          priority = 5, groups = {"regression"})
    public void testRemoveProductFromCart() {
        driver.get("https://automationexercise.com/product_details/1");
        driver.findElement(org.openqa.selenium.By.cssSelector("button.cart")).click();

        CartPage cartPage = new CartPage(driver);
        cartPage.navigateTo();

        Assert.assertFalse(cartPage.isCartEmpty(),
                "Cart should have 1 item before removal.");

        cartPage.removeProductByRow(1);

        Assert.assertTrue(cartPage.isCartEmpty(),
                "Cart should be empty after removing the only product.");

        System.out.println("[CartTest] Product removed — cart is now empty.");
    }

    // -----------------------------------------------------------------------
    // TC_C006 — Remove product by name
    // -----------------------------------------------------------------------

    /**
     * Adds two products, removes one by name, verifies the other remains.
     *
     * Demonstrates: removeProductByName() + isProductInCart()
     */
    @Test(description = "Remove specific product by name, other product should remain",
          priority = 6, groups = {"regression"})
    public void testRemoveProductByName() {
        ProductsPage productsPage = new ProductsPage(driver);
        productsPage.navigateTo();

        productsPage.addFirstProductToCart();
        productsPage.continueShopping();
        productsPage.addProductToCartByIndex(2);
        productsPage.continueShopping();

        CartPage cartPage = new CartPage(driver);
        cartPage.navigateTo();

        List<String> names = cartPage.getCartProductNames();
        Assert.assertEquals(names.size(), 2, "Should have 2 products before removal.");

        // Remove the first product by name
        String productToRemove = names.get(0);
        String productToKeep   = names.get(1);

        cartPage.removeProductByName(productToRemove);

        // The removed product should no longer be in cart
        Assert.assertFalse(cartPage.isProductInCart(productToRemove),
                "'" + productToRemove + "' should have been removed from cart.");

        // The other product should still be there
        Assert.assertTrue(cartPage.isProductInCart(productToKeep),
                "'" + productToKeep + "' should still be in the cart.");

        System.out.println("[CartTest] Removed: " + productToRemove
                + " | Remaining: " + productToKeep);
    }
}
