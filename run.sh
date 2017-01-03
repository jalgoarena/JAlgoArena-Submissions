#!/bin/bash
EUREKA_URL=http://localhost:5000/eureka/
java -Dserver.port=5003 -Djalgoarena.apiGatewayUrl=http://localhost:5001/ -jar jalgoarena-submissions-*.jar
