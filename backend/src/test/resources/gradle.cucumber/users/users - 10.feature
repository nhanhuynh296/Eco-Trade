Feature: U10: Modifying Individuals
  Background:
    Given I am logged in

  Scenario: AC1 - As a registered individual I can update any of my attributes - Name
    And my name is "Jeff" "Bezos"
    When I change my first name to "Jeremiah", and my last name to "Beelzebub"
    Then my name is updated successfully to "Jeremiah" "Beelzebub"

  Scenario: AC1 - As a registered individual I can update any of my attributes - Email
    And my email is "jeffbezos@mail.com"
    When I change my email to "veryoriginal@mail.com"
    Then my email is updated successfully to "veryoriginal@mail.com"

  Scenario: AC3 - Mandatory attributes are sill mandatory - Email
    And my email is "jeffbezos@mail.com"
    When I try to remove my email
    Then I am returned an error as email is mandatory