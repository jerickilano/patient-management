---
name: Patient Management Microservices Development Plan
overview: Plan for building a complete microservices system with API Gateway, Auth Service, and Patient Service using Java 21, Spring Boot, PostgreSQL, and Docker. Includes security best practices for public repository.
todos:
  - id: setup-project-structure
    content: Create parent POM, multi-module structure, .gitignore, and .env.example template
    status: pending
  - id: setup-docker-infrastructure
    content: Create docker-compose.yml with PostgreSQL and service networking
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
    content: Build Patient Service with CRUD endpoints, PostgreSQL integration, and RBAC
    status: pending
    dependencies:
      - setup-docker-infrastructure
  - id: integration-testing
    content: Test end-to-end flow, update documentation, and verify all environment variables are properly configured
    status: pending
    dependencies:
      - implement-api-gateway
      - implement-patient-service
---

# Patient Management Microservices Development Plan

## Project Structure

The project will use a **multi-module Maven structure** with separate modules for each service:

```
patient-management/
├── api-gateway/          # Spring Cloud Gateway service
├── auth-service/         # Authentication & JWT service
├── patient-service/      # Patient CRUD service
├── docker-compose.yml    # Infrastructure orchestration
├── .gitignore           # Exclude sensitive files
└── pom.xml              # Parent POM
```

## Security Considerations (Public Repository)

- **No hardcoded secrets**: All sensitive values (JWT secrets, DB passwords) via environment variables
- **Environment files**: Use `.env` files (gitignored) with `.env.example` as template
- **Secure defaults**: BCrypt for password hashing, secure JWT configuration
- **Docker secrets**: Use environment variables in docker-compose, not hardcoded values
- **.gitignore**: Exclude `.env`, `*.log`, `target/`, IDE files, etc.

## Implementation Phases

Create parent POM, multi-module structure, .gitignore, and .env.example with educational comments
Create docker-compose.yml with PostgreSQL and detailed comments
Build Auth Service with extensive line-by-line educational comments
Build API Gateway with detailed comments explaining routing and JWT validation
Build Patient Service with CRUD, RBAC, and educational comments

### Phase 1: Project Setup & Infrastructure

**1.1 Root Project Structure**

- Create parent `pom.xml` with Java 21, Spring Boot 3.x, Spring Cloud dependencies
- Set up multi-module structure with shared parent configuration
- Create `.gitignore` (Java, Maven, Docker, IDE files, `.env`)
- Create `.env.example` with placeholder values for all secrets

**1.2 Docker Infrastructure**

- Create `docker-compose.yml` with:
  - PostgreSQL container (separate databases for auth-service and patient-service)
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

**3.3 JWT Validation Filter & Identity Propagation**

- Custom filter to validate JWT tokens on protected routes
- Extract token from `Authorization: Bearer <token>` header
- Validate signature and expiration
- Extract user identity from JWT claims (userId, role)
- Forward request with identity headers (`X-User-Id`, `X-User-Role`) to downstream services
- Reject if invalid

**3.4 Application Configuration**

- Gateway port: 8080 (public entry point)
- Service URLs via environment variables

### Phase 4: Patient Service

**4.1 Service Setup**

- Create `patient-service/` module with Spring Boot
- Configure PostgreSQL connection (separate database)
- Set up Spring Data JPA

**4.2 Patient Entity & Repository**

- `Patient` entity: `id` (UUID), `firstName`, `lastName`, `dateOfBirth`, `email` (optional), `phone` (optional), `createdAt`, `createdByUserId` (for auditing)
- `PatientRepository` with standard CRUD methods and custom query methods for filtering
- Database migration for schema

**4.3 REST API Endpoints & RBAC**

- `POST /patients`: Create patient (USER/ADMIN), save to DB, use `X-User-Id` for auditing
- `GET /patients/{id}`: Retrieve patient by ID (USER/ADMIN)
- `GET /patients`: List patients with pagination, sorting, and filtering (USER/ADMIN)
  - Query params: `page`, `size`, `sort=field,order`, `lastName=value`, `firstName=value`, etc.
- `PUT /patients/{id}`: Update patient (ADMIN only)
- `DELETE /patients/{id}`: Delete patient (ADMIN only)
- All endpoints require authentication (validated by gateway)
- RBAC enforcement using Spring Security `@PreAuthorize` annotations
- Read identity from `X-User-Id` and `X-User-Role` headers forwarded by gateway

### Phase 5: Integration & Testing

**5.1 Docker Compose Integration**

- Ensure all services connect to correct databases
- Configure service networking and health checks
- Test end-to-end flow: register → login → create patient

**5.2 Environment Configuration**

- Document required environment variables in README
- Provide `.env.example` with all placeholders
- Ensure all services read from environment variables

**5.3 Error Handling & Documentation**

- Implement `@ControllerAdvice` for consistent error responses across all services
- Error response format: `{timestamp, path, errorCode, message}`
- Update README with actual setup instructions
- Add troubleshooting section
- Document API endpoints and request/response examples
- Document RBAC rules and identity propagation

## Key Files to Create

### Configuration Files

- `pom.xml` (parent) - [pom.xml](pom.xml)
- `api-gateway/pom.xml` - [api-gateway/pom.xml](api-gateway/pom.xml)
- `auth-service/pom.xml` - [auth-service/pom.xml](auth-service/pom.xml)
- `patient-service/pom.xml` - [patient-service/pom.xml](patient-service/pom.xml)
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
- `patient-service/src/main/proto/patient.proto` - gRPC definition (optional)
- `patient-service/src/main/resources/application.yml` - Service config


## Security Checklist

- [ ] All secrets in environment variables (no hardcoded values)
- [ ] `.env` file in `.gitignore`
- [ ] `.env.example` provided with placeholders
- [ ] JWT secret is strong and configurable
- [ ] Passwords hashed with BCrypt (strength 10+)
- [ ] Database credentials externalized
- [ ] No sensitive data in docker-compose.yml (use env vars)
- [ ] Input validation on all endpoints
- [ ] Proper error messages (no stack traces in production)

## Development Order

1. **Setup**: Project structure, parent POM, .gitignore, .env.example
2. **Infrastructure**: docker-compose.yml with PostgreSQL
3. **Auth Service**: Complete authentication flow
4. **API Gateway**: Routing and JWT validation
5. **Patient Service**: CRUD with RBAC, pagination, sorting, filtering
6. **Integration**: End-to-end testing and documentation