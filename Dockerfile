FROM eclipse-temurin:17-jre-alpine

ARG JAR_FILE=target/HomeworkBot*.jar

WORKDIR /opt/app

COPY ${JAR_FILE} homework_bot.jar

ENTRYPOINT ["java","-jar","homework_bot.jar"]
