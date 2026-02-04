# Kojo.Stack API - Production-Ready Spring Boot Backend

## Overview

A fully-featured, production-ready Spring Boot 3.2 REST API backend for the Kojo.Stack enterprise consulting platform. Built with modern Java 21, featuring event-driven architecture, caching, comprehensive documentation, and enterprise-grade security.

## Architecture

### Technology Stack

- **Framework**: Spring Boot 3.2 (latest LTS)
- **Language**: Java 21 (latest LTS)
- **Database**: PostgreSQL (production), H2 (development)
- **Caching**: Redis with Spring Cache
- **Event Streaming**: Apache Kafka
- **API Documentation**: OpenAPI 3.0 / Swagger UI
- **ORM**: Spring Data JPA / Hibernate
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
│   │   ├── model/                      # JPA Entities
│   │   └── repository/                 # Data access layer
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
- **Domain-Driven Design**: Rich domain models with JPA

### 2. Data Persistence
- **Spring Data JPA**: Clean repository interfaces
- **Hibernate**: ORM with relationship management
- **PostgreSQL**: Production database
- **H2**: In-memory database for development
- **Liquibase/Flyway Ready**: Migration support

### 3. Caching Layer
- **Redis Integration**: High-performance distributed caching
- **Spring Cache Abstraction**: `@Cacheable`, `@CacheEvict` annotations
- **TTL Configuration**: Time-to-live for cache entries

### 4. Event-Driven Architecture
- **Apache Kafka**: Real-time event streaming
- **Event Publishing**: Project and Inquiry events
- **Consumer Ready**: Template for event listeners
- **JSON Serialization**: Spring Kafka JSON support

### 5. REST API
- **6 Feature Endpoints**:
  - Experiences (CRUD + Search)
  - Projects (CRUD + Filtering by type/client)
  - Inquiries (Submit + Status management)
  - Skills (Browse + Category filtering)
  - Documentation (Full-text search + Tag filtering)
  - Health (Liveness/Readiness probes)

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
- **CORS Configuration**: Configurable allowed origins
- **HTTPS Ready**: SSL/TLS support
- **Environment-based Config**: Secrets via environment variables
- **Non-root Container User**: Docker security best practice

## Quick Start

### Prerequisites

- Java 21+
- Maven 3.9+
- Docker & Docker Compose
- PostgreSQL 16+ (for production)
- Redis 7+ (for production)
- Kafka 7.5+ (for production)

### Development (H2 In-Memory Database)

```bash
# Clone and build
git clone <repo>
cd kojo-stack-api
mvn clean package

# Run application
mvn spring-boot:run

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
# - PostgreSQL: localhost:5432 (user: kojo, pass: kojo123)
# - Redis: localhost:6379
# - Kafka: localhost:9092
# - Zookeeper: localhost:2181
```

### Environment Variables

```bash
# Database
DB_HOST=postgres
DB_PORT=5432
DB_NAME=kojo_stack
DB_USER=kojo
DB_PASSWORD=<secure-password>

# Cache
REDIS_HOST=redis
REDIS_PORT=6379

# Event Streaming
KAFKA_BOOTSTRAP_SERVERS=kafka:9092

# Application
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
```

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

### With Testcontainers
```bash
# PostgreSQL integration tests with real database
mvn verify -Dgroups=integration
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
- **dev**: H2 database, detailed logging, CORS all origins
- **prod**: PostgreSQL, optimized logging, strict CORS

### Override via Environment
```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/db
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
# Check PostgreSQL
docker exec kojo-stack-postgres psql -U kojo -d kojo_stack

# Check logs
docker logs kojo-stack-api
```

### Redis Cache Issues
```bash
# Test Redis connection
docker exec kojo-stack-redis redis-cli ping

# Clear cache
docker exec kojo-stack-redis redis-cli FLUSHALL
```

### Kafka Message Issues
```bash
# List topics
docker exec kojo-stack-kafka kafka-topics \
  --list --bootstrap-server localhost:9092

# Monitor topics
docker exec kojo-stack-kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic project-events --from-beginning
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
