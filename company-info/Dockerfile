FROM maven:3.8.3-openjdk-17 AS builder
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean install -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app

COPY --from=builder /app/target/company-info-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]