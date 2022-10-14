Feature: U4: When a user wants to search for other users they will query the backend for all users that are
  registered. Certain users are able to be excluded from the search results based on their roles. DGAAs are
  users with a global admin role, these special DGAA users are able to search for all users, even ones that
  may be excluded from the search results.

  Scenario: AC4: The DGAA can search for a particular individual.
    Given I am logged in with global admin privileges
    When I search for myself on the user search page
    Then the webpage displays my account in the results
