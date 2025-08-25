FROM maven:3.8.3-openjdk-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/api-T-connector-0.0.1-SNAPSHOT.jar .

EXPOSE 8085

CMD java -jar api-T-connector-0.0.1-SNAPSHOT.jar