Feature: U16: Product Images

  Scenario: AC1: I can upload one or more images for the product.
    Given I am logged in as a user, with a business that contains a product called "Beans"
    When I upload an image for the product
    Then The image is saved and added to the product

  Scenario: AC2: When the first image for a product is added it is set as the primary image
    Given I am logged in as a user, with a business that contains a product called "Beans"
    And That product has no images associated with it
    When I upload an image for the product
    Then The image is saved and added to the product
    And The products primary image id is set to the images id

  Scenario: AC4: I can delete product images
    Given I am logged in as a user, with a business that contains a product called "Beans"
    And That product has images associated with it
    When I delete an image from the product
    Then The image is deleted successfully

  Scenario: AC2: I can set an image as a primary image
    Given I am logged in as a user, with a business that contains a product called "Beans"
    And That product has a primary and non primary image associated with it
    When I make the non primary a primary image
    Then The image is set as the primary image and the previous one is unset
