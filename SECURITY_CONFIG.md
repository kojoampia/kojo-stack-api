# Spring Security & JWT Configuration - Kojo.Stack API

## Overview
Implemented stateless JWT-based authentication with Spring Security for the Kojo.Stack API.

## Configuration Details

### Security Architecture
- **Type**: Stateless JWT Authentication
- **Session Policy**: STATELESS (no session storage)
- **CSRF Protection**: Disabled (for stateless API)
- **Password Encoding**: BCrypt

### Authorization Rules

#### Public Endpoints (No Authentication Required)
- **All GET requests**: `/api/v1/**`
- **Health/Actuator**: `/health`, `/actuator/**`
- **API Documentation**: `/swagger-ui/**`, `/v3/api-docs/**`
- **Authentication endpoints**: `/api/v1/auth/**`

#### Protected Endpoints (Authentication Required)
- **POST requests**: `/api/v1/**` - Requires valid JWT
- **PUT requests**: `/api/v1/**` - Requires valid JWT
- **DELETE requests**: `/api/v1/**` - Requires valid JWT

### JWT Configuration
- **Algorithm**: HMAC SHA-512
- **Expiration**: 24 hours (configurable)
- **Secret**: `***REMOVED-JWT-SECRET***`
  - **⚠️ IMPORTANT**: Change this in production!

### Default Test Credentials
```
Username: admin
Password: admin123
Role: ADMIN

Username: consultant
Password: consultant123
Role: CONSULTANT
```

## Components

### 1. JwtTokenProvider
Location: `src/main/java/com/kojo/stack/security/JwtTokenProvider.java`

**Responsibilities**:
- Generate JWT tokens from authentication
- Validate JWT tokens
- Extract claims (username, expiration)
- Check token expiration

### 2. JwtAuthenticationFilter
Location: `src/main/java/com/kojo/stack/security/JwtAuthenticationFilter.java`

**Responsibilities**:
- Extract JWT from `Authorization: Bearer <token>` header
- Validate token and set authentication in SecurityContext
- Runs once per request (stateless)

### 3. SecurityConfig
Location: `src/main/java/com/kojo/stack/config/SecurityConfig.java`

**Responsibilities**:
- Configure Spring Security filter chain
- Define authorization rules
- Set up authentication manager
- Configure exception handling

### 4. AuthController
Location: `src/main/java/com/kojo/stack/api/controller/AuthController.java`

**Endpoints**:
- `POST /api/v1/auth/login` - Authenticate and get JWT token
- `POST /api/v1/auth/validate` - Validate existing JWT token

## Usage Flow

### 1. Login to Get Token
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**Response**:
```json
{
  "token": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer"
}
```

### 2. Use Token for Authenticated Requests
```bash
# GET requests (public, no token needed)
curl http://localhost:8080/api/v1/profiles

# POST requests (requires token)
curl -X POST http://localhost:8080/api/v1/profiles \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com"
  }'

# PUT requests (requires token)
curl -X PUT http://localhost:8080/api/v1/profiles/{id} \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{...}'

# DELETE requests (requires token)
curl -X DELETE http://localhost:8080/api/v1/profiles/{id} \
  -H "Authorization: Bearer <token>"
```

### 3. Validate Token
```bash
curl -X POST http://localhost:8080/api/v1/auth/validate \
  -H "Authorization: Bearer <token>"
```

## Configuration Properties

Located in `src/main/resources/application.yml`:

```yaml
app:
  jwtSecret: ***REMOVED-JWT-SECRET***
  jwtExpirationMs: 86400000  # 24 hours in milliseconds
```

## Dependencies Added

```xml
<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JWT (JSON Web Token) -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>

<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
```

## Production Recommendations

1. **Change JWT Secret**: Update `app.jwtSecret` to a strong, random value
2. **Use Environment Variables**: Store secrets in environment variables or secrets manager
3. **Implement UserDetailsService**: Replace in-memory users with database-backed implementation
4. **Enable HTTPS**: Always use HTTPS in production
5. **Token Refresh**: Implement refresh token mechanism for better security
6. **Audit Logging**: Add detailed audit logs for authentication attempts
7. **Rate Limiting**: Implement rate limiting on login endpoint

## Error Responses

- **401 Unauthorized**: Invalid or missing JWT token
- **403 Forbidden**: Valid token but insufficient permissions
- **400 Bad Request**: Invalid token format

## Security Filter Chain Order

1. CORS Filter (if configured)
2. JwtAuthenticationFilter (our custom filter)
3. UsernamePasswordAuthenticationFilter (Spring Security default)
4. Authorization Filter (Spring Security)

## Testing Endpoints

All public GET endpoints are accessible without authentication:
```bash
curl http://localhost:8080/api/v1/profiles
curl http://localhost:8080/api/v1/experiences
curl http://localhost:8080/api/v1/projects
```

API Documentation available at:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs
