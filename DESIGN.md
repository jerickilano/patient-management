# Patient Management Microservices – Design Document

## 1. Overview

This project is a small, production-style **microservices** system for managing patients.  
The goal is to practice building an end-to-end backend using modern tools:

The system is intentionally scoped to be small but realistic, focusing on clean service boundaries and integration patterns rather than a large feature set.

---

## 2. Scope

### In Scope

- User authentication and JWT issuance
- Basic patient CRUD (create, read, list)
- Event publishing when a patient is created
- A simple consumer that reacts to those events
- Routing and auth enforcement at the gateway layer
- Running all services locally with Docker Compose

### Out of Scope (for now)

- Full appointment management
- Frontend UI
- Production-grade monitoring, tracing, and scaling
- Real cloud deployment (may be added later using LocalStack or AWS)

---

## 3. Architecture

The system follows a **microservices** architecture with a single entry point:

- **API Gateway (Spring Cloud Gateway)**
  - Exposes public HTTP endpoints
  - Routes requests to internal services
  - Validates JWT tokens on protected routes

- **Auth Service**
  - Manages user registration and login
  - Issues signed **JWT bearer tokens**
  - Stores users and hashed passwords in PostgreSQL

- **Patient Service**
  - Exposes REST APIs for patient CRUD
  - Stores patient records in PostgreSQL
  - Publishes `PATIENT_CREATED` events to Kafka
  - Exposes a small **gRPC** API to demonstrate binary, strongly-typed service-to-service calls

- **Notification Service**
  - Subscribes to `PATIENT_CREATED` events from Kafka
  - Simulates downstream behavior by logging a “welcome notification” for the patient

All services are containerized and run together via `docker-compose.yml`.

---

## 4. Data & Messaging

### Data Model (initial)

**User (Auth Service)**
- `id` (UUID, primary key)
- `email` (unique)
- `password_hash`
- `role` (e.g. `ADMIN`, `STAFF`)
- `created_at`

**Patient (Patient Service)**
- `id` (UUID, primary key)
- `first_name`
- `last_name`
- `date_of_birth`
- `email` (optional)
- `created_at`

### Events

**Topic:** `patient-created`

**Payload (JSON or Avro-like structure, simplified):**
```json
{
  "patientId": "uuid",
  "firstName": "Alice",
  "lastName": "Nguyen",
  "createdAt": "2025-01-01T12:00:00Z"
}
