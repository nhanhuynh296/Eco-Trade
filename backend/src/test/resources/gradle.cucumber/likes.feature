Feature: U30: Individual Full Sale Listing

  Scenario: User likes a listing
    Given The user does not like a listing
    When A listing is liked by the user
    Then The user likes the listing

  Scenario: User unlikes a listing
    Given The user likes a listing
    When A listing is unliked by the user
    Then The user does not like the listing

  Scenario: Notification when user liked a listing
    Given The user does not like a listing
    When A listing is liked by the user
    Then The user likes the listing
    And I should have a message added to my home feed

    Scenario: Notification when user unlikes a listing
      Given The user likes a listing
      When A listing is unliked by the user
      Then The user receives a notification telling them they have unliked the listing