# JAlgoArena Submissions [![Build Status](https://travis-ci.org/spolnik/JAlgoArena-Submissions.svg?branch=master)](https://travis-ci.org/spolnik/JAlgoArena-Submissions) [![codecov](https://codecov.io/gh/spolnik/JAlgoArena-Submissions/branch/master/graph/badge.svg)](https://codecov.io/gh/spolnik/JAlgoArena-Submissions) [![GitHub release](https://img.shields.io/github/release/spolnik/jalgoarena-submissions.svg)]()

JAlgoArena Submissions is service dedicated for collecting users submissions and exposing that data. Querying submissions and submitting it has to be secure operation - methods require passing token which is then checked with Auth service.

- [Introduction](#introduction)
- [REST API](#rest-api)
- [Components](#components)
- [Continuous Delivery](#continuous-delivery)
- [Infrastructure](#infrastructure)
- [Running Locally](#running-locally)
- [Notes](#notes)

## Introduction

- JAlgoArena Submissions exposes Submissions data via REST API

![Component Diagram](https://github.com/spolnik/JAlgoArena-Submissions/raw/master/design/component_diagram.png)

# REST API

| Endpoint | Description |
| ---- | --------------- |
| [GET /submissions/:userId] | Get all user submissions (only for logged in user) |

## Components

- [JAlgoArena](https://github.com/spolnik/JAlgoArena)
- [JAlgoArena UI](https://github.com/spolnik/JAlgoArena-UI)
- [JAlgoArena Auth Server](https://github.com/spolnik/JAlgoArena-Auth)
- [JAlgoArena API Gateway](https://github.com/spolnik/JAlgoArena-API)

## Continuous Delivery

- initially, developer push his changes to GitHub
- in next stage, GitHub notifies Travis CI about changes
- Travis CI runs whole continuous integration flow, running compilation, tests and generating reports
- coverage report is sent to Codecov

## Infrastructure

- Xodus (embedded highly scalable database) - http://jetbrains.github.io/xodus/
- Spring Boot, Spring Cloud
- TravisCI - https://travis-ci.org/spolnik/JAlgoArena-Submissions
- Apache Kafka

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
