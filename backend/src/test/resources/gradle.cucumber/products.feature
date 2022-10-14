Feature: U15: Product catalogue

  Scenario: AC1: As a logged in business admin I can access the product catalogue of the business.
    Given I am logged in as an admin for a business with an existing catalogue of one product
    When I request the products in the catalogue
    Then I can view my business's product with name "Apple"

  Scenario: AC2: I can add a valid items to my catalogue.
    Given I have added two valid products
    When I request the products in the catalogue
    Then The products are in the catalogue

  Scenario: AC3: Created catalogue items have a name, description, manufacturer, RRP, and date.
    Given I am logged in as an admin for a business with an existing catalogue of one product
    When I request the products in the catalogue
    Then The product has the mandatory values with "Apple", "Apple is red", "Apple Company"
