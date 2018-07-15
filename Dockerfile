FROM openjdk:8-jre-alpine

MAINTAINER Jacek Spolnik <jacek.spolnik@gmail.com>

WORKDIR /app
COPY build/libs/jalgoarena-submissions-*.jar /app
RUN mkdir /app/SubmissionsStore
VOLUME /app/SubmissionsStore

EXPOSE 5004

CMD ["/usr/bin/java", "-jar", "/app/jalgoarena-submissions-*.jar"]