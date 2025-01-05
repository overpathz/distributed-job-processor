FROM maven:3.9.5-eclipse-temurin-21 AS build-env
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package -DskipTests

FROM openjdk:21-slim
VOLUME /tmp

COPY --from=build-env /app/target/distributed-job-processor-0.0.1.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
