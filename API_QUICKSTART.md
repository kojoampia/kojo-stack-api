# API Quick Reference

## Getting Started (5 minutes)

### Option 1: Development Mode
```bash
mvn spring-boot:run
# Runs with H2 in-memory database
# Access: http://localhost:8080/swagger-ui.html
```

### Option 2: Docker Compose (Production-like)
```bash
docker-compose up --build
# Starts API + PostgreSQL + Redis + Kafka
# Access: http://localhost:8080/swagger-ui.html
```

## Common API Requests

### Submit Consulting Inquiry
```bash
curl -X POST http://localhost:8080/api/v1/inquiries/submit \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "type": "BACKEND",
    "message": "Need Java/Spring expertise for microservices"
  }'
```

### Get All Projects
```bash
curl http://localhost:8080/api/v1/projects
```

### Filter Projects by Type
```bash
curl http://localhost:8080/api/v1/projects/type/MICROSERVICES
```

### Get Expert-Level Skills
```bash
curl http://localhost:8080/api/v1/skills/expert
```

### Search Documentation
```bash
curl "http://localhost:8080/api/v1/docs/search?title=Kafka"
```

### Check Application Health
```bash
curl http://localhost:8080/actuator/health
```

## Database Access

### PostgreSQL
```bash
# Via Docker
docker exec -it kojo-stack-postgres psql -U kojo -d kojo_stack

# Query
SELECT * FROM projects;
SELECT * FROM inquiries;
```

### Redis Cache
```bash
# Monitor cache usage
docker exec kojo-stack-redis redis-cli MONITOR

# Check keys
docker exec kojo-stack-redis redis-cli KEYS "*"
```

## Monitoring

### View Application Logs
```bash
docker logs -f kojo-stack-api
```

### Check Metrics
```bash
curl http://localhost:8080/actuator/metrics
```

### View Prometheus Metrics
```bash
curl http://localhost:8080/actuator/prometheus | grep kojo
```

## Troubleshooting

### API not responding?
```bash
# Check health
curl http://localhost:8080/actuator/health/db

# Check logs
docker logs kojo-stack-api | tail -50
```

### Database errors?
```bash
# Restart database
docker restart kojo-stack-postgres

# Check connection
docker logs kojo-stack-postgres
```

### Cache issues?
```bash
# Clear all cache
docker exec kojo-stack-redis redis-cli FLUSHALL

# Restart Redis
docker restart kojo-stack-redis
```

## Development Tips

### Add New Endpoint
1. Create Controller in `api/controller/`
2. Create Service in `service/`
3. Create DTO in `api/dto/`
4. Create Repository in `domain/repository/`
5. Document with `@Operation` and `@Schema`

### Enable Debug Logging
```bash
# application.yml
logging:
  level:
    com.kojo.stack: DEBUG
```

### Run Tests
```bash
# Unit tests only
mvn test

# Integration tests
mvn verify
```

## Production Deployment

### Build Production Image
```bash
mvn clean package -DskipTests
docker build -t kojo-stack-api:prod .
```

### Deploy to Kubernetes
```bash
kubectl create namespace kojo-stack
kubectl apply -f k8s/ -n kojo-stack
kubectl port-forward svc/kojo-api 8080:8080 -n kojo-stack
```

### Environment Setup
```bash
export DB_PASSWORD=$(openssl rand -base64 32)
export KAFKA_BOOTSTRAP_SERVERS="kafka-broker-1:9092,kafka-broker-2:9092"
docker-compose up -d
```

## Support

- API Docs: `http://localhost:8080/v3/api-docs`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Health Check: `http://localhost:8080/actuator/health`
- Metrics: `http://localhost:8080/actuator/prometheus`

---
For full documentation, see README.md
