FROM maven:3.9.6-sapmachine-21 AS MAVEN_BUILD

COPY pom.xml /build/
COPY src /build/src/

WORKDIR /build/
RUN mvn clean package

FROM openjdk:21-slim

WORKDIR /app

COPY --from=MAVEN_BUILD /build/target/*.jar /app/app.jar
COPY data /app/data

ENTRYPOINT ["java", "-jar", "app.jar"]
