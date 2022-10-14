Feature: U10: Modifying individuals

  Scenario: AC5/AC7: I can upload one or more images with thumbnails being created automatically
    Given I am logged in as a user with no images
    When I upload an image to my profile
    Then The image is saved and added my profile
    And a thumbnail of the image is created for me

  Scenario: AC6: One of these images is deemed to be the primary image.
    Given I am logged in as a user with an existing image
    When I make the image the primary image
    Then The primary image id is set to the image id
