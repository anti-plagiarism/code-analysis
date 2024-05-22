FROM openjdk:21-jdk-slim

COPY ./build/libs/code-analysis-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8085

CMD ["java", "-jar", "app.jar"]
