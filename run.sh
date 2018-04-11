#!/bin/bash
BOOTSTRAP_SERVERS="localhost:9092,localhost:9093,localhost:9094" EUREKA_URL=http://localhost:5000/eureka/ nohup java -jar jalgoarena-submissions-*.jar &
