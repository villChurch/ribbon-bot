FROM openjdk:18-ea-11-jdk-alpine3.15
MAINTAINER Will Spires
COPY target/epona-bot-0.0.6-SNAPSHOT.jar epona-bot.jar
ENTRYPOINT ["java","-jar","/epona-bot.jar"]