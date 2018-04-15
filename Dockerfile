FROM openjdk:8

WORKDIR /app
ADD build/libs/jalgoarena-submissions-*.jar /app
RUN mkdir /app/SubmissionsStore
VOLUME /app/SubmissionsStore

ENV EUREKA_URL=http://eureka:5000/eureka
ENV BOOTSTRAP_SERVERS=kafka1:9092,kafka2:9093,kafka3:9094
EXPOSE 5004

CMD java -Dserver.port=5004 -jar /app/jalgoarena-submissions-*.jar