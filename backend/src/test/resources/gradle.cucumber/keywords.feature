Feature: UCM6: Keyword Management

  Scenario: AC2: The keywords may be selected from a system-wide list.
  The list contains keywords which have been added during the creation of previous cards,
  or which have been added by a system administrator
    Given There are already existing keywords with the letter "T" (Case insensitive)
    When I search for keywords containing the letter "T"
    Then I am presented with all the keywords that contain the letter "T"

  @UserCreateKeyword
  Scenario Outline: AC4: As a logged in user, I can add keywords
    Given I am logged in as a user
    When I create a new keyword with name "<keyword>"
    Then The keyword is successfully saved
    Examples:
      | keyword  |
      | Fruit    |
      | Car      |
      | Computer |

  Scenario Outline: AC6: Only system administrators can delete a keyword. The keyword is removed from the list and from any cards it appears on.
    Given I am logged in as an administrator
    And There is a card title "Car" with keyword "<keyword>"
    When I delete the keyword "<keyword>"
    Then The keyword "<keyword>" was removed
    And The keyword "<keyword>" was remove from all the cards
    Examples:
      | keyword |
      | car     |
