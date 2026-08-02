# Kojo Stack API

## Project Overview
Production-ready Spring Boot 3.2 backend for the Kojo.Stack enterprise platform.

## Core Mandates
- **Technology Stack:** Java 21, Spring Boot 3.2, MongoDB, Ehcache, MapStruct, Spring Security (JWT). Kafka was removed; there is no event streaming in this service.
- **Source of Truth:** Always prioritize `pom.xml`, `application.yml`, and the active Java classes over documentation in `README.md` if they conflict.
- **Database:** This project uses **MongoDB**. Disregard legacy references to PostgreSQL or H2 in `README.md` or stale dependencies.

## Architecture & Design Patterns
- **Layered Architecture:** Follow the `Controller -> Service -> Repository` flow.
  - `api/controller`: Thin controllers returning `ResponseEntity`.
  - `service`: Business logic. Use `@Transactional` for write operations; class-level `@Transactional(readOnly = true)` is preferred for read-heavy services.
  - `domain/model`: MongoDB documents (use `@Document`, `String` IDs).
  - `repository`: Extend `MongoRepository`. Note the package is `com.kojo.stack.repository`, NOT `domain/repository` (which does not exist).
- **DTO Mapping:** Use MapStruct mappers in `api/mapper` for converting between API DTOs and domain models.
- **Dependency Injection:** Use constructor injection via Lombok `@RequiredArgsConstructor`. Avoid field injection (`@Autowired`).
- **REST Conventions:** Endpoints should be under `/api/v1/**`.

## Development Workflows
- **Code Style:** Adhere to existing Java/Spring patterns.
- **Caching:** When adding `@Cacheable` or `@CacheEvict` with a new cache name, ensure `src/main/resources/ehcache.xml` is updated.
- **Security:** Inspect `SecurityConfig`, `JwtAuthenticationFilter`, and method-level `@PreAuthorize` together when modifying auth behavior.
- **Configuration:** Update `src/main/resources/application*.yml` for profile-specific settings.

## Testing Guidelines
- **Approach:** Prefer small, focused tests.
- **Controllers:** Use `@WebMvcTest` + `MockMvc` for narrow slices; `@SpringBootTest` for full wiring/security tests.
- **Services:** Use unit tests with mocks.
- **Persistence:** Test against MongoDB (avoid assuming SQL).
- **Structure:** Mirror production package structure in `src/test/java`.

## Legacy Code
- `com.kojo.stack.repository` is the ACTIVE repository package - every service imports from it. Earlier revisions of this file wrongly described it as a legacy remnant to avoid.
- `api/errors` is an empty directory. There is a single `Authority` type (`domain/model/Authority.java`) and no `User` class; the accounts model is `Account`.
