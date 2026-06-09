FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml .
COPY common-kafka common-kafka
COPY payment-service payment-service
COPY ledger-service ledger-service
COPY fraud-service fraud-service

RUN mvn -pl ledger-service -am -DskipTests package -Dmaven.repo.local=/root/.m2/repository

FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache curl
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app
COPY --from=build /workspace/ledger-service/target/ledger-service-1.0.0.jar app.jar
USER app
ENTRYPOINT ["java","-jar","app.jar"]
