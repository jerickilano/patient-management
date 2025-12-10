# Patient Management Microservices (Java + Spring Boot)

A small, production-style **microservices** system for managing patients, built from scratch as a learning project.

It demonstrates:
- Java 21, Spring Boot, Spring Cloud Gateway
- REST + gRPC service-to-service communication
- JWT-based authentication
- PostgreSQL for data storage
- Kafka for event-driven communication
- Docker + docker-compose to run everything locally

---

## Architecture Overview

The system is composed of four services:

- **API Gateway** – entry point for all client traffic, routes to internal services and validates JWT tokens.
- **Auth Service** – handles user registration/login and issues JWT bearer tokens.
- **Patient Service** – CRUD operations for patients, stores data in PostgreSQL, and publishes `PATIENT_CREATED` events to Kafka.
- **Notification Service** – consumes Kafka events and logs a “welcome notification” (simulating email/SMS).

High-level flow:

1. Client registers/logs in via **Auth Service** and receives a JWT.
2. Client calls **API Gateway** with the JWT in the `Authorization: Bearer <token>` header.
3. Gateway forwards the request to **Patient Service**.
4. Patient Service writes to **PostgreSQL** and publishes a `PATIENT_CREATED` event to **Kafka**.
5. **Notification Service** consumes the event and logs a notification.

---

## Tech Stack

- **Language:** Java 21
- **Framework:** Spring Boot, Spring Web, Spring Security, Spring Data JPA
- **API Gateway:** Spring Cloud Gateway
- **Data Storage:** PostgreSQL (Docker container)
- **Messaging:** Apache Kafka (Docker container)
- **Auth:** JWT bearer tokens
- **Service-to-Service:** REST + gRPC
- **Containerization:** Docker, docker-compose
