Feature: U32: So that my home page feed doesn't get cluttered, as a logged-in user, I need to be able to manage the items in my feed.

  Scenario: AC3: I can “star” items to mark them as high importance.
    Given I have a notification with an existing tag
    When I star the notification
    Then the notification is starred

  Scenario: AC6: I can "tag" an item.
    Given I have a notification with an existing tag
    When I add the tag "High Priority"
    Then the notification is updated with that tag

  Scenario: AC6: Tags can be removed
    Given I have a notification with an existing tag
    When I remove the tag
    Then the notification is updated and no longer has a tag

  Scenario: AC6: I can search for a notification with a specific tag
    Given I have a notification with an existing tag
    When I search for that tag
    Then the notifications with that tag are returned