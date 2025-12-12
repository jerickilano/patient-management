---
name: Patient Management Microservices Development Plan
overview: Plan for building a complete microservices system with API Gateway, Auth Service, Patient Service, and Notification Service using Java 21, Spring Boot, PostgreSQL, Kafka, and Docker. Includes security best practices for public repository.
todos:
  - id: setup-project-structure
    content: Create parent POM, multi-module structure, .gitignore, and .env.example template
    status: pending
  - id: setup-docker-infrastructure
    content: Create docker-compose.yml with PostgreSQL, Kafka, Zookeeper, and service networking
    status: pending
    dependencies:
      - setup-project-structure
  - id: implement-auth-service
    content: Build Auth Service with User entity, JWT generation, registration/login endpoints, and PostgreSQL integration
    status: pending
    dependencies:
      - setup-docker-infrastructure
  - id: implement-api-gateway
    content: Build API Gateway with route configuration and JWT validation filter for protected routes
    status: pending
    dependencies:
      - implement-auth-service
  - id: implement-patient-service
    content: Build Patient Service with CRUD endpoints, PostgreSQL integration, Kafka event publishing, and optional gRPC API
    status: pending
    dependencies:
      - setup-docker-infrastructure
  - id: implement-notification-service
    content: Build Notification Service with Kafka consumer for patient-created events
    status: pending
    dependencies:
      - setup-docker-infrastructure
  - id: integration-testing
    content: Test end-to-end flow, update documentation, and verify all environment variables are properly configured
    status: pending
    dependencies:
      - implement-api-gateway
      - implement-patient-service
      - implement-notification-service
---

# Patient Management Microservices Development Plan

## Project Structure

The project will use a **multi-module Maven structure** with separate modules for each service:

```
patient-management/
├── api-gateway/          # Spring Cloud Gateway service
├── auth-service/         # Authentication & JWT service
├── patient-service/      # Patient CRUD + Kafka publisher
├── notification-service/ # Kafka consumer
├── docker-compose.yml    # Infrastructure orchestration
├── .gitignore           # Exclude sensitive files
├── .env.example         # Template for environment variables
└── pom.xml              # Parent POM
```

## Security Considerations (Public Repository)

- **No hardcoded secrets**: All sensitive values (JWT secrets, DB passwords, Kafka config) via environment variables
- **Environment files**: Use `.env` files (gitignored) with `.env.example` as template
- **Secure defaults**: BCrypt for password hashing, secure JWT configuration
- **Docker secrets**: Use environment variables in docker-compose, not hardcoded values
- **.gitignore**: Exclude `.env`, `*.log`, `target/`, IDE files, etc.

## Implementation Phases

Create parent POM, multi-module structure, .gitignore, and .env.example with educational comments
Create docker-compose.yml with PostgreSQL, Kafka, Zookeeper with detailed comments
Build Auth Service with extensive line-by-line educational comments
Build API Gateway with detailed comments explaining routing and JWT validation
Build Patient Service with CRUD, Kafka publishing, and educational comments
Build Notification Service with Kafka consumer and detailed explanations

### Phase 1: Project Setup & Infrastructure

**1.1 Root Project Structure**

- Create parent `pom.xml` with Java 21, Spring Boot 3.x, Spring Cloud dependencies
- Set up multi-module structure with shared parent configuration
- Create `.gitignore` (Java, Maven, Docker, IDE files, `.env`)
- Create `.env.example` with placeholder values for all secrets

**1.2 Docker Infrastructure**

- Create `docker-compose.yml` with:
  - PostgreSQL container (separate databases for auth-service and patient-service)
  - Kafka + Zookeeper containers
  - Network configuration for service communication
- Use environment variables for database credentials
- Add health checks and proper service dependencies

### Phase 2: Auth Service

**2.1 Service Setup**

- Create `auth-service/` module with Spring Boot starter
- Configure PostgreSQL connection (via environment variables)
- Set up Spring Data JPA with User entity

**2.2 User Entity & Repository**

- `User` entity: `id` (UUID), `email` (unique), `passwordHash`, `role`, `createdAt`
- `UserRepository` with `findByEmail` method
- Database migration (Flyway or Liquibase) for schema creation

**2.3 Security Implementation**

- JWT utility class for token generation/validation (secret from env var)
- Password encoder (BCrypt) for hashing
- Spring Security configuration for `/auth/**` endpoints (public)

**2.4 API Endpoints**

- `POST /auth/register`: Validate input, hash password, save user, return success
- `POST /auth/login`: Verify credentials, generate JWT, return token
- Input validation and error handling

**2.5 Application Configuration**

- `application.yml` with database connection (env vars)
- JWT secret and expiration from environment variables
- Service port configuration (e.g., 8081)

### Phase 3: API Gateway

**3.1 Service Setup**

- Create `api-gateway/` module with Spring Cloud Gateway
- Configure service discovery/routing (static routes for local dev)

**3.2 Route Configuration**

- Route `/auth/**` → `http://auth-service:8081`
- Route `/patients/**` → `http://patient-service:8082` (with JWT validation)
- Public route for `/auth/**` (no JWT required)

**3.3 JWT Validation Filter**

- Custom filter to validate JWT tokens on protected routes
- Extract token from `Authorization: Bearer <token>` header
- Validate signature and expiration
- Forward request with user context if valid, reject if invalid

**3.4 Application Configuration**

- Gateway port: 8080 (public entry point)
- Service URLs via environment variables

### Phase 4: Patient Service

**4.1 Service Setup**

- Create `patient-service/` module with Spring Boot
- Configure PostgreSQL connection (separate database)
- Set up Spring Data JPA

**4.2 Patient Entity & Repository**

- `Patient` entity: `id` (UUID), `firstName`, `lastName`, `dateOfBirth`, `email` (optional), `createdAt`
- `PatientRepository` with standard CRUD methods
- Database migration for schema

**4.3 Kafka Producer Setup**

- Add Spring Kafka dependency
- Configure Kafka producer (broker URL from env var)
- Create `PatientEventPublisher` service

**4.4 REST API Endpoints**

- `POST /patients`: Create patient, save to DB, publish `PATIENT_CREATED` event
- `GET /patients/{id}`: Retrieve patient by ID
- `GET /patients`: List patients with pagination (page, size)
- All endpoints require authentication (validated by gateway)

**4.5 gRPC API (Optional but mentioned)**

- Define `.proto` file for `GetPatientSummary` RPC
- Generate gRPC stubs
- Implement gRPC server endpoint
- Configure gRPC server port

**4.6 Event Publishing**

- Publish to `patient-created` Kafka topic with JSON payload:
  ```json
  {
    "patientId": "uuid",
    "firstName": "string",
    "lastName": "string",
    "createdAt": "ISO-8601 timestamp"
  }
  ```


### Phase 5: Notification Service

**5.1 Service Setup**

- Create `notification-service/` module with Spring Boot
- Configure Spring Kafka consumer

**5.2 Kafka Consumer**

- Subscribe to `patient-created` topic
- Create `PatientCreatedListener` with `@KafkaListener`
- Deserialize JSON event payload

**5.3 Notification Logic**

- Log welcome notification: `"Sending welcome notification to patient {id}"`
- Simple implementation (can be extended later for email/SMS)

### Phase 6: Integration & Testing

**6.1 Docker Compose Integration**

- Ensure all services connect to correct databases/Kafka
- Configure service networking and health checks
- Test end-to-end flow: register → login → create patient → verify notification

**6.2 Environment Configuration**

- Document required environment variables in README
- Provide `.env.example` with all placeholders
- Ensure all services read from environment variables

**6.3 Documentation Updates**

- Update README with actual setup instructions
- Add troubleshooting section
- Document API endpoints and request/response examples

## Key Files to Create

### Configuration Files

- `pom.xml` (parent) - [pom.xml](pom.xml)
- `api-gateway/pom.xml` - [api-gateway/pom.xml](api-gateway/pom.xml)
- `auth-service/pom.xml` - [auth-service/pom.xml](auth-service/pom.xml)
- `patient-service/pom.xml` - [patient-service/pom.xml](patient-service/pom.xml)
- `notification-service/pom.xml` - [notification-service/pom.xml](notification-service/pom.xml)
- `docker-compose.yml` - [docker-compose.yml](docker-compose.yml)
- `.gitignore` - [.gitignore](.gitignore)
- `.env.example` - [.env.example](.env.example)

### Auth Service

- `auth-service/src/main/java/.../User.java` - User entity
- `auth-service/src/main/java/.../UserRepository.java` - Repository
- `auth-service/src/main/java/.../AuthController.java` - REST endpoints
- `auth-service/src/main/java/.../JwtUtil.java` - JWT utilities
- `auth-service/src/main/java/.../SecurityConfig.java` - Security configuration
- `auth-service/src/main/resources/application.yml` - Service config

### API Gateway

- `api-gateway/src/main/java/.../JwtAuthenticationFilter.java` - JWT validation
- `api-gateway/src/main/resources/application.yml` - Gateway routes

### Patient Service

- `patient-service/src/main/java/.../Patient.java` - Patient entity
- `patient-service/src/main/java/.../PatientRepository.java` - Repository
- `patient-service/src/main/java/.../PatientController.java` - REST endpoints
- `patient-service/src/main/java/.../PatientEventPublisher.java` - Kafka publisher
- `patient-service/src/main/proto/patient.proto` - gRPC definition (optional)
- `patient-service/src/main/resources/application.yml` - Service config

### Notification Service

- `notification-service/src/main/java/.../PatientCreatedListener.java` - Kafka consumer
- `notification-service/src/main/resources/application.yml` - Service config

## Security Checklist

- [ ] All secrets in environment variables (no hardcoded values)
- [ ] `.env` file in `.gitignore`
- [ ] `.env.example` provided with placeholders
- [ ] JWT secret is strong and configurable
- [ ] Passwords hashed with BCrypt (strength 10+)
- [ ] Database credentials externalized
- [ ] Kafka connection strings from environment
- [ ] No sensitive data in docker-compose.yml (use env vars)
- [ ] Input validation on all endpoints
- [ ] Proper error messages (no stack traces in production)

## Development Order

1. **Setup**: Project structure, parent POM, .gitignore, .env.example
2. **Infrastructure**: docker-compose.yml with PostgreSQL and Kafka
3. **Auth Service**: Complete authentication flow
4. **API Gateway**: Routing and JWT validation
5. **Patient Service**: CRUD + Kafka publishing
6. **Notification Service**: Kafka consumer
7. **Integration**: End-to-end testing and documentation