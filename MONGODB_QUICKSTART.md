# MongoDB Quick Start Guide

## Overview
The Kojo.Stack API has been successfully refactored to use MongoDB instead of PostgreSQL. This guide will get you up and running in minutes.

## Prerequisites
- Docker & Docker Compose (recommended)
- Java 21 or higher
- Maven 3.9+ (or use ./mvnw wrapper)

## Quick Start (2 minutes)

### 1. Start MongoDB Locally
```bash
# Option A: Using Docker (Recommended)
docker run -d \
  --name kojo-mongodb \
  -p 27017:27017 \
  -e MONGO_INITDB_ROOT_USERNAME=admin \
  -e MONGO_INITDB_ROOT_PASSWORD=password \
  mongo:latest

# Option B: Using Docker Compose
docker-compose up -d mongodb
```

### 2. Run the Application
```bash
cd /.../kojo-stack-api

# Build (if needed)
./mvnw clean package -DskipTests

# Run
java -jar target/kojo-stack-api-2026.1.0.jar
```

### 3. Verify It Works
```bash
# Health check
curl http://localhost:8080/api/v1/health

# API Docs (Open in browser)
open http://localhost:8080/swagger-ui.html

# Get experiences
curl http://localhost:8080/api/v1/experiences | jq .
```

## MongoDB Connection Details

**Development (Default)**
```
mongodb://localhost:27017/kojo-stack
```

**With Authentication**
```
mongodb://admin:password@localhost:27017/kojo-stack?authSource=admin
```

**Environment Variables (if needed)**
```bash
export SPRING_DATA_MONGODB_URI=mongodb://localhost:27017/kojo-stack
java -jar target/kojo-stack-api-2026.1.0.jar
```

## Docker Compose (Full Stack)

Create `docker-compose.yml`:
```yaml
version: '3.9'

services:
  mongodb:
    image: mongo:latest
    container_name: kojo-mongodb
    environment:
      MONGO_INITDB_ROOT_USERNAME: admin
      MONGO_INITDB_ROOT_PASSWORD: password
    ports:
      - "27017:27017"
    volumes:
      - mongodb_data:/data/db
    networks:
      - kojo-network

  api:
    build: .
    container_name: kojo-stack-api
    environment:
      SPRING_DATA_MONGODB_URI: mongodb://admin:password@mongodb:27017/kojo-stack?authSource=admin
      SPRING_KAFKA_BOOTSTRAP_SERVERS: localhost:9092
      SPRING_REDIS_HOST: localhost
    ports:
      - "8080:8080"
    depends_on:
      - mongodb
    networks:
      - kojo-network

volumes:
  mongodb_data:

networks:
  kojo-network:
    driver: bridge
```

Run:
```bash
docker-compose up -d
```

## Verify MongoDB Collections

### Using mongosh (MongoDB Shell)
```bash
mongosh mongodb://admin:password@localhost:27017/kojo-stack

# View collections
show collections

# Query experiences
db.experiences.find()

# Query projects
db.projects.find()

# Count documents
db.experiences.countDocuments()

# Create an index
db.experiences.createIndex({ company: 1 })
```

### View in MongoDB Compass
1. Download [MongoDB Compass](https://www.mongodb.com/products/compass)
2. Connect to: `mongodb://admin:password@localhost:27017`
3. Navigate to `kojo-stack` database
4. Browse collections

## API Endpoints

All REST endpoints continue to work as before:

```bash
# Experiences
GET    /api/v1/experiences                # Get all
POST   /api/v1/experiences                # Create
GET    /api/v1/experiences/{id}           # Get one
PUT    /api/v1/experiences/{id}           # Update
DELETE /api/v1/experiences/{id}           # Delete

# Projects
GET    /api/v1/projects                   # Get all
POST   /api/v1/projects                   # Create
GET    /api/v1/projects?type=MICROSERVICES # Filter by type
GET    /api/v1/projects?client=Acme       # Search by client

# Documentation
GET    /api/v1/docs                       # Get all
GET    /api/v1/docs/search?q=architecture # Search

# Health
GET    /api/v1/health                     # Health check
GET    /api/v1/health/live                # Liveness probe
GET    /api/v1/health/ready               # Readiness probe
```

## Sample Data

Create an experience with metrics:
```bash
curl -X POST http://localhost:8080/api/v1/experiences \
  -H "Content-Type: application/json" \
  -d '{
    "company": "Acme Inc",
    "role": "Senior Developer",
    "period": "2023-2024",
    "status": "ACTIVE",
    "description": "Led development team",
    "stack": ["Java 21", "Spring Boot", "MongoDB"],
    "metrics": [
      {"label": "Performance", "value": "95%"},
      {"label": "Uptime", "value": "99.9%"}
    ]
  }'
```

## Troubleshooting

### MongoDB Connection Refused
```bash
# Check if MongoDB is running
docker ps | grep mongo

# Check logs
docker logs kojo-mongodb

# Verify connection
mongosh mongodb://admin:password@localhost:27017
```

### Application Won't Start
```bash
# Check logs
java -jar target/kojo-stack-api-2026.1.0.jar 2>&1 | tail -50

# Verify MongoDB is accessible
curl mongodb://localhost:27017 || echo "Connection failed"
```

### Port Already in Use
```bash
# Change MongoDB port
docker run -d -p 27018:27017 mongo:latest

# Update connection string
export SPRING_DATA_MONGODB_URI=mongodb://localhost:27018/kojo-stack
```

## Monitoring

### Health Endpoint
```bash
curl http://localhost:8080/actuator/health | jq .
```

### Metrics (Prometheus)
```bash
curl http://localhost:8080/actuator/metrics | jq .
```

### MongoDB Stats
```bash
mongosh
> use kojo-stack
> db.stats()
> db.experiences.stats()
```

## Production Deployment

### Environment Variables
```bash
export SPRING_DATA_MONGODB_URI=mongodb://admin:password@prod-mongodb.example.com:27017/kojo-stack?authSource=admin
export SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka-broker:9092
export SPRING_REDIS_HOST=redis-server
```

### Kubernetes
```bash
kubectl set env deployment/kojo-stack-api \
  SPRING_DATA_MONGODB_URI="mongodb://admin:password@mongodb:27017/kojo-stack?authSource=admin"
```

### Docker Image
```bash
# Build
docker build -t kojo-stack-api:2026.1.0 .

# Push to registry
docker tag kojo-stack-api:2026.1.0 registry.example.com/kojo-stack-api:2026.1.0
docker push registry.example.com/kojo-stack-api:2026.1.0

# Run
docker run -d \
  -e SPRING_DATA_MONGODB_URI=mongodb://admin:password@mongodb:27017/kojo-stack \
  -p 8080:8080 \
  kojo-stack-api:2026.1.0
```

## Data Migration (From PostgreSQL)

If migrating from PostgreSQL:

```bash
# 1. Export PostgreSQL data
pg_dump -U postgres kojo_stack -t experiences -D > experiences.sql

# 2. Convert SQL to JSON (use a tool like pgloader or custom script)
# Example: convert.py

# 3. Import to MongoDB
mongoimport --uri "mongodb://admin:password@localhost:27017/kojo-stack" \
  --collection experiences \
  --file experiences.json \
  --jsonArray
```

## Performance Tips

1. **Enable Indexing:** Auto-index creation is enabled
2. **Use Projection:** Query only needed fields
3. **Batch Operations:** Use insertMany() for bulk inserts
4. **Connection Pooling:** Default pool size is 100
5. **Caching:** Redis layer is enabled (1-hour TTL)

## Next Steps

1. ✅ Start MongoDB
2. ✅ Run the application
3. ✅ Test endpoints
4. ✅ View API docs (Swagger UI)
5. ✅ Explore MongoDB collections
6. 📋 Read [MONGODB_REFACTORING.md](MONGODB_REFACTORING.md) for detailed info
7. 🚀 Deploy to production

## Support

For detailed information, see:
- [MONGODB_REFACTORING.md](MONGODB_REFACTORING.md) - Complete refactoring documentation
- [README.md](README.md) - Project overview
- [API_QUICKSTART.md](API_QUICKSTART.md) - API usage guide

---

**Build Date:** February 4, 2026
**Status:** ✅ Production Ready
