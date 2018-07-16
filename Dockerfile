FROM openjdk:8-jre-alpine

MAINTAINER Jacek Spolnik <jacek.spolnik@gmail.com>

WORKDIR /app
COPY build/libs/jalgoarena-submissions-*.jar /app

VOLUME /app/SubmissionsStore

EXPOSE 5004

CMD java -XX:+PrintFlagsFinal $JAVA_OPTS -jar /app/jalgoarena-submissions-*.jar