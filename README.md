# JAlgoArena Submissions [![Build Status](https://travis-ci.org/spolnik/JAlgoArena-Submissions.svg?branch=master)](https://travis-ci.org/spolnik/JAlgoArena-Submissions) [![codecov](https://codecov.io/gh/spolnik/JAlgoArena-Submissions/branch/master/graph/badge.svg)](https://codecov.io/gh/spolnik/JAlgoArena-Submissions) [![GitHub release](https://img.shields.io/github/release/spolnik/jalgoarena-submissions.svg)]()

JAlgoArena Submissions is service dedicated for collecting users submissions and exposing that data. Querying submissions and submitting it has to be secure operation - methods require passing token which is then checked with Auth service.

- [Introduction](#introduction)
- [API](#api)
- [Running Locally](#running-locally)
- [Notes](#notes)

## Introduction

- JAlgoArena Submissions exposes Submissions data via REST API

![Component Diagram](https://github.com/spolnik/JAlgoArena-Submissions/raw/master/design/component_diagram.png)

## API

#### Find all user submissions

  _Given user token - find all user submissions_

|URL|Method|
|---|------|
|_/submissions/:userId_|`GET`|

* **Data Params**

  _User id path parameter has to be in sync with token based user id set in headers_
  
  `GET /submissions/1`
  
  ```
  'Accept': 'application/json',
  'X-Authorization': 'Bearer <token>'
  ```

* **Success Response:**

  _List of all submissions_

  * **Code:** 200 <br />
    **Content:** `[{"sourceCode":"<source code>",...}, ...]`

* **Sample Call:**

  ```bash
  curl --header "Content-Type: application/json" \
       --header "X-Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMSIsInNjb3BlcyI6WyJST0xFX1VTRVIiXSwiaXNzIjoiamFsZ29hcmVuYS5jb20iLCJpYXQiOjE1MzI2ODUyMDUsImV4cCI6MTUzNTI3NzIwNX0.45Ilu0GnQyBVYprAcgtvPHmq5tdvbwiUZucSRAFDDPU2RYY-N8cDoM8k3gl1i2r4FPV7ECZaHgcc20fZwqj_CQ" \
       http://localhost:5004/submissions/1
  ```

#### Find user submission by given submission id

  _Given user token and submission id - find user submission_

|URL|Method|
|---|------|
|_/submissions/find/:userId/:submissionId_|`GET`|

* **Data Params**

  _User id path parameter has to be in sync with token based user id set in headers_
  
  `GET /submissions/find/1/fib`
  
  ```
  'Accept': 'application/json',
  'X-Authorization': 'Bearer <token>'
  ```

* **Success Response:**

  _List of all submissions_

  * **Code:** 200 <br />
    **Content:** `{"sourceCode":"<source code>",...}`

* **Sample Call:**

  ```bash
  curl --header "Content-Type: application/json" \
       --header "X-Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMSIsInNjb3BlcyI6WyJST0xFX1VTRVIiXSwiaXNzIjoiamFsZ29hcmVuYS5jb20iLCJpYXQiOjE1MzI2ODUyMDUsImV4cCI6MTUzNTI3NzIwNX0.45Ilu0GnQyBVYprAcgtvPHmq5tdvbwiUZucSRAFDDPU2RYY-N8cDoM8k3gl1i2r4FPV7ECZaHgcc20fZwqj_CQ" \
       http://localhost:5004/submissions/find/1/fib
  ```
  
## Running locally

There are two ways to run it - from sources or from binaries.

### Running from binaries
- go to [releases page](https://github.com/spolnik/JAlgoArena-Submissions/releases) and download last app package (JAlgoArena-Submissions-[version_number].zip)
- after unpacking it, go to folder and run `./run.sh` (to make it runnable, invoke command `chmod +x run.sh`)
- you can modify port in run.sh script, depending on your infrastructure settings. The script itself can be found in here: [run.sh](run.sh)

### Running from sources
- run `git clone https://github.com/spolnik/JAlgoArena-Submissions` to clone locally the sources
- now, you can build project with command `./gradlew clean stage` which will create runnable jar package with app sources. Next, run `java -jar build/libs/jalgoarena-auth-*.jar` which will start application
- there is second way to run app with gradle. Instead of running above, you can just run `./gradlew clean bootRun`

## Notes
- [Travis Builds](https://travis-ci.org/spolnik)

![Component Diagram](https://github.com/spolnik/JAlgoArena/raw/master/design/JAlgoArena_Logo.png)
