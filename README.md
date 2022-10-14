<br />
<h1 style="text-align: center">
  Scrumbledoor's Army
</h1>
<p style="text-align: center">

  <h3 style="text-align: center">SENG302 Team 800</h3>

  <p style="text-align: center">
    <br />
    <a href="https://eng-git.canterbury.ac.nz/seng302-2021/team-800/-/wikis/home"><strong>Explore the Wiki »</strong></a>
    <br />
    <br />
    ·
    <a href="https://csse-s302g8.canterbury.ac.nz/prod/">Production Site</a>
    ·
  </p>
</p>

<!-- TABLE OF CONTENTS -->
<details open="open">
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#basic-project-structure">Basic Project Structure</a></li>
        <li><a href="#how-to-run">How to Run</a></li>
      </ul>
    </li>
    <li><a href="#production-build">Production Build</a></li>
    <li><a href="#contributors">Contributors</a></li>
    <li><a href="#references">References</a></li>
  </ol>
</details>

<!-- ABOUT THE PROJECT -->
## About The Project
"Every year, New Zealanders send around 2.5 million tonnes of waste to landfill” (over a tonne of rubbish per household.

This is a software application where stores can advertise any products that they are about to throw out at a reduced cost to the public. The primary purpose of this application is to reduce the amount of wastage.

### Built With

* [Gradle](https://gradle.org/)
* [npm](https://www.npmjs.com/)
* [Spring Boot](https://spring.io/projects/spring-boot)
* [Vue.js](https://vuejs.org/)
* [Gitlab CI](https://docs.gitlab.com/ee/ci/)

## Getting Started

The following lists the prerequisites, the project structure and how to get the project up and running.

Please refer to the documentation found in [`resources/KiwiFruitUserManual.pdf`](resources/KiwiFruitUserManual.pdf) to find out more on how to use the application.


### Prerequisites

* Gradle
* npm
* Spring Boot
* Vue.js

### Basic Project Structure

A frontend sub-project (web GUI):

- `frontend/src` Frontend source code (Vue.js)
- `frontend/public` publicly accessible web assets (e.g., icons, images, style sheets)
- `frontend/dist` Frontend production build
- `frontend/test` Frontend tests of javascript modules & functions

A backend sub-project (business logic and persistence server):

- `backend/src` Backend source code (Java - Spring)
- `backend/test` Backend testing code
- `backend/out` Backend production build
- `backend/log` automatically generated folder containing all backend logs

Generic Files

- `media/images` Stored images
- `resources/` Stored resources such as the API specification and POSTMAN config

### How to Run

### Frontend / GUI

    $ cd frontend
    $ npm install
    $ npm run serve

Running on: http://localhost:9500/ by default

### Backend / server

    cd backend
    ./gradlew bootRun

Running on: http://localhost:9499/ by default

## Production Build

There is a [production version of this project](https://csse-s302g8.canterbury.ac.nz/prod/) which can be accessed through the following two accounts (or you may signup).

**Test User**

`email`: johnsmith@gmail.com

`password`: Jsmith123

This user has fully generated bulk data. Their business "Food Haven" has bulk products, inventory and listings
aswell as sales history from 05/03/2018 - 30/09/2021.

To test this locally rename bulk_data.sql to data.sql

**Test User 2**

`email`: cja@uclive.ac.nz

`password`: password123

**Admin User**

`email`: admin@admin.com

`password`: admin

## Contributors

- SENG302 teaching team
- Swapnil Bhagat
- Christian Askey
- Harrison Caughey
- Finn Bright
- Seth Kingsbury
- Callum McLoughlin
- Inga Tokarenko
- Nathan Huynh

## References

- [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring JPA docs](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Vue docs](https://vuejs.org/v2/guide/)
- [Learn resources](https://learn.canterbury.ac.nz/course/view.php?id=10577&section=11)
