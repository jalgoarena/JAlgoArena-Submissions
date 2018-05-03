#!/usr/bin/env bash
pm2 stop submissions
pm2 delete submissions
./gradlew clean
./gradlew stage
pm2 start pm2.config.js