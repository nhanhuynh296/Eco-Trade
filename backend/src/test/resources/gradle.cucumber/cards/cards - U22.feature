Feature: UCM2: Card Creation

  @CreateCard
  Scenario Outline: AC1: I can create a card to be displayed in one of the three sections
    Given I am logged in as user with no card
    When I create a card using title "<title>", and section "<section>"
    Then The card is created with the correct title and section
    And The creatorId of the card is the id of the user who created the card
    Examples:
      | title | section  |
      | Car   | ForSale  |
      | Car   | Wanted   |
      | Car   | Exchange |


  @ValidTitle
  Scenario: AC3: The card has a title which is intended to be suitable for one-line displays
    Given I am logged in as user with a card
    When I set the card title "1982 Lada Samara"
    Then I expect the card to be saved with the new valid title

#  @InvalidTitle
#  Scenario: AC3: The card has a title which is intended to be suitable for one-line displays
#    Given I am logged in as user with a card
#    And  I have a card
#    When I set the card title "1982 Lada Samara This is a really long title and should be invalid blah blah blah blah"
#    Then I expect the card to not be saved with the new invalid title

  @AddKeyword
  Scenario Outline: AC5: One or more keywords can be added
    Given I am logged in as user with a card
    When I add valid keywords "<keyword1>" and "<keyword2>" to that card
    Then I expect the added keywords to be saved to the card
    Examples:
      | keyword1 | keyword2 |
      | Car      | Wheels   |
      | House    | Door     |
      | Dog      | Collar   |

  @CardExpireNotification
  Scenario: AC3: Get notify near expired card
    Given I am logged in as user with a card
    When My card expiry date is less than 1 day
    Then I am notified or notification is created


  @CardDeleteNotification
  Scenario: AC3: Remove notification
    Given I am logged in as user with a notification
    When I delete the notification for the card
    Then The notification is deleted successfully

  @CardSearch
  Scenario Outline: : AC2: A keyword search facility is available in each marketplace section.  Only cards from the current section will be searched.
    Given I am logged in as user with no card
    And There is a card with title "Subaru" and "<keyword>" in "<section>"
    When I search for card with "<keyword>" in "<section>"
    Then I expect to see that card along with all the card with "<keyword>" in "<section>"
    Examples:
    | keyword | section |
    | Vehicle | ForSale |




  @ReceiveMessageNotification
  Scenario: AC3: Sending a message results in a new item of the recipient's feed
    Given I have a card titled "1982 Lada Samara" that someone can comment on
    When A different user comments "Is this still for sale?" on the card
    Then I receive a notification that includes the correct message and the senders information

