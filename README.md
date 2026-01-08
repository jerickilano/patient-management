# Patient Management Platform (Java + Spring Boot)

A small, **production-style backend system** for managing patients, built to demonstrate strong backend fundamentals with clean architecture, security, and deployment practices.

---

## What This Project Demonstrates (Resume-Ready)

- **Java 21**, **Spring Boot**, **Spring Security**
- **REST APIs** with validation, pagination, sorting, filtering
- **JWT Authentication + Role-Based Authorization (RBAC)**
- **API Gateway** (Spring Cloud Gateway) for routing + auth enforcement
- **PostgreSQL** with **Spring Data JPA**
- **Docker + docker-compose** for one-command local boot
- **Testing** (unit + integration) and API documentation (OpenAPI/Swagger)

---

## Architecture Overview

This system is intentionally scoped to “small but real”:

### Services

1) **API Gateway**
- Single entry point for clients
- Routes:
  - `/auth/**` → Auth Service
  - `/patients/**` → Patient Service
- Validates JWT on protected routes
- Extracts identity from JWT and forwards to downstream services via headers

2) **Auth Service**
- Handles user registration + login
- Hashes passwords (BCrypt)
- Issues JWTs with claims (userId, role, expiration)

3) **Patient Service**
- Patient CRUD + pagination/sorting/filtering
- Enforces authorization rules (RBAC)
- Persists patients to PostgreSQL using JPA
- Uses identity headers from gateway (e.g., `X-User-Id`) for auditing

---

## High-Level Request Flow

1) **Register**  
   Client → Gateway → Auth Service → stores user in Postgres  
2) **Login**  
   Client → Gateway → Auth Service → returns `JWT`  
3) **Create Patient**  
   Client → Gateway validates JWT → forwards to Patient Service  
   Gateway adds identity headers (e.g., `X-User-Id`, `X-User-Role`)  
4) **Patient Service**
   - validates request body
   - enforces RBAC rules
   - persists patient to Postgres
   - returns created patient response

---

## Tech Stack

- **Language:** Java 21
- **Framework:** Spring Boot, Spring Security
- **Gateway:** Spring Cloud Gateway
- **Database:** PostgreSQL
- **ORM:** Spring Data JPA
- **Auth:** JWT (Bearer token), BCrypt password hashing
- **Testing:** JUnit 5, Spring Boot Test
- **Deployment:** Docker + docker-compose

---

## API Endpoints

### Auth Service (via Gateway)

**POST `/auth/register`**
- Creates a user account
- Stores hashed password
- Optionally returns a JWT (or requires login separately)

**POST `/auth/login`**
- Validates credentials
- Returns JWT

**GET `/auth/health`**
- Simple health endpoint

### Patient Service (protected via Gateway)

**POST `/patients`**
- Create a patient
- Requires valid JWT
- Example rules:
  - USER: can create and read
  - ADMIN: can create/read/update/delete

**GET `/patients/{id}`**
- Get patient by id

**GET `/patients`**
- List patients with pagination + sorting + filtering
- Example query params:
  - `page`, `size`
  - `sort=createdAt,desc`
  - `lastName=Smith` (filter)

**PUT `/patients/{id}`**
- Update patient (ADMIN only)

**DELETE `/patients/{id}`**
- Delete patient (ADMIN only)

---

## Security Model

### Authentication (AuthN)
- JWT is passed as: `Authorization: Bearer <token>`
- Token includes:
  - `sub` (userId)
  - `role` (USER/ADMIN)
  - `iat`, `exp`

### Authorization (AuthZ / RBAC)
- Patient Service enforces roles:
  - USER: read/create only
  - ADMIN: full CRUD
- You can implement this with:
  - Spring Security annotations (`@PreAuthorize`)
  - or custom filter that reads `X-User-Role` header

### Identity Propagation (Gateway → Services)
After validating the JWT, Gateway forwards identity to internal services:
- `X-User-Id: <uuid-or-id>`
- `X-User-Role: USER|ADMIN`

This proves you understand how real systems pass identity through layers without re-validating everywhere.

---

## Data Model (Example)

### User (Auth DB)
- `id`
- `email` (unique)
- `passwordHash`
- `role` (USER/ADMIN)
- `createdAt`

### Patient (Patient DB)
- `id`
- `firstName`
- `lastName`
- `dob`
- `phone`
- `email`
- `createdAt`
- `createdByUserId` (from `X-User-Id`) ✅

---

## Error Handling (Professional API Behavior)

All services return consistent error responses using `@ControllerAdvice`.

Example error body:
```json
{
  "timestamp": "2026-01-05T18:32:11Z",
  "path": "/patients",
  "errorCode": "VALIDATION_ERROR",
  "message": "lastName must not be blank"
}
