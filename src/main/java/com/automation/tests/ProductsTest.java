package com.automation.tests;

import com.automation.base.BaseTest;
import com.automation.pages.HomePage;
import com.automation.pages.ProductsPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * ProductsTest — verifies the Products page of automationexercise.com
 *
 * SKILLS DEMONSTRATED:
 *   - Multi-page flow (Home → Products → Product Detail)
 *   - Hover interaction to reveal hidden "Add to cart" button
 *   - Modal handling ("Added!" confirmation dialog)
 *   - Search functionality with result verification
 *   - List<WebElement> assertions (product count, search results)
 *   - JavascriptExecutor scroll (inside ProductsPage)
 */
public class ProductsTest extends BaseTest {

    /**
     * TC_P001 — Products page should load with at least one product card.
     */
    @Test(description = "Products page should display a list of products",
          priority = 1, groups = {"smoke", "regression"})
    public void testProductsPageLoads() {
        HomePage home = new HomePage(driver);
        home.clickProducts();

        ProductsPage productsPage = new ProductsPage(driver);
        Assert.assertTrue(productsPage.isOnProductsPage(),
                "Should be on the All Products page after clicking Products in nav.");

        int count = productsPage.getProductCount();
        Assert.assertTrue(count > 0,
                "Product list should contain at least one item. Got: " + count);

        System.out.println("[ProductsTest] Total products visible: " + count);
    }

    /**
     * TC_P002 — Searching "Top" should return products whose names contain "Top".
     *
     * Demonstrates:
     *   - Typing in a search box and submitting
     *   - Iterating search result names and asserting each one
     */
    @Test(description = "Search for 'Top' should return matching products",
          priority = 2, groups = {"regression"})
    public void testSearchProduct() {
        ProductsPage productsPage = new ProductsPage(driver);
        productsPage.navigateTo();

        productsPage.searchProduct("Top");

        java.util.List<String> results = productsPage.getSearchResultProductNames();

        Assert.assertFalse(results.isEmpty(),
                "Search for 'Top' should return at least one result.");

        // Each result name should contain "Top" (case-insensitive)
        for (String name : results) {
            Assert.assertTrue(name.toLowerCase().contains("top"),
                    "Search result '" + name + "' does not match query 'Top'.");
        }

        System.out.println("[ProductsTest] Search results: " + results);
    }

    /**
     * TC_P003 — Adding the first product to cart should show the confirmation modal.
     *
     * Demonstrates:
     *   - Actions.moveToElement() hover to reveal the hidden overlay button
     *   - Modal assertion after the action
     */
    @Test(description = "Hovering a product card and clicking Add to Cart should show modal",
          priority = 3, groups = {"regression"})
    public void testAddFirstProductToCart() {
        ProductsPage productsPage = new ProductsPage(driver);
        productsPage.navigateTo();

        productsPage.addFirstProductToCart();

        Assert.assertTrue(productsPage.isAddedToCartModalVisible(),
                "The 'Added to Cart' modal should appear after clicking Add to cart.");

        // Dismiss the modal by clicking "Continue Shopping"
        productsPage.continueShopping();
    }

    /**
     * TC_P004 — Clicking "View Product" should navigate to the product detail page.
     *
     * Product detail URL pattern: /product_details/{id}
     */
    @Test(description = "Clicking View Product should navigate to product detail page",
          priority = 4, groups = {"regression"})
    public void testViewProductDetail() {
        ProductsPage productsPage = new ProductsPage(driver);
        productsPage.navigateTo();

        productsPage.viewProductByIndex(1);

        // Verify URL contains /product_details/
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/product_details/"),
                "URL should contain '/product_details/' after clicking View Product. Got: "
                        + currentUrl);

        System.out.println("[ProductsTest] Navigated to: " + currentUrl);
    }

    /**
     * TC_P005 — Adding a product and navigating to cart via modal link.
     *
     * Full flow: Products page → Add to cart → modal → View Cart
     * Demonstrates an end-to-end mini-flow across two pages.
     */
    @Test(description = "Clicking View Cart in modal should navigate to cart page",
          priority = 5, groups = {"regression"})
    public void testAddToCartAndGoToCart() {
        ProductsPage productsPage = new ProductsPage(driver);
        productsPage.navigateTo();

        productsPage.addFirstProductToCart();

        Assert.assertTrue(productsPage.isAddedToCartModalVisible(),
                "Modal should be visible before clicking View Cart.");

        productsPage.goToCartFromModal();

        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/view_cart"),
                "Should navigate to the cart page. Got: " + currentUrl);

        System.out.println("[ProductsTest] Landed on cart page: " + currentUrl);
    }
}
