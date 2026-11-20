@checkout
Feature: Checkout and order confirmation
  A registered shopper can review delivery details, pay and receive confirmation.

  @AC11 @AC12 @e2e
  Scenario: Register and pay for a clothing order
    Given the shopper is on the sign-up page
    When the shopper registers a new account
    Then the shop displays "Account Created!"
    When the shopper continues from the account confirmation
    And the shopper adds "Blue Top" with a quantity of 2 and opens the cart
    And the shopper proceeds to checkout
    Then checkout shows the shopper's registered delivery address
    And the order summary shows "Blue Top" with a quantity of 2 and a subtotal of "Rs. 1000"
    And the order total is "Rs. 1000"
    When the shopper submits the order with test payment details
    Then the order page displays the heading "Order Placed!"
    And the order page displays the confirmation "Congratulations! Your order has been confirmed!"

  @AC14
  Scenario: Review the order total for two products with different quantities
    Given the shopper is on the sign-up page
    And the shopper registers a new account
    And the shopper continues from the account confirmation
    When the shopper adds "Blue Top" with a quantity of 2 and opens the cart
    And the shopper adds "Men Tshirt" with a quantity of 3 and opens the cart
    And the shopper proceeds to checkout
    Then the order summary lists only "Blue Top" and "Men Tshirt"
    And the order summary shows "Blue Top" with a quantity of 2 and a subtotal of "Rs. 1000"
    And the order summary shows "Men Tshirt" with a quantity of 3 and a subtotal of "Rs. 1200"
    And the order total is "Rs. 2200"

  @AC13 @alternate
  Scenario: Prompt a guest to sign in or register before checkout
    Given the shopper has "Blue Top" with a quantity of 1 in the cart
    When the shopper proceeds to checkout
    Then checkout prompts the shopper to register or sign in
    When the shopper follows the registration or sign-in link
    Then the sign-up form displays the heading "New User Signup!"

  @AC19 @alternate
  Scenario: Resume checkout after registering from the guest cart
    Given the shopper has "Blue Top" with a quantity of 2 in the cart
    When the shopper proceeds to checkout
    And the shopper follows the registration or sign-in link
    And the shopper registers a new account
    And the shopper continues from the account confirmation
    And the shopper opens the cart
    And the shopper proceeds to checkout
    Then checkout shows the shopper's registered delivery address
    And checkout shows the shopper's registered billing address
    And the order summary shows "Blue Top" with a quantity of 2 and a subtotal of "Rs. 1000"
    And the order total is "Rs. 1000"
