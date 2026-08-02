# Kojo.Stack API - Production-Ready Spring Boot Backend

## Overview

A production-ready Spring Boot 3.2 REST API backend for the Kojo.Stack consulting platform, built on Java 21 with MongoDB persistence, in-process caching, OpenAPI documentation, and stateless JWT security.

## Architecture

### Technology Stack

- **Framework**: Spring Boot 3.2 (latest LTS)
- **Language**: Java 21 (latest LTS)
- **Database**: MongoDB (all environments)
- **Caching**: Ehcache via the JSR-107 (JCache) Spring Cache abstraction
- **API Documentation**: OpenAPI 3.0 / Swagger UI
- **Data Access**: Spring Data MongoDB
- **Security**: Spring Security with stateless JWT (HS256)
- **DTO Mapping**: MapStruct (compile-time safety)
- **Logging**: SLF4J with Logback
- **Build Tool**: Maven 3.9
- **Containerization**: Docker + Docker Compose
- **Java Features**: Records, Sealed Classes, Text Blocks

### Project Structure

```
kojo-stack-api/
├── src/main/java/com/kojo/stack/
│   ├── KojoStackApplication.java       # Main application entry
│   ├── config/                         # Configuration classes
│   │   ├── CorsConfig.java             # CORS configuration
│   │   ├── DataInitConfig.java         # Data initialization
│   │   └── JacksonConfig.java          # JSON serialization config
│   ├── api/                            # REST API Layer
│   │   ├── controller/                 # REST Controllers (HTTP)
│   │   ├── dto/                        # Data Transfer Objects
│   │   ├── exception/                  # Exception handling
│   │   └── mapper/                     # DTO <-> Entity mapping
│   ├── domain/                         # Domain Model Layer
│   │   └── model/                      # MongoDB documents (@Document)
│   ├── repository/                     # Spring Data MongoDB repositories
│   ├── security/                       # JWT provider, filter, user details
│   └── service/                        # Business Logic Layer
├── src/main/resources/
│   └── application.yml                 # Configuration file
├── pom.xml                             # Maven dependencies
├── Dockerfile                          # Multi-stage Docker build
└── docker-compose.yml                  # Full stack orchestration
```

## Key Features

### 1. Clean Architecture
- **Separation of Concerns**: Controller → Service → Repository
- **DTOs for API Contract**: MapStruct for type-safe mapping
- **Domain-Driven Design**: Rich domain models as MongoDB documents

### 2. Data Persistence
- **Spring Data MongoDB**: Repository interfaces in `com.kojo.stack.repository`
- **Documents**: `@Document` models with `String` ids in `domain/model`
- **Schema**: managed by the application; there is no migration tool
- **Seeding**: `DataInitConfig` loads `resources/data.json` when `app.db.init-data` is true (dev only)

### 3. Caching Layer
- **Ehcache**: in-process cache configured by `src/main/resources/ehcache.xml`
- **Spring Cache Abstraction**: `@Cacheable`, `@CacheEvict` annotations
- **Important**: every new cache name needs a matching `alias` in `ehcache.xml`

### 4. REST API
All endpoints live under `/api/v1/**`:
  - Projects, Experiences, Skills, Documentation, Education (public read, authenticated write)
  - Profiles, Settings, KPIs (public read, authenticated write)
  - Metrics (authenticated)
  - Inquiries (public submit, admin read/manage)
  - Accounts, Authorities (admin only)
  - Auth (login, token validation)
  - Health (liveness/readiness probes)

### 6. API Documentation
- **OpenAPI 3.0**: Complete API specification
- **Swagger UI**: Interactive API explorer at `/swagger-ui.html`
- **Detailed Schemas**: Full documentation on all endpoints

### 7. Error Handling
- **Global Exception Handler**: Centralized error handling
- **Validation**: Jakarta Validation with detailed error messages
- **Standard Error Response**: Consistent error format across API

### 8. Observability
- **Spring Boot Actuator**: Health checks, metrics, info
- **Prometheus Metrics**: Export to Prometheus for monitoring
- **Structured Logging**: SLF4J with contextual information
- **Kubernetes Probes**: Liveness and readiness endpoints

### 9. Security
- **Stateless JWT**: HS256, signing key supplied via the `JWT_SECRET` environment variable.
  The application refuses to start if it is missing or shorter than 32 bytes.
- **Method security**: `@EnableMethodSecurity` is required for the `@PreAuthorize`
  annotations on the account, authority and inquiry endpoints to be enforced. Do not remove it.
- **CORS Configuration**: origins from `app.security.cors-origins` (env `CORS_ORIGINS`)
- **Environment-based Config**: secrets via environment variables, never in `application.yml`
- **Non-root Container User**: Docker security best practice

## Quick Start

### Prerequisites

- Java 21 (the build targets release 21; a newer default JDK will fail to compile)
- Maven 3.9+ (or the bundled `./mvnw`)
- Docker & Docker Compose
- MongoDB 7+

### Development (local MongoDB)

```bash
# Build (runs the test suite)
./mvnw clean package

# Run application on port 8085 with seeded data
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Access API
# Swagger UI: http://localhost:8080/swagger-ui.html
# API Docs: http://localhost:8080/v3/api-docs
# Health: http://localhost:8080/actuator/health
```

### Production (Docker Compose Stack)

```bash
# Build and run full stack
docker-compose up --build

# Services:
# - API: http://localhost:8080
# - Swagger UI: http://localhost:8080/swagger-ui.html
# - MongoDB: localhost:27017
```

### Environment Variables

```bash
# Database
MONGODB_HOST=mongodb
MONGODB_PORT=27017
MONGODB_DATABASE=kojo-stack

# Security — both are REQUIRED; the application refuses to start without them
JWT_SECRET=<at least 32 bytes; openssl rand -base64 48>
ADMIN_PASSWORD=<at least 12 characters; openssl rand -base64 24>

# Site administrator (optional overrides)
ADMIN_LOGIN=admin
ADMIN_EMAIL=admin@example.com

# CORS — comma-separated; empty means same-origin only
CORS_ORIGINS=https://jojoaddison.net

# Application
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
```

### The site administrator account

There is exactly one administrator, and it is **not** stored in `data.json` or any seed
script. `AdminAccountInitializer` reconciles it from the environment on every start:

| State on startup | Action |
|---|---|
| No account with `ADMIN_LOGIN` | Created with `ADMIN_PASSWORD`, granted `ROLE_ADMIN` + `ROLE_USER` |
| Account exists, password differs | Password re-encoded and updated; reset/activation keys cleared |
| Account exists, password matches | Nothing written |

**To rotate the credential:** change `ADMIN_PASSWORD` in the environment and restart the API.

This runs in every profile, including `prod` — unlike `DataInitConfig`, which seeds
portfolio content only under `!prod`.

## API Endpoints

### Experiences
```
GET    /api/v1/experiences              # Get all
GET    /api/v1/experiences/active       # Get active only
GET    /api/v1/experiences/search       # Search by company
POST   /api/v1/experiences              # Create
PUT    /api/v1/experiences/{id}         # Update
DELETE /api/v1/experiences/{id}         # Delete
```

### Projects
```
GET    /api/v1/projects                 # Get all
GET    /api/v1/projects/active          # Get active only
GET    /api/v1/projects/type/{type}     # Filter by type
GET    /api/v1/projects/search          # Search by client
POST   /api/v1/projects                 # Create
PUT    /api/v1/projects/{id}            # Update
DELETE /api/v1/projects/{id}            # Delete
```

### Inquiries (Hire Consultant Form)
```
GET    /api/v1/inquiries                # Get all (admin)
GET    /api/v1/inquiries/new            # Get new inquiries
GET    /api/v1/inquiries/type/{type}    # Filter by type
POST   /api/v1/inquiries/submit         # Submit new inquiry
PATCH  /api/v1/inquiries/{id}/status    # Update status
DELETE /api/v1/inquiries/{id}           # Delete
```

### Skills
```
GET    /api/v1/skills                   # Get all
GET    /api/v1/skills/category/{cat}    # Filter by category
GET    /api/v1/skills/expert            # Get expert skills (>=80)
```

### Documentation
```
GET    /api/v1/docs                     # Get all
GET    /api/v1/docs/type/{type}         # Filter by type
GET    /api/v1/docs/search              # Search by title
GET    /api/v1/docs/tag/{tag}           # Filter by tag
POST   /api/v1/docs                     # Create
PUT    /api/v1/docs/{id}                # Update
DELETE /api/v1/docs/{id}                # Delete
```

### Health
```
GET    /api/v1/health                   # Full health status
GET    /api/v1/health/live              # Liveness probe
GET    /api/v1/health/ready             # Readiness probe
```

## Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn verify
```

### Running a single test
```bash
./mvnw test -Dtest=SecurityAccessTest
./mvnw test -Dtest=AccountServiceTest#getAllDoesNotLeakPassword
```

## Building Docker Image

### Build Image
```bash
docker build -t kojo-stack-api:2026.1.0 .
docker tag kojo-stack-api:2026.1.0 kojo-stack-api:latest
```

### Run Single Container
```bash
docker run -p 8080:8080 \
  -e DB_HOST=postgres \
  -e DB_USER=kojo \
  -e DB_PASSWORD=secret \
  kojo-stack-api:latest
```

### Push to Registry
```bash
docker tag kojo-stack-api:latest your-registry/kojo-stack-api:2026.1.0
docker push your-registry/kojo-stack-api:2026.1.0
```

## Kubernetes Deployment

### Create Namespace
```bash
kubectl create namespace kojo-stack
```

### Deploy with Helm (Example)
```bash
helm install kojo-api ./helm-chart \
  --namespace kojo-stack \
  --values values-prod.yml
```

### Manual Deployment
```bash
kubectl apply -f k8s/deployment.yml
kubectl apply -f k8s/service.yml
kubectl apply -f k8s/ingress.yml
```

## Monitoring & Metrics

### Prometheus Metrics
```
http://localhost:8080/actuator/prometheus
```

### Application Metrics
```
- HTTP request duration
- Database connection pool stats
- JVM memory, GC, threads
- Business metrics (inquiries received, projects created, etc.)
```

### Health Endpoints
```
http://localhost:8080/actuator/health              # Full status
http://localhost:8080/actuator/health/db           # Database
http://localhost:8080/actuator/health/redis        # Cache
http://localhost:8080/actuator/health/kafka        # Message broker
http://localhost:8080/actuator/health/livenessState
http://localhost:8080/actuator/health/readinessState
```

## Configuration Management

### Application Profiles
- **dev**: local MongoDB on port 8085, verbose logging, seeded data, throwaway JWT secret
- **prod**: MongoDB via `MONGODB_HOST`, minimal logging, `JWT_SECRET` required

### Override via Environment
```bash
SPRING_DATA_MONGODB_URI=mongodb://host:27017/kojo-stack
SPRING_REDIS_HOST=redis-host
LOGGING_LEVEL_COM_KOJO_STACK=DEBUG
```

## Performance Tuning

### Database
- Connection pooling (Hikari): 20 connections (prod)
- Batch operations: Enabled (20-item batches)
- Query optimization: N+1 lazy loading prevention

### Caching
- TTL: 1 hour (configurable)
- Eviction: LRU with max memory policy
- Cache-aside pattern with `@Cacheable`

### Application
- Compression: Gzip for responses >1KB
- Async processing: `@Async` for non-blocking operations
- Pagination: Ready for large datasets

## Security Considerations

### Production Checklist
- [ ] Use strong database password
- [ ] Enable HTTPS/TLS
- [ ] Configure strict CORS origins
- [ ] Enable authentication/authorization
- [ ] Use managed secrets (AWS Secrets Manager, HashiCorp Vault)
- [ ] Enable audit logging
- [ ] Configure rate limiting
- [ ] Use network policies in Kubernetes
- [ ] Regular security updates
- [ ] Implement API key management

## Troubleshooting

### Database Connection Issues
```bash
# Check MongoDB
docker exec kojo-stack-db mongosh kojo-stack --eval 'db.runCommand({ping:1})'

# Check logs
docker logs kojo-stack-api
```

### Application refuses to start
```bash
# "app.jwtSecret is not configured" means JWT_SECRET is unset or under 32 bytes.
# Generate one with:
openssl rand -base64 48
```

### Cache Issues
```bash
# Ehcache is in-process: restarting the container clears every cache.
# A @Cacheable name with no matching alias in ehcache.xml fails at startup.
```

## Contributing

1. Follow REST API conventions
2. Maintain clean architecture layers
3. Add OpenAPI documentation
4. Write unit tests
5. Handle exceptions globally
6. Use meaningful commit messages

## License

MIT License - See LICENSE file for details

## Version

- API Version: 2026.1.0
- Spring Boot: 3.2.0
- Java: 21 (LTS)
- Latest Update: February 2026
