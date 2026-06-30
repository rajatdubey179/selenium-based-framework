# Selenium Automation Framework

A clean, production-style UI test automation framework built for learning and interview preparation.

**Target application:** [https://automationexercise.com](https://automationexercise.com)

---

## Tech Stack

| Tool | Version | Purpose |
|---|---|---|
| Java | 17 | Programming language |
| Selenium WebDriver | 4.21.0 | Browser automation |
| TestNG | 7.10.2 | Test runner & assertions |
| Maven | 3.x | Build & dependency management |
| WebDriverManager | 5.8.0 | Auto-downloads browser drivers |

---

## Project Structure

```
selenium-framework/
├── pom.xml                          # Maven dependencies & build config
├── testng.xml                       # TestNG suite — smoke & regression
└── src/
    └── main/
        ├── java/com/automation/
        │   ├── base/
        │   │   └── BaseTest.java        # @BeforeMethod / @AfterMethod lifecycle
        │   ├── pages/
        │   │   ├── HomePage.java        # Home page — nav, categories, brands, footer
        │   │   ├── LoginPage.java       # Login + Signup forms
        │   │   ├── ProductsPage.java    # Product list, search, add to cart, modal
        │   │   └── CartPage.java        # Cart table — verify, remove, checkout
        │   ├── tests/
        │   │   ├── LoginTest.java       # 5 login test cases
        │   │   ├── ProductsTest.java    # 5 products test cases
        │   │   └── CartTest.java        # 6 cart test cases
        │   └── utils/
        │       ├── ConfigReader.java    # Reads config.properties
        │       └── DriverFactory.java   # Creates & quits WebDriver
        └── resources/
            └── config.properties        # baseUrl, browser, implicitWait
```

---

## Design Patterns

### Page Object Model (POM)
Each page of the application has its own class. Locators and actions live in the page class — tests never interact with Selenium APIs directly.

```
Test Class  →  Page Object  →  Selenium WebDriver
```

### DriverFactory
Centralises browser creation. Supports Chrome, Firefox, and Edge via a `browser` config key. Uses WebDriverManager to auto-download the correct driver binary — no manual setup needed.

### ConfigReader
All environment settings are externalised to `config.properties`. Change the browser or URL without touching any Java file.

---

## Configuration

Edit [`src/main/resources/config.properties`](src/main/resources/config.properties):

```properties
baseUrl=https://automationexercise.com
browser=chrome          # chrome | firefox | edge
implicitWait=10         # seconds
```

---

## Prerequisites

- Java 17 installed (`java -version`)
- Maven installed (`mvn -version`)
- Google Chrome installed (default browser)

---

## How to Run

```bash
# Clone / navigate to the project
cd /Users/rajatdubey/projects/selenium-framework

# Run the full suite (smoke + regression)
mvn test

# Run smoke tests only
mvn test -Dgroups="smoke"

# Run a specific test class
mvn test -Dtest=LoginTest
mvn test -Dtest=ProductsTest
mvn test -Dtest=CartTest

# Run a specific test method
mvn test -Dtest=LoginTest#testSuccessfulLogin

# Run on a different browser
mvn test -Dbrowser=firefox
```

---

## Test Cases

### LoginTest (5 tests)

| ID | Test | Group |
|---|---|---|
| TC_001 | Valid credentials → successful login + navbar shows username | smoke, regression |
| TC_002 | Wrong password → error message shown | regression |
| TC_003 | Non-existent email → error message shown | regression |
| TC_004 | Empty credentials → login fails | regression |
| TC_005 | Signup with existing email → duplicate error shown | regression |

### ProductsTest (5 tests)

| ID | Test | Group |
|---|---|---|
| TC_P001 | Products page loads with at least one product card | smoke, regression |
| TC_P002 | Search "Top" → all results contain "Top" in name | regression |
| TC_P003 | Hover product card → click Add to Cart → modal appears | regression |
| TC_P004 | Click View Product → navigates to product detail page | regression |
| TC_P005 | Add to cart → click View Cart in modal → lands on cart page | regression |

### CartTest (6 tests)

| ID | Test | Group |
|---|---|---|
| TC_C001 | Cart page loads with correct breadcrumb | smoke, regression |
| TC_C002 | Product added from detail page appears in cart with correct price | smoke, regression |
| TC_C003 | Add 2 products → cart item count = 2 | regression |
| TC_C004 | Cart row shows correct name, price, quantity and total | regression |
| TC_C005 | Remove only product → cart becomes empty | regression |
| TC_C006 | Remove product by name → other product still remains | regression |

---

## Key Selenium Concepts Covered

| Concept | Where |
|---|---|
| Implicit wait | `DriverFactory.java` |
| Explicit wait (`WebDriverWait`) | All page objects |
| CSS selectors (`[data-qa]`, `.class`, `#id`, `td.cart_price p`) | All page objects |
| XPath with `text()` and `contains()` | `LoginPage`, `CartPage` |
| `Actions` class — hover to reveal hidden overlay | `ProductsPage.java` |
| `JavascriptExecutor` — scroll element into view | `ProductsPage.java` |
| `List<WebElement>` — bulk extraction & iteration | `CartPage.java`, `ProductsPage.java` |
| Row-scoped `findElement` — table cell lookup | `CartPage.java` |
| Modal handling | `ProductsPage.java` |
| TestNG groups — smoke / regression | All test classes |
| TestNG priority — test execution order | All test classes |
| Page Object Model | All page classes |

---

## Locator Strategy

Selectors are chosen in this priority order:

1. **`[data-qa='...']`** — purpose-built test attributes (most stable)
2. **`#id`** — unique element id
3. **CSS class / attribute** — `.cart_price p`, `a[href='/logout']`
4. **XPath** — only when CSS cannot express the relationship (e.g. matching visible text with `contains(text(), '...')`)

---

## Adding a New Page / Test

1. Create `src/main/java/com/automation/pages/NewPage.java`
2. Add locators as `private final By` fields
3. Add action methods and state query methods
4. Create `src/main/java/com/automation/tests/NewTest.java` extending `BaseTest`
5. Add the class to `testng.xml`

---

## Author

Rajat Dubey — built as a learning project for SDET interview preparation.
