Feature: U41 Sales report
  Background:
    Given I am logged in as a business admin with two sales

  Scenario: AC3: I can also specify a custom period by selecting when it starts and ends.
    When I retrieve my business's sales report with custom start date "1999-02-05" and end date "2021-01-16"
    Then the first and last row contain the specified dates

  Scenario Outline: AC4: I can select the granularity of the report.
    When I retrieve my business's sales report with granularity "<granulateBy>"
    Then the correct number of rows are shown
    Examples:
      | granulateBy   |
      | DAY           |
      | WEEK          |
      | MONTH         |
      | YEAR          |

  Scenario: Selecting a time period of more than 100 years
    When I request sale report data with a time period from "1900-01-01" to "2100-01-01"
    Then the report end date will be changed to the start date plus 100 years