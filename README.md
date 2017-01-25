# JAlgoArena Submissions [![Build Status](https://travis-ci.org/spolnik/JAlgoArena-Submissions.svg?branch=master)](https://travis-ci.org/spolnik/JAlgoArena-Submissions) [![codecov](https://codecov.io/gh/spolnik/JAlgoArena-Submissions/branch/master/graph/badge.svg)](https://codecov.io/gh/spolnik/JAlgoArena-Submissions) [![GitHub release](https://img.shields.io/github/release/spolnik/jalgoarena-submissions.svg)]()

JAlgoArena Submissions is service dedicated for collecting users submissions and exposing that data together with calculating ranking for all submissions as well as for problem based rankings. Querying submissions and submitting it has to be secure operation - methods require passing token which is then checked with Auth service.

Demo: https://jalgoarena-ui.herokuapp.com/

- [Introduction](#introduction)
- [REST API](#rest-api)
- [Components](#components)
- [Continuous Delivery](#continuous-delivery)
- [Infrastructure](#infrastructure)
- [Running Locally](#running-locally)
- [Notes](#notes)

## Introduction

- JAlgoArena Submissions consists two parts, CRUD operations for Submissions and exposing two calculated rankings - all submissions ranking and problem rankings

![Component Diagram](https://github.com/spolnik/JAlgoArena-Submissions/raw/master/design/component_diagram.png)

# REST API

| Endpoint | Description |
| ---- | --------------- |
| [GET /ranking](https://jalgoarena-submissions.herokuapp.com/ranking) | Get general ranking list |
| [GET /ranking/:problemId](https://jalgoarena-submissions.herokuapp.com/ranking/fib) | Get ranking for particular problem |
| [GET /submissions](https://jalgoarena-submissions.herokuapp.com/submissions) | Get all submissions list (only for ADMIN) |
| [PUT /submissions](https://jalgoarena-submissions.herokuapp.com/submissions) | Put new submission (only for logged in user) |
| [DELETE /submissions/:submissionsId](https://jalgoarena-submissions.herokuapp.com/submissions/0-0) | Delete submission by submission id (only for ADMIN) |
| [GET /submissions/:userId](https://jalgoarena-submissions.herokuapp.com/submissions/0-1) | Get all user submissions (only for logged in user) |
| [GET /submissions/solved-ratio](https://jalgoarena-submissions.herokuapp.com/submissions/solved-ratio) | Get all problems solved by users ratio |

## Components

- [JAlgoArena](https://github.com/spolnik/JAlgoArena)
- [JAlgoArena UI](https://github.com/spolnik/JAlgoArena-UI)
- [JAlgoArena Auth Server](https://github.com/spolnik/JAlgoArena-Auth)
- [JAlgoArena Eureka Server](https://github.com/spolnik/JAlgoArena-Eureka)
- [JAlgoArena API Gateway](https://github.com/spolnik/JAlgoArena-API)

## Continuous Delivery

- initially, developer push his changes to GitHub
- in next stage, GitHub notifies Travis CI about changes
- Travis CI runs whole continuous integration flow, running compilation, tests and generating reports
- coverage report is sent to Codecov
- application is deployed into Heroku machine

## Infrastructure

- Heroku (PaaS)
- Xodus (embedded highly scalable database) - http://jetbrains.github.io/xodus/
- Spring Boot, Spring Cloud (Eureka Client)
- TravisCI - https://travis-ci.org/spolnik/JAlgoArena-Submissions

## Running locally

There are two ways to run it - from sources or from binaries.
- Default port: `5003`

### Running from binaries
- go to [releases page](https://github.com/spolnik/JAlgoArena-Submissions/releases) and download last app package (JAlgoArena-Submissions-[version_number].zip)
- after unpacking it, go to folder and run `./run.sh` (to make it runnable, invoke command `chmod +x run.sh`)
- you can modify port and Eureka service url in run.sh script, depending on your infrastructure settings. The script itself can be found in here: [run.sh](run.sh)

### Running from sources
- run `git clone https://github.com/spolnik/JAlgoArena-Submissions` to clone locally the sources
- now, you can build project with command `./gradlew clean bootRepackage` which will create runnable jar package with app sources. Next, run `java -Dserver.port=5003 -jar build/libs/jalgoarena-auth-*.jar` which will start application
- there is second way to run app with gradle. Instead of running above, you can just run `./gradlew clean bootRun`

## Notes
- [Travis Builds](https://travis-ci.org/spolnik)

![Component Diagram](https://github.com/spolnik/JAlgoArena/raw/master/design/JAlgoArena_Logo.png)
