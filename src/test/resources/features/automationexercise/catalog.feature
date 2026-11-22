@catalog
Feature: Product catalog
  A shopper can search for a product and review its details before buying.

  @AC05 @happy @boundary
  Scenario Outline: Find a product by name regardless of letter case
    Given the shopper is viewing the product catalog
    When the shopper searches for "<query>"
    Then the search results include "Blue Top"

    Examples:
      | query    |
      | Blue Top |
      | blue top |

  @AC06 @negative
  Scenario: Search for a product that does not exist
    Given the shopper is viewing the product catalog
    When the shopper searches for "zz-no-matching-clothing-90210"
    Then the search results contain no products

  @AC07 @happy
  Scenario: Review an in-stock product
    Given the shopper is viewing the product catalog
    When the shopper opens the details for "Blue Top"
    Then the product details show "Blue Top" at "Rs. 500" as "In Stock"

  @AC17 @alternate
  Scenario: Filter the catalog by the women's tops subcategory
    Given the shopper is viewing the product catalog
    When the shopper opens the "Tops" subcategory under "Women"
    Then the catalog displays the heading "Women - Tops Products"
    And the category results include "Blue Top" and exclude "Men Tshirt"

  @AC21 @alternate
  Scenario: Open an available product from a partial-name search
    Given the shopper is viewing the product catalog
    When the shopper searches for "Blue"
    Then the search results include "Blue Top"
    When the shopper opens the details for "Blue Top"
    Then the product details show "Blue Top" at "Rs. 500" as "In Stock"
