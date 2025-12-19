# ============================================================================
# DEVPLAN PHASE 2.1: AUTH SERVICE SETUP
# ============================================================================
# This file is part of Phase 2.1 - Service Setup from DEVPLAN.md
# Dockerfile for building the Auth Service Docker image
# ============================================================================
#
# Dockerfile - Instructions for Building Docker Image
#
# WHAT IS A DOCKERFILE?
# A Dockerfile contains instructions for building a Docker image.
# A Docker image is a snapshot of your application with all its dependencies.
# A Docker container is a running instance of an image.
#
# BUILD PROCESS:
# 1. Docker reads this Dockerfile
# 2. Executes each instruction in order
# 3. Creates layers (each instruction = one layer)
# 4. Results in a Docker image
#
# RUNNING:
# docker build -t auth-service .
# docker run -p 8081:8081 auth-service

# ============================================
# STAGE 1: BUILD (Multi-stage build)
# ============================================
# Multi-stage builds allow us to:
# - Use a full JDK for building (compiling, running tests, etc.)
# - Use a smaller JRE-only image for running (smaller final image)
# - Keep build tools out of production image

# BASE IMAGE: Use Maven with OpenJDK 21 for building
# This image contains:
# - Java 21 JDK (Java Development Kit - includes compiler)
# - Maven (build tool)
# - All tools needed to compile and package the application
FROM maven:3.9-eclipse-temurin-21 AS build

# WORKDIR: Set the working directory inside the container
# All subsequent commands run in this directory
WORKDIR /app

# COPY POM FILES: Copy dependency files first
# Why copy POM files separately?
# Docker caches layers. If we copy POM files first and they don't change,
# Docker can reuse the cached layer (faster builds).
# If we copy everything together, any code change invalidates the cache.
COPY pom.xml .
COPY ../pom.xml ../pom.xml  # Copy parent POM

# COPY SOURCE CODE: Copy the application source code
COPY src ./src

# RUN MAVEN: Build the application
# - mvn clean: Remove previous build artifacts
# - mvn package: Compile, test, and package into JAR
# - -DskipTests: Skip tests (optional - remove this to run tests)
# The result is a JAR file in target/auth-service-1.0.0-SNAPSHOT.jar
RUN mvn clean package -DskipTests

# ============================================
# STAGE 2: RUNTIME (Production image)
# ============================================
# Use a smaller image with only JRE (Java Runtime Environment)
# No need for JDK, Maven, or build tools in production

# BASE IMAGE: Use Eclipse Temurin JRE 21 (smaller than JDK)
# Eclipse Temurin is an OpenJDK distribution
FROM eclipse-temurin:21-jre-alpine

# WHY ALPINE?
# Alpine Linux is a minimal Linux distribution (only ~5MB).
# This makes the Docker image much smaller.
# Trade-off: Some libraries might not be available, but for Java apps it's usually fine.

# WORKDIR: Set working directory in the container
WORKDIR /app

# COPY JAR: Copy the built JAR file from the build stage
# --from=build: Copy from the "build" stage (the previous FROM statement)
# target/*.jar: The JAR file created by Maven
# app.jar: Rename it to app.jar (simpler name)
COPY --from=build /app/target/*.jar app.jar

# EXPOSE PORT: Document which port the application uses
# This doesn't actually publish the port - it's just documentation.
# The port is published in docker-compose.yml with "ports: - 8081:8081"
EXPOSE 8081

# ENTRYPOINT: Command to run when container starts
# java -jar app.jar: Run the Spring Boot application
# Spring Boot's embedded server (Tomcat) will start and listen on port 8081
ENTRYPOINT ["java", "-jar", "app.jar"]

# ============================================
# NOTES:
# ============================================
# - The final image only contains:
#   - Alpine Linux base
#   - Java 21 JRE
#   - Our application JAR
# - No source code, no build tools, no Maven
# - Much smaller and more secure than including everything
# - Typical size: ~200-300MB (vs 500MB+ with full JDK)
