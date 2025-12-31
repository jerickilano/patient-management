# ============================================================================
# DEVPLAN PHASE 4.1: PATIENT SERVICE SETUP
# ============================================================================
# This file is part of Phase 4.1 - Service Setup from DEVPLAN.md
# Dockerfile for building the Patient Service Docker image
# ============================================================================
#
# PATIENT SERVICE DOCKERFILE
# Multi-stage build (same pattern as Auth Service)

FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY ../pom.xml ../pom.xml

COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
