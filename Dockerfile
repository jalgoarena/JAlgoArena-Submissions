FROM openjdk:8

MAINTAINER Jacek Spolnik <jacek.spolnik@gmail.com>

WORKDIR /app
ADD build/libs/jalgoarena-submissions-*.jar /app
RUN mkdir /app/SubmissionsStore
VOLUME /app/SubmissionsStore

EXPOSE 5004

CMD java -jar /app/jalgoarena-submissions-*.jar