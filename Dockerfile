FROM openjdk:18-ea-11-jdk-alpine3.15
MAINTAINER Will Spires
RUN apk upgrade --no-cache
COPY target/epona-bot-0.0.14-SNAPSHOT.jar epona-bot.jar
COPY questions.txt questions.txt
ENTRYPOINT ["java","-jar","/epona-bot.jar"]