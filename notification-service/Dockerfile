# ============================================================================
# DEVPLAN PHASE 5.1: NOTIFICATION SERVICE SETUP
# ============================================================================
# This file is part of Phase 5.1 - Service Setup from DEVPLAN.md
# Dockerfile for building the Notification Service Docker image
# ============================================================================
#
# NOTIFICATION SERVICE DOCKERFILE
# Multi-stage build (same pattern as other services)

FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY ../pom.xml ../pom.xml

COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Note: No EXPOSE needed - this service doesn't expose HTTP endpoints
# It only consumes Kafka events

ENTRYPOINT ["java", "-jar", "app.jar"]
