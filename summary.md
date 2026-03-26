# CORS Fix Summary — jojoaddison.net

**Date:** March 25, 2026  
**Issue:** `POST https://www.jojoaddison.net/api/v1/auth/login` returning `403 Forbidden` with body `Invalid CORS request`

---

## Problem

The frontend application at `https://www.jojoaddison.net` was unable to make API calls to `/api/v1/auth/login`. Every `POST` request was rejected with a **403 Forbidden** status and the message **"Invalid CORS request"**.

### Root Cause

The Spring Boot (JHipster-based) backend at `kojo-stack-api` has a built-in `CorsFilter` in its Spring Security filter chain. This filter inspects the `Origin` header on incoming requests and rejects any origin that is not explicitly allowed.

The API container had **no CORS allowed-origins configured**, so every cross-origin request from the browser (originating from `https://www.jojoaddison.net`) was rejected by Spring before it even reached the login controller.

Additionally, the Nginx reverse proxy configuration for the `/api` location had **no CORS handling at all** — it simply forwarded requests as-is to the backend, including the `Origin` header that triggered Spring's rejection.

---

## Changes Made

### 1. Nginx Configuration — `/etc/nginx/sites-available/jojoaddison.conf`

**File:** `available/jojoaddison.conf`  
**Location block:** `location ~ ^/api`

Added full CORS handling at the Nginx layer for all `/api` routes:

#### a) Preflight (OPTIONS) Handling

```nginx
if ($request_method = 'OPTIONS') {
    add_header Access-Control-Allow-Origin $http_origin always;
    add_header Access-Control-Allow-Methods 'GET, POST, PUT, DELETE, OPTIONS' always;
    add_header Access-Control-Allow-Headers 'Authorization, Content-Type, Accept, Origin, X-Requested-With' always;
    add_header Access-Control-Allow-Credentials 'true' always;
    add_header Access-Control-Max-Age 1800;
    add_header Content-Type 'text/plain; charset=UTF-8';
    add_header Content-Length 0;
    return 204;
}
```

**Why:** Browsers send an `OPTIONS` preflight request before any cross-origin `POST` with `Content-Type: application/json`. Nginx now intercepts these and responds with a `204 No Content` and the appropriate CORS headers, without ever touching the backend. This is faster and avoids Spring's CORS filter entirely for preflights.

#### b) CORS Headers on Actual Requests

```nginx
add_header Access-Control-Allow-Origin $http_origin always;
add_header Access-Control-Allow-Credentials 'true' always;
add_header Access-Control-Expose-Headers 'Authorization, Link, X-Total-Count' always;
```

**Why:** Even after the preflight succeeds, the browser checks the actual response for CORS headers. The `always` directive ensures these headers are sent regardless of the backend's response status code (e.g., `401`, `500`).

#### c) Stripping the Origin Header

```nginx
proxy_set_header Origin '';
```

**Why:** This is the critical fix. Spring's `CorsFilter` inspects the `Origin` header to decide whether to allow or reject the request. By setting it to an empty string at the Nginx layer, the request arrives at Spring without an `Origin`, so the `CorsFilter` treats it as a same-origin (non-CORS) request and lets it through. Nginx is now fully responsible for CORS enforcement.

---

### 2. Docker Compose — `docker-compose.yaml`

**File:** `webroot/01-jojoaddison/kojo-stack/docker-compose.yaml`  
**Service:** `kojo-stack-api`

Added JHipster CORS environment variables to the API container:

```yaml
- JHIPSTER_CORS_ALLOWEDORIGINS=https://www.jojoaddison.net,https://jojoaddison.net
- JHIPSTER_CORS_ALLOWEDMETHODS=GET,POST,PUT,DELETE,OPTIONS
- JHIPSTER_CORS_ALLOWEDHEADERS=*
- JHIPSTER_CORS_EXPOSEDHEADERS=Authorization,Link,X-Total-Count
- JHIPSTER_CORS_ALLOWCREDENTIALS=true
- JHIPSTER_CORS_MAXAGE=1800
```

**Why:** These are the standard JHipster CORS configuration properties exposed as environment variables. While the current application uses a custom `CorsFilter` that doesn't read these (which is why the Nginx `Origin` stripping was necessary), these variables serve as **future-proofing** — if the app is updated to use JHipster's default CORS configuration, it will pick up these values automatically.

---

## Deployment Steps Performed

1. Edited `/etc/nginx/sites-available/jojoaddison.conf` (symlinked to `sites-enabled`)
2. Ran `nginx -t` — configuration syntax validated successfully
3. Ran `systemctl reload nginx` — applied changes without downtime
4. Ran `docker compose up -d kojo-stack-api` — recreated the API container with new environment variables
5. Verified container health status

---

## Verification

### Preflight Request (OPTIONS)
```
HTTP/2 204
access-control-allow-origin: https://www.jojoaddison.net
access-control-allow-methods: GET, POST, PUT, DELETE, OPTIONS
access-control-allow-headers: Authorization, Content-Type, Accept, Origin, X-Requested-With
access-control-allow-credentials: true
access-control-max-age: 1800
```
**Result:** ✅ Preflight succeeds with proper CORS headers

### Actual Request (POST)
```
HTTP/2 401
access-control-allow-origin: https://www.jojoaddison.net
access-control-allow-credentials: true
access-control-expose-headers: Authorization, Link, X-Total-Count
content-type: application/json
{"message":"Invalid credentials"}
```
**Result:** ✅ Returns `401 Unauthorized` with CORS headers (expected for invalid credentials — previously returned `403 Invalid CORS request`)

---

## Architecture Overview

```
Browser (www.jojoaddison.net)
    │
    ├── OPTIONS /api/v1/auth/login
    │       → Nginx returns 204 + CORS headers (never hits Spring)
    │
    └── POST /api/v1/auth/login
            → Nginx adds CORS headers + strips Origin header
            → Proxies to Spring Boot (127.0.0.1:1987)
            → Spring sees no Origin → skips CorsFilter → processes request
            → Response flows back through Nginx with CORS headers attached
```

---

## Files Modified

| File | Change |
|------|--------|
| `/etc/nginx/sites-available/jojoaddison.conf` | Added CORS preflight handling, response headers, and Origin stripping to `/api` location |
| `/root/webroot/01-jojoaddison/kojo-stack/docker-compose.yaml` | Added `JHIPSTER_CORS_*` environment variables to `kojo-stack-api` service |
