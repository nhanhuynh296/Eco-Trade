Feature: U32: Managing my feed

  Scenario: AC1: I can delete items from my feed and undo the most recently deleted
    Given I am logged in and have just deleted a notification
    When I click undo
    Then the notification is restored