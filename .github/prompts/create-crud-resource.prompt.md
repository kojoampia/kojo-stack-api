---
description: "Generate a new MongoDB-backed CRUD resource for kojo-stack-api, including Spring Boot controller, service, repository, DTO, mapper, cache updates, and API wiring."
name: "Create CRUD Resource"
argument-hint: "Resource name, fields, endpoint path, validation rules, and any filters or search endpoints"
agent: "fullstack-engineer"
---
Create a new CRUD resource for the `kojo-stack-api` workspace using the existing active architecture and conventions.

Inputs to infer or ask from the user argument:
- Resource name and plural form
- Fields, types, and validation rules
- Endpoint base path under `/api/v1`
- Whether the resource is public read / authenticated write / admin-only
- Any list filters, search behavior, or caching requirements

Required implementation rules:
- Follow the active controller -> service -> repository flow used by this project.
- Persist using MongoDB documents in `src/main/java/com/kojo/stack/domain/model` and active repositories in `src/main/java/com/kojo/stack/domain/repository`.
- Use `ResponseEntity` in controllers and keep controllers thin.
- Put business logic in a dedicated service with the existing transaction pattern.
- Add DTOs and MapStruct mappers when the API contract should not expose the raw domain model directly.
- Keep routes under `/api/v1/**`.
- Avoid legacy JHipster remnants under `src/main/java/com/kojo/stack/repository` and `src/main/java/com/kojo/stack/api/errors` unless the task explicitly requires refactoring them.
- Verify imports carefully where duplicate legacy and active classes exist.
- If you add caching, update `src/main/resources/ehcache.xml` in the same change.
- If security behavior is affected, check `SecurityConfig`, `JwtAuthenticationFilter`, `JwtTokenProvider`, and method-level annotations before finalizing the change.

Execution checklist:
1. Inspect the closest existing resource with similar behavior and reuse its structure.
2. Implement only the files needed for the new resource.
3. Update configuration or cache aliases only when the new feature requires it.
4. Add focused tests for the new behavior when practical.
5. Run the most relevant Maven validation command for the changed code.

Return:
- The files created or changed
- Any assumptions made from ambiguous requirements
- Validation performed and any remaining gaps
