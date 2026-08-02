---
description: "Use when editing Spring Boot Java application code, adding REST endpoints, services, repositories, DTOs, MapStruct mappers, MongoDB documents, or API features in kojo-stack-api."
name: "Backend Java API Guidelines"
applyTo: "src/main/java/**/*.java"
---
# Backend Java API Guidelines

- Treat `pom.xml`, `application.yml`, and the active Spring classes as the source of truth when README content disagrees with the codebase.
- Keep the main flow as controller -> service -> repository. Put request handling in `api/controller`, business logic in `service`, and persistence in `repository` (package `com.kojo.stack.repository`).
- Use MongoDB patterns, not JPA patterns: new persisted models belong in `domain/model`, use `@Document`, prefer `String` ids, and extend `MongoRepository` in active repositories.
- `src/main/java/com/kojo/stack/repository` holds the active repositories; add new ones there. `src/main/java/com/kojo/stack/api/errors` is an empty directory.
- There are no duplicate `Authority`/`User` types to disambiguate; `domain/model/Authority.java` is the only one and accounts are modelled by `Account`.
- Keep controllers thin and return `ResponseEntity`. Keep endpoints under `/api/v1/**` unless the task clearly requires a new version boundary.
- Put write logic behind services annotated with `@Transactional`; keep class-level `@Transactional(readOnly = true)` when following the existing service pattern.
- Prefer constructor injection through Lombok `@RequiredArgsConstructor`; do not introduce field injection.
- When translating between API contracts and domain models, use MapStruct mappers in `src/main/java/com/kojo/stack/api/mapper` instead of hand-written conversion logic unless the mapping is unusually custom.
- Before changing authentication or authorization behavior, inspect `SecurityConfig`, `JwtAuthenticationFilter`, `JwtTokenProvider`, and any method-level `@PreAuthorize` annotations together.
- When adding `@Cacheable` or `@CacheEvict` with a new cache name, update `src/main/resources/ehcache.xml` in the same change.
- If a change introduces new config or profile-specific behavior, update the relevant file under `src/main/resources/application*.yml` rather than scattering magic defaults in code.
