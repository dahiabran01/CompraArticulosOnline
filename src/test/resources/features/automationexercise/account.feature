@account
Feature: Shopper accounts
  A shopper can create an account and sign in with the registered credentials.

  @AC01 @happy
  Scenario: Create an account with a new email
    Given the shopper is on the sign-up page
    When the shopper registers a new account
    Then the shop displays "Account Created!"
    When the shopper continues from the account confirmation
    Then the header shows the registered shopper's name

  @AC02 @negative
  Scenario: Reject registration with an email already in use
    Given the shopper has registered an account and signed out
    When the shopper starts sign-up with the registered email
    Then the sign-up form displays the error "Email Address already exist!"

  @AC03 @happy
  Scenario: Sign in with valid credentials
    Given the shopper has registered an account and signed out
    When the shopper signs in with the registered credentials
    Then the header shows the registered shopper's name

  @AC04 @negative
  Scenario: Reject sign-in with an incorrect password
    Given the shopper has registered an account and signed out
    When the shopper signs in with an incorrect password
    Then the sign-in form displays "Your email or password is incorrect!"
    And the sign-in page is displayed and the shopper is signed out

  @AC15 @alternate
  Scenario: Sign out of a shopper account
    Given the shopper has just registered and is signed in
    When the shopper signs out
    Then the sign-in page is displayed and the shopper is signed out

  @AC16 @negative
  Scenario: Reject sign-in after the shopper deletes the account
    Given the shopper has just registered and is signed in
    When the shopper deletes the account
    Then the shop confirms that the account was deleted
    When the shopper continues and tries to sign in with the deleted account
    Then the sign-in form displays "Your email or password is incorrect!"

  @AC20 @negative @boundary
  Scenario: Block sign-in when the required password is empty
    Given the shopper is on the sign-up page
    When the shopper submits the sign-in form with an email and no password
    Then the password is required and the shopper remains signed out

  @AC29 @negative @boundary
  Scenario: Block account creation when the required password is empty
    Given the shopper is on the sign-up page
    When the shopper submits the registration details without a password
    Then the registration password is required and the account is not created
