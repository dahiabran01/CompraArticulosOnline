@cart
Feature: Shopping cart
  A shopper can add the intended quantity and remove products before checkout.

  @AC08 @happy
  Scenario Outline: Add Blue Top to the cart with quantity <quantity>
    Given the shopper has an empty cart
    When the shopper adds "Blue Top" with a quantity of <quantity> and opens the cart
    Then the cart shows "Blue Top" with a quantity of <quantity>
    And the cart shows a unit price of "Rs. 500" and a subtotal of "<subtotal>" for "Blue Top"

    Examples:
      | quantity | subtotal |
      | 1        | Rs. 500  |
      | 4        | Rs. 2000 |

  @AC09 @alternate
  Scenario: Remove the only product from the cart
    Given the shopper has "Blue Top" with a quantity of 1 in the cart
    When the shopper removes "Blue Top" from the cart
    Then the cart displays "Cart is empty!"

  @AC10 @alternate
  Scenario: Remove one product from a cart with two different products
    Given the shopper has "Blue Top" with a quantity of 1 in the cart
    When the shopper adds "Men Tshirt" with a quantity of 2 and opens the cart
    And the shopper removes "Blue Top" from the cart
    Then the cart shows only "Men Tshirt" with a quantity of 2

  @AC18 @alternate
  Scenario: Keep the guest cart after signing in
    Given the shopper has registered an account and signed out
    And the shopper has "Blue Top" with a quantity of 3 in the cart
    When the shopper opens the sign-in page
    And the shopper signs in with the registered credentials
    Then the header shows the registered shopper's name
    When the shopper opens the cart
    Then the cart shows only "Blue Top" with a quantity of 3
    And the cart shows a unit price of "Rs. 500" and a subtotal of "Rs. 1500" for "Blue Top"

  @AC22 @boundary
  Scenario: Add the minimum quantity without editing the product field
    Given the shopper is viewing the product catalog
    When the shopper opens the details for "Blue Top"
    Then the product quantity starts at its minimum of "1"
    When the shopper adds the displayed product without changing its quantity
    Then the cart shows only "Blue Top" with a quantity of 1
    And the cart shows a unit price of "Rs. 500" and a subtotal of "Rs. 500" for "Blue Top"

  @AC23 @alternate
  Scenario: Return to the catalog from an empty cart and add a product
    Given the shopper has an empty cart
    When the shopper follows the empty cart link to the catalog
    Then the catalog displays the heading "All Products"
    When the shopper opens the details for "Blue Top"
    And the shopper adds the displayed product without changing its quantity
    Then the cart shows only "Blue Top" with a quantity of 1

  @AC30 @boundary @alternate
  Scenario: Accumulate quantity when adding the same product twice
    Given the shopper has "Blue Top" with a quantity of 1 in the cart
    When the shopper adds "Blue Top" with a quantity of 2 and opens the cart
    Then the cart shows only "Blue Top" with a quantity of 3
    And the cart shows a unit price of "Rs. 500" and a subtotal of "Rs. 1500" for "Blue Top"
