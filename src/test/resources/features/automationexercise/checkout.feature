@checkout
Feature: Checkout and order confirmation
  Shoppers can review the order and pay after signing in.

  Background:
    Given the shopper has an empty cart

  @AC11 @AC12 @e2e @happy
  Scenario: Pay for a clothing order as a newly registered shopper
    Given the shopper is signed in with a new account
    And the shopper adds "Blue Top" with a quantity of 2 and opens the cart
    When the shopper proceeds to checkout
    Then checkout shows the shopper's registered delivery address
    And the order summary lists only "Blue Top" with a quantity of 2 and a subtotal of "Rs. 1000"
    And the order total is "Rs. 1000"
    When the shopper submits the order with test payment details
    Then the shop confirms that the order was placed

  @AC14 @happy
  Scenario: Calculate the total for two products with different quantities
    Given the shopper is signed in with a new account
    And the shopper adds "Blue Top" with a quantity of 2 and opens the cart
    And the shopper adds "Men Tshirt" with a quantity of 3 and opens the cart
    When the shopper proceeds to checkout
    Then the order summary lists only "Blue Top" and "Men Tshirt"
    And the order summary shows "Blue Top" with a quantity of 2 and a subtotal of "Rs. 1000"
    And the order summary shows "Men Tshirt" with a quantity of 3 and a subtotal of "Rs. 1200"
    And the order total is "Rs. 2200"

  @AC13 @alternate @negative
  Scenario: Prompt a guest to sign in or register before checkout
    Given the shopper adds "Blue Top" with a quantity of 1 and opens the cart
    When the shopper proceeds to checkout
    Then checkout prompts the shopper to register or sign in
    When the shopper follows the registration or sign-in link
    Then the sign-up form displays the heading "New User Signup!"

  @AC19 @alternate
  Scenario: Resume checkout after registering from the guest cart
    Given the shopper adds "Blue Top" with a quantity of 2 and opens the cart
    When the shopper proceeds to checkout
    And the shopper registers from the checkout prompt
    And the shopper resumes checkout from the cart
    Then checkout shows the shopper's registered delivery and billing addresses
    And the order summary lists only "Blue Top" with a quantity of 2 and a subtotal of "Rs. 1000"
    And the order total is "Rs. 1000"

  @AC24 @negative @boundary
  Scenario: Prevent checkout after the final item is removed
    Given the shopper adds "Blue Top" with a quantity of 1 and opens the cart
    When the shopper removes "Blue Top" from the cart
    Then the cart displays "Cart is empty!"
    And checkout is unavailable from the empty cart

  @AC25 @alternate
  Scenario: Keep the cart when a guest cancels the sign-in prompt
    Given the shopper adds "Blue Top" with a quantity of 2 and opens the cart
    When the shopper proceeds to checkout
    Then checkout prompts the shopper to register or sign in
    When the shopper chooses to continue on the cart
    Then the checkout prompt closes and the cart remains available
    And the cart shows only "Blue Top" with a quantity of 2
    And the cart shows a unit price of "Rs. 500" and a subtotal of "Rs. 1000" for "Blue Top"

  @AC26 @alternate @e2e
  Scenario: Sign in from guest checkout and complete the order
    Given the shopper has an account and is browsing as a guest
    And the shopper adds "Blue Top" with a quantity of 1 and opens the cart
    When the shopper proceeds to checkout
    And the shopper signs in from the checkout prompt
    And the shopper resumes checkout from the cart
    Then checkout shows the shopper's registered delivery address
    And the order summary lists only "Blue Top" with a quantity of 1 and a subtotal of "Rs. 500"
    And the order total is "Rs. 500"
    When the shopper submits the order with test payment details
    Then the shop confirms that the order was placed
