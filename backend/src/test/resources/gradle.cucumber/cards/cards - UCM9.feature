Feature: UCM9: Edit Card
  Background:
    Given I am logged in with a card

    Scenario: AC1 - I can select a card and edit it. - Title
      And The card title is "Old card title"
      When I change the card title to "New card title"
      Then The card is updated successfully with title "New card title"

    Scenario: AC2 - I can select a card and edit it. - Keywords
      And The card has keyword "Car"
      When I add a keyword "Vehicle"
      Then The card is updated successfully with both keywords "Car" and "Vehicle"
