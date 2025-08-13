FROM maven:3.9-amazoncorretto-21 AS build

COPY pom.xml .
COPY src ./src

RUN mvn clean package

FROM openjdk:21-jdk-slim
COPY --from=build /target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]