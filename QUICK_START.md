# Quick Start Guide

## 🚀 Fastest Way to Run the Application

### 1. Create Environment File

Create a `.env` file in the project root:

```bash
# Generate a strong JWT secret (32+ characters)
# On Mac/Linux: openssl rand -base64 32
# Or use any random string at least 32 characters long

JWT_SECRET=your-super-secret-jwt-key-change-this-minimum-32-characters-long

# Database passwords (change these!)
POSTGRES_AUTH_PASSWORD=auth_password_123
POSTGRES_PATIENT_PASSWORD=patient_password_123

# Everything else can use defaults
```

**Minimum `.env` file (copy-paste ready):**

```bash
JWT_SECRET=change-this-to-a-random-32-character-string-minimum
POSTGRES_AUTH_PASSWORD=change-this-password
POSTGRES_PATIENT_PASSWORD=change-this-password
```

### 2. Start Everything

```bash
docker compose up --build
```

Wait 1-2 minutes for all services to start. You'll see logs from all services.

### 3. Test It Works

Open a new terminal and run:

```bash
# 1. Register a user
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'

# Copy the "token" from the response, then:

# 2. Create a patient (replace YOUR_TOKEN with the token from step 1)
curl -X POST http://localhost:8080/patients \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Alice","lastName":"Nguyen","dateOfBirth":"1995-06-01"}'
```

### 4. Check Notification Service Logs

```bash
docker compose logs notification-service | tail -20
```

You should see the welcome notification message!

## 🛑 Stop the Application

```bash
# Stop all services
docker compose down

# Stop and remove data (fresh start)
docker compose down -v
```

## ❓ Common Issues

**"Port already in use"**
- Stop other applications using ports 8080, 8081, 8082, 5433, 5434, 9092

**"Connection refused"**
- Wait longer (services take 1-2 minutes to fully start)
- Check: `docker compose ps` (all should be "Up")

**"JWT validation failed"**
- Make sure JWT_SECRET in `.env` is the same for all services
- Restart: `docker compose restart`

**Services keep restarting**
- Check logs: `docker compose logs <service-name>`
- Usually a database connection issue - wait for databases to be ready


-----------------------------------
## 📚 More Details!!!

## Running Locally

### Prerequisites

Before running the application, ensure you have:

1. **Docker Desktop** installed and running
   - Download from: https://www.docker.com/products/docker-desktop
   - Verify: `docker --version` and `docker compose version`

2. **Java 21** (optional - only needed if building locally without Docker)
   - Verify: `java -version`

3. **Git** (for cloning the repository)
   - Verify: `git --version`

### Quick Start (Recommended)

#### Step 1: Set Up Environment Variables

Create a `.env` file in the project root (copy from `.env.example` if it exists, or create manually):

```bash
# Copy the example file (if it exists)
cp .env.example .env

# Or create .env manually with these variables:
```

**Minimum required `.env` file:**

```bash
# JWT Configuration
JWT_SECRET=your-super-secret-jwt-key-change-this-minimum-32-characters-long-for-security

# Database Configuration (Auth Service)
POSTGRES_AUTH_HOST=postgres-auth
POSTGRES_AUTH_PORT=5432
POSTGRES_AUTH_DB=auth_db
POSTGRES_AUTH_USER=auth_user
POSTGRES_AUTH_PASSWORD=change-this-password

# Database Configuration (Patient Service)
POSTGRES_PATIENT_HOST=postgres-patient
POSTGRES_PATIENT_PORT=5432
POSTGRES_PATIENT_DB=patient_db
POSTGRES_PATIENT_USER=patient_user
POSTGRES_PATIENT_PASSWORD=change-this-password

# Kafka Configuration
KAFKA_BOOTSTRAP_SERVERS=kafka:9092

# Service URLs
AUTH_SERVICE_URL=http://auth-service:8081
PATIENT_SERVICE_URL=http://patient-service:8082

# Ports
GATEWAY_PORT=8080
AUTH_SERVICE_PORT=8081
PATIENT_SERVICE_PORT=8082
```

**⚠️ Important:** Change the passwords and JWT secret in production!

#### Step 2: Start All Services

From the project root directory, run:

```bash
# Build and start all services (this may take a few minutes the first time)
docker compose up --build

# Or run in detached mode (background)
docker compose up --build -d
```

**What this does:**
- Builds Docker images for all 4 services (API Gateway, Auth Service, Patient Service, Notification Service)
- Starts PostgreSQL databases (2 separate databases)
- Starts Kafka and Zookeeper
- Starts all microservices
- Sets up networking between services

**Expected output:** You should see logs from all services. Wait until you see messages like:
- `Started AuthServiceApplication`
- `Started GatewayApplication`
- `Started PatientServiceApplication`
- `Started NotificationServiceApplication`

#### Step 3: Verify Services Are Running

In a new terminal, check if all containers are running:

```bash
docker compose ps
```

You should see 7 containers running:
- `postgres-auth`
- `postgres-patient`
- `zookeeper`
- `kafka`
- `api-gateway`
- `auth-service`
- `patient-service`
- `notification-service`

Check service health:

```bash
# Check API Gateway (should return "Auth Service is running" or similar)
curl http://localhost:8080/auth/health

# Check Auth Service directly
curl http://localhost:8081/auth/health

# Check Patient Service directly
curl http://localhost:8082/patients/health
```

### Testing the Application

#### Step 1: Register a New User

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "message": "Registration successful"
}
```

**Save the token** from the response - you'll need it for the next steps!

#### Step 2: Login (Alternative to Registration)

If you already have an account:

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "message": "Login successful"
}
```

#### Step 3: Create a Patient (Requires Authentication)

Replace `<YOUR_JWT_TOKEN>` with the token from Step 1 or 2:

```bash
curl -X POST http://localhost:8080/patients \
  -H "Authorization: Bearer <YOUR_JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Alice",
    "lastName": "Nguyen",
    "dateOfBirth": "1995-06-01",
    "email": "alice@example.com"
  }'
```

**Expected Response:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "firstName": "Alice",
  "lastName": "Nguyen",
  "dateOfBirth": "1995-06-01",
  "email": "alice@example.com",
  "createdAt": "2025-01-01T12:00:00"
}
```

**Check Notification Service logs** - you should see:
```
============================================================
NOTIFICATION SERVICE - PATIENT CREATED EVENT
============================================================
Sending welcome notification to patient 550e8400... (Alice Nguyen)
Action: Sending welcome email and SMS
Status: Notification queued for delivery
============================================================
```

#### Step 4: Get Patient by ID

```bash
curl -X GET http://localhost:8080/patients/<PATIENT_ID> \
  -H "Authorization: Bearer <YOUR_JWT_TOKEN>"
```

Replace `<PATIENT_ID>` with the `id` from the create response.

#### Step 5: List All Patients (with Pagination)

```bash
# Get first page (20 patients per page by default)
curl -X GET "http://localhost:8080/patients?page=0&size=10" \
  -H "Authorization: Bearer <YOUR_JWT_TOKEN>"

# Get second page
curl -X GET "http://localhost:8080/patients?page=1&size=10" \
  -H "Authorization: Bearer <YOUR_JWT_TOKEN>"
```

### Viewing Logs

To see logs from all services:

```bash
# All services
docker compose logs -f

# Specific service
docker compose logs -f auth-service
docker compose logs -f patient-service
docker compose logs -f notification-service
docker compose logs -f api-gateway
```

### Stopping the Application

```bash
# Stop all services (keeps containers)
docker compose stop

# Stop and remove containers
docker compose down

# Stop and remove containers + volumes (deletes database data!)
docker compose down -v
```

### Troubleshooting

#### Services Won't Start

1. **Check Docker is running:**
   ```bash
   docker ps
   ```

2. **Check port conflicts:**
   - Port 8080 (Gateway), 8081 (Auth), 8082 (Patient) must be available
   - Ports 5433, 5434 (PostgreSQL) must be available
   - Port 9092 (Kafka) must be available

3. **Check logs for errors:**
   ```bash
   docker compose logs <service-name>
   ```

#### Database Connection Errors

- Wait a bit longer - databases take time to initialize
- Check database containers are healthy: `docker compose ps`
- Verify environment variables in `.env` file

#### JWT Token Errors

- Make sure you're using the token from login/register response
- Token expires after 1 hour (default) - login again to get a new token
- Check JWT_SECRET in `.env` matches between Auth Service and API Gateway

#### Kafka Connection Errors

- Wait for Kafka to fully start (can take 30+ seconds)
- Check Zookeeper is running: `docker compose ps zookeeper`
- Check Kafka logs: `docker compose logs kafka`

#### "Service Unavailable" or Connection Refused

- Services may still be starting - wait 1-2 minutes
- Check service health endpoints
- Verify Docker network: `docker network ls`

### Development Mode

To run services individually (without Docker):

1. Start infrastructure only:
   ```bash
   docker compose up postgres-auth postgres-patient kafka zookeeper
   ```

2. Run services locally with Maven:
   ```bash
   # In separate terminals
   cd auth-service && mvn spring-boot:run
   cd api-gateway && mvn spring-boot:run
   cd patient-service && mvn spring-boot:run
   cd notification-service && mvn spring-boot:run
   ```

   **Note:** Update `application.yml` files to use `localhost` instead of service names for local development.

---

