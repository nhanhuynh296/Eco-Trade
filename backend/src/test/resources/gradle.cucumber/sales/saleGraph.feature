Feature: U42: Sales report graph
  Background:
    Given I am logged in as a business admin with one sale

  Scenario: By selecting the report graph display seller's business is able to view the data in a graph format
    When I retrieve my business' sales report graph
    Then listings that have been bought from my business are returned in a graph format