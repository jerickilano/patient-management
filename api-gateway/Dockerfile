# ============================================================================
# DEVPLAN PHASE 3.1: API GATEWAY SERVICE SETUP
# ============================================================================
# This file is part of Phase 3.1 - Service Setup from DEVPLAN.md
# Dockerfile for building the API Gateway Docker image
# ============================================================================
#
# API GATEWAY DOCKERFILE
# Similar to Auth Service Dockerfile - multi-stage build

FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY ../pom.xml ../pom.xml

COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
