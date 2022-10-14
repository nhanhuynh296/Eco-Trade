Feature: U30: Individual full sale listing; U31: Purchases

  Scenario: U30: Full details of the individual sale listing are displayed.
    Given I am logged in as a user on the listings page
    When I click on a listing
    Then Information relating only to that listing is displayed

  Scenario: U31: AC4: When I buy a listing, the listing is removed from future searches
    Given I am logged-in
    When I buy a listing
    Then The listing is removed from future searches

  Scenario: U31: AC5: When I buy a listing, a corresponding sale record is added to the selling business' sales history
    Given I am logged-in
    When I buy a listing
    Then Information about the sale is recorded in a sales history for the seller’s business.

  Scenario: U31: AC1: When I activate the "Buy" control (see U30) any other users who have liked the corresponding sale listing are notified.
    Given People have liked a listing including myself
    When Someone buys that listing
    Then I receive a notification that the specific listing has sold

  Scenario: U31: AC1: When I activate the "Buy" control (see U30) any other users who have liked the corresponding sale listing are notified.
    Given People have liked a listing including myself
    When I buy that listing
    Then every other user who liked the listing receives a notification, but not myself

  Scenario: U31: AC2: A notification appears on my home feed to remind me what I have purchased
    Given I am logged-in
    When I buy a listing
    Then I receive a notification about the purchase confirmation
    And The notification contains what I have purchased, price and pickup location
