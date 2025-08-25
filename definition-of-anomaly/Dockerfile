FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/target/definition-of-anomaly-0.0.1-SNAPSHOT.jar .

EXPOSE 8090

CMD  java -jar definition-of-anomaly-0.0.1-SNAPSHOT.jar

