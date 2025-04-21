FROM maven:3.8.3-openjdk-17 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src
COPY .env .

RUN mvn dependency:go-offline -B

RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app

COPY .env .

COPY --from=build /app/target/api-T-connector-0.0.1-SNAPSHOT.jar .

EXPOSE 8085

# Установка переменных окружения
CMD set -a && . ./.env && set +a && java -jar api-T-connector-0.0.1-SNAPSHOT.jar

