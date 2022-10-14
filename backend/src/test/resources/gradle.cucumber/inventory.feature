Feature: Inventory

#  U19: Create Inventory
  Scenario: As a logged in business administrator I can access my inventory. Other users cannot see it.
    Given I am logged in as an admin for an existing business which contains an item with the name "Juice Box" in its inventory
    When I view my businesses inventory
    Then I am able to see all details about the item

#  U20: Modify Inventory
  Scenario: As a logged in business administrator I can modify inventory item.
    Given I am logged in as an admin for an existing business which contains an item with the name "Juice Box" in its inventory
    When I can update my inventory item, by changing the quantity to 20
    Then I am able to view my changes
