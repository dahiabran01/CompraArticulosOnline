@cart
Feature: Shopping cart
  A shopper can add the intended quantity and remove products before checkout.

  @AC08
  Scenario Outline: Add Blue Top to the cart with quantity <quantity>
    Given the shopper has an empty cart
    When the shopper adds "Blue Top" with a quantity of <quantity> and opens the cart
    Then the cart shows "Blue Top" with a quantity of <quantity>
    And the cart shows a unit price of "Rs. 500" and a subtotal of "<subtotal>" for "Blue Top"

    Examples:
      | quantity | subtotal |
      | 1        | Rs. 500  |
      | 4        | Rs. 2000 |

  @AC09
  Scenario: Remove the only product from the cart
    Given the shopper has "Blue Top" with a quantity of 1 in the cart
    When the shopper removes "Blue Top" from the cart
    Then the cart displays "Cart is empty!"

  @AC10
  Scenario: Remove one product from a cart with two different products
    Given the shopper has "Blue Top" with a quantity of 1 in the cart
    When the shopper adds "Men Tshirt" with a quantity of 2 and opens the cart
    And the shopper removes "Blue Top" from the cart
    Then the cart shows only "Men Tshirt" with a quantity of 2

  @AC18
  Scenario: Keep the guest cart after signing in
    Given the shopper has registered an account and signed out
    And the shopper has "Blue Top" with a quantity of 3 in the cart
    When the shopper opens the sign-in page
    And the shopper signs in with the registered credentials
    Then the header shows the registered shopper's name
    When the shopper opens the cart
    Then the cart shows only "Blue Top" with a quantity of 3
    And the cart shows a unit price of "Rs. 500" and a subtotal of "Rs. 1500" for "Blue Top"
