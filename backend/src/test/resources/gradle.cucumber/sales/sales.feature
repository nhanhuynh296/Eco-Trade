Feature: U31: Purchases
  Background:
    Given I am logged in as a business admin

  Scenario: AC5: : Information about the sale is recorded in a sales history for the seller’s business.
    When I retrieve my business' sales history
    Then listings that have been bought from my business are returned
