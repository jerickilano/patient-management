# Patient Management Microservices – Design Document

## 1. Overview

This project is a small, production-style **microservices** system for managing patients.  
The goal is to practice building an end-to-end backend using modern tools:

The system is intentionally scoped to be small but realistic, focusing on clean service boundaries and integration patterns rather than a large feature set.

---

## 2. Scope

### In Scope

- User authentication and JWT issuance
- Patient CRUD with pagination, sorting, and filtering
- Role-Based Access Control (RBAC): USER can create/read, ADMIN can full CRUD
- Identity propagation from gateway to services via headers (X-User-Id, X-User-Role)
- Routing and auth enforcement at the gateway layer
- Consistent error handling with @ControllerAdvice
- Running all services locally with Docker Compose

### Out of Scope (for now)

- Full appointment management
- Frontend UI
- Production-grade monitoring, tracing, and scaling
- Real cloud deployment (may be added later using LocalStack or AWS)

---

## 3. Architecture

### Development
![Spring Boot Architecture](/img/Development.png)
### Spring Boot
![Spring Boot Architecture](/img/Architecture.png)

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
  - Exposes REST APIs for patient CRUD with pagination, sorting, and filtering
  - Enforces RBAC rules (USER: create/read, ADMIN: full CRUD)
  - Stores patient records in PostgreSQL with audit fields (createdByUserId)
  - Reads identity from headers forwarded by gateway (X-User-Id, X-User-Role)
  - Consistent error handling with @ControllerAdvice

All services are containerized and run together via `docker-compose.yml`.

### Deployment
![Deployment Architecture](/img/Deployment.png)

---

## 4. Data Model

### Data Model

**User (Auth Service)**
- `id` (UUID, primary key)
- `email` (unique)
- `password_hash`
- `role` (e.g. `USER`, `ADMIN`)
- `created_at`

**Patient (Patient Service)**
- `id` (UUID, primary key)
- `first_name`
- `last_name`
- `date_of_birth`
- `email` (optional)
- `phone` (optional)
- `created_by_user_id` (for auditing - from X-User-Id header)
- `created_at`
