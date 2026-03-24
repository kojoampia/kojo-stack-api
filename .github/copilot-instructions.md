# Project Guidelines

## Build And Test
- Use `./mvnw clean package` for a full local build.
- Use `./mvnw clean package -DskipTests` only when matching the Docker build flow.
- Use `./mvnw spring-boot:run` for local development.
- CI currently builds through Docker and skips tests, so run relevant tests locally when changing application code.

## Architecture
- The application is a Spring Boot 3.2 API on Java 21 with a controller -> service -> repository structure.
- Keep new business logic in `service` classes, keep controllers thin, and keep persistence logic in Spring Data repositories.
- Domain documents live under `src/main/java/com/kojo/stack/domain/model` and repositories for active code live under `src/main/java/com/kojo/stack/domain/repository`.
- Use MapStruct mappers in `src/main/java/com/kojo/stack/api/mapper` when translating between DTOs and domain models.
- Security is stateless JWT-based. Check `SecurityConfig`, `JwtAuthenticationFilter`, and `JwtTokenProvider` before changing endpoint access.

## Data And Configuration
- The runtime database is MongoDB, not PostgreSQL. Prefer `MongoRepository` patterns and `@Document` models.
- Main configuration lives in `src/main/resources/application.yml` with profile overrides in `application-dev.yml` and `application-prod.yml`.
- Development startup may seed data through `DataInitConfig` when `app.db.init-data` is enabled.
- When adding a new cache name, also add the matching alias in `src/main/resources/ehcache.xml`.

## Conventions
- Prefer constructor injection through Lombok `@RequiredArgsConstructor`; do not introduce field injection.
- Follow the existing service annotations pattern: class-level `@Transactional(readOnly = true)` and method-level write transactions for mutations.
- Keep REST endpoints under `/api/v1/**` unless there is a clear reason to change the versioned API structure.
- Use `ResponseEntity` consistently for controller responses and keep OpenAPI annotations aligned with endpoint behavior.

## Known Pitfalls
- Treat `pom.xml` and the Spring config files as the source of truth when README content conflicts with the codebase.
- Avoid adding new code to the legacy JHipster remnants under `src/main/java/com/kojo/stack/repository` and `src/main/java/com/kojo/stack/api/errors` unless you are explicitly refactoring them.
- There are duplicate legacy and active domain/repository classes in a few areas, so verify imports carefully before editing `Authority`, `User`, or repository types.
- `SECURITY_CONFIG.md` is useful context, but the implemented JWT algorithm and some access behavior should be verified against the source code before changing security-sensitive logic.

## Key Files
- `pom.xml`
- `src/main/resources/application.yml`
- `src/main/resources/application-dev.yml`
- `src/main/resources/application-prod.yml`
- `src/main/java/com/kojo/stack/config/SecurityConfig.java`
- `src/main/java/com/kojo/stack/config/DataInitConfig.java`
- `src/main/java/com/kojo/stack/security/JwtTokenProvider.java`