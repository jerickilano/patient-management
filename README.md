# Patient Management Microservices (Java + Spring Boot)

A small, production-style **microservices** system for managing patients, built from scratch as a learning project.

Demonstrates:
- Java 21, Spring Boot, Spring Cloud Gateway
- REST + gRPC service-to-service communication
- JWT-based authentication
- PostgreSQL for data storage
- Kafka for event-driven communication
- Docker + docker-compose to run everything locally

Learning Points:

1. Designing a small but realistic microservices architecture (gateway + auth + domain + consumer).
2. Implementing JWT-based authentication and securing APIs.
3. Using Kafka for asynchronous, event-driven communication between services.
4. Exposing both REST and gRPC interfaces for service-to-service communication.
5. Containerizing services and wiring them together with Docker Compose for local development.

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

---

## Services

### API Gateway
- Routes `/auth/**` to Auth Service and `/patients/**` to Patient Service.
- Validates JWT tokens on protected routes.

### Auth Service
- Endpoints:
  - `POST /auth/register` – create new user account
  - `POST /auth/login` – verify credentials and return JWT
- Stores users and hashed passwords in PostgreSQL.

### Patient Service
- Endpoints (behind gateway):
  - `POST /patients` – create patient
  - `GET /patients/{id}` – get patient by id
  - `GET /patients` – list patients (basic pagination)
- Publishes `PATIENT_CREATED` messages to Kafka.
- Exposes a small **gRPC API** for internal calls (e.g. `GetPatientSummary`).

### Notification Service
- Kafka consumer for `PATIENT_CREATED`.
- Logs a simple message like:  
  `Sending welcome notification to patient {id}`

---

## Running Locally

### Prerequisites
- Java 21
- Docker + Docker Desktop
- Git

### Steps

```bash
# Clone the repo
git clone https://github.com/<your-username>/patient-microservices.git
cd patient-microservices

# Start infrastructure + services
docker compose up --build

---

### Register User
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'

### Login and get JWT
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'

### Create Patient
curl -X POST http://localhost:8080/patients \
  -H "Authorization: Bearer <jwt-from-login>" \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Alice","lastName":"Nguyen","dateOfBirth":"1995-06-01"}'

---
