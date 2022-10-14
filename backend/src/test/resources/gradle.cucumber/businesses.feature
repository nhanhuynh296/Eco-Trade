Feature: U5: Creating Business Account

  Scenario: AC1: As a logged-in individual, I can create/register a Business Account
    Given I am logged in as user
    When I create a business account using name "Lumbridge", description "Adventuring store", businessType "Accommodation"
    Then The business account is created with the correct name, description and business type
    And The primary admin of the business is the user who created the account

  Scenario: AC1: As a logged-in individual, I can't create/register a Business Account that is invalid
    Given I am logged in as user
    When I create a business account using name "", description "", businessType ""
    Then The business account is not created

  Scenario: AC1: As a logged-in individual, I can view all my businesses
    Given I am logged in as user with one business
    When I can request to view all of my businesses
    Then I should be able to see my "Lumbridge" business
