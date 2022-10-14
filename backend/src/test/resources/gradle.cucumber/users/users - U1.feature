Feature: U1: Registering and logging into an individual account

  @BeforeEach
  Scenario: AC1 - Registering a new user
    Given I have not registered with the email "johnsmith99@gmail.com" before
    When I register with name "John 'Jonny' Hector Smith", bio "Hi there", email "johnsmith99@gmail.com", dob "1999-04-27", phone "+64 3 555 0129", address "3/24, Ilam Road, Christchurch, Canterbury, New Zealand, 9022", password "pass"
    Then The user is created with correct name, bio, email, dob, phone, address and password

  @validLogin
  Scenario: AC1 - User Should Login With Valid Credentials
    Given I have registered an account with valid credentials
    When I login with registered valid email "johnsmith99@gmail.com" and password "pass"
    Then I am logged into the website and status code is ok

  @invalidLogin
  Scenario Outline: AC1 - Email and Password Validation in Login API
    Given I have registered an account with valid credentials
    When I attempt to login with invalid "<email>" and "<password>" credential combinations
    Then I am not logged into the website and status code is bad request
    Examples:
      | email         | password |
      |               |          |
      | test          |          |
      | test@mail.com |          |
      |               | pass     |
      | test@mail.com | password |

  @validRegister
  Scenario Outline: AC1 - User Should Register With Valid Credentials
    Given I am not logged into the website
    And I have not registered with the email "<email>" before
    When I provide valid credentials "<firstname>", "<middlename>", "<lastname>", "<nickname>", "<bio>", "<email>", "<dob>", "<phonenumber>", "<homeAddress>", "<password>"
    Then I am taken to my home page
    And logged in as the correct user

    Examples:
      | firstname | middlename | lastname | nickname | bio        | email           | dob        | phonenumber    | homeAddress                                               | password |
      | John      | Joseph     | Smith    | Jonny    | Likes fish | jonny@gmail.com | 1999-04-27 | +64 3 555 0129 | 1, John Road, Christchurch, Canterbury, New Zealand, 9022 | password |
      #|Mary|      |Adams|     |          |mary@gmail.com |2002-04-27|              |1, John Road, Christchurch, Canterbury, New Zealand, 9022|secret|

  @invalidRegister
  Scenario Outline: AC1 - Email and Password Validation in Register API
    Given I am not logged into the website
    When I provide nulls in any of the required fields "<firstname>", "<middlename>", "<lastname>", "<nickname>", "<bio>", "<email>", "<dob>", "<phonenumber>", "<homeaddress>", "<password>"
    Then I am not taking to my home page and remain on the register page

    Examples:
      | firstname | middlename | lastname | nickname | bio        | email           | dob        | phonenumber    | homeaddress                                               | password |
      |           | Joseph     | Smith    | Jonny    | Likes fish | test1@gmail.com | 1999-04-27 | +64 3 555 0129 | 1, John Road, Christchurch, Canterbury, New Zealand, 9022 | password |
      | John      | Joseph     |          | Jonny    | Likes fish | test2@gmail.com | 1999-04-27 | +64 3 555 0129 | 1, John Road, Christchurch, Canterbury, New Zealand, 9022 | password |
      | John      | Joseph     | Smith    | Jonny    | Likes fish |                 | 1999-04-27 | +64 3 555 0129 | 1, John Road, Christchurch, Canterbury, New Zealand, 9022 | password |
      | John      | Joseph     | Smith    | Jonny    | Likes fish | test3@gmail.com |            | +64 3 555 0129 | 1, John Road, Christchurch, Canterbury, New Zealand, 9022 | password |
      | John      | Joseph     | Smith    | Jonny    | Likes fish | test8@gmail.com | 1999-04-27 | +64 3 555 0129 |                                                           | password |
      | John      | Joseph     | Smith    | Jonny    | Likes fish | test8@gmail.com | 1999-04-27 | +64 3 555 0129 |                                                           |          |
      | John      | Joseph     | Smith    | Jonny    | Likes fish | invalidemail.co | 1999-04-27 | +64 3 555 0129 | 1, John Road, Christchurch, Canterbury, New Zealand, 9022 | password |
      | John      | Joseph     | Smith    | Jonny    | Likes fish | invalid@email   | 1999-04-27 | +64 3 555 0129 | 1, John Road, Christchurch, Canterbury, New Zealand, 9022 | password |


