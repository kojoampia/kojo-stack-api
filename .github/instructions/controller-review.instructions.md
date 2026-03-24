---
description: "Use when reviewing or editing REST controllers in kojo-stack-api, especially for ResponseEntity usage, OpenAPI annotation quality, API versioning under /api/v1, and endpoint consistency."
name: "Controller Review Guidelines"
applyTo: "src/main/java/com/kojo/stack/api/controller/**/*.java"
---
# Controller Review Guidelines

- Keep controllers focused on transport concerns: request mapping, input validation, response shaping, and delegation to services.
- Prefer `ResponseEntity` for explicit status codes and headers. Match status semantics to behavior (`200`, `201`, `204`, error paths).
- Keep endpoint paths under `/api/v1/**` unless a change explicitly introduces a new versioning strategy.
- Ensure method-level mappings, parameter usage, and HTTP verbs align with REST intent.
- Keep OpenAPI annotations (`@Tag`, `@Operation`, and response docs where needed) aligned with actual runtime behavior.
- Do not leak internal persistence concerns in controller contracts when DTOs are the intended API boundary.
- Validate that authorization annotations and endpoint visibility align with `SecurityConfig` request matcher behavior.
- Avoid moving business logic into controllers; place it in service classes.
- When introducing new cached endpoints or cache names from controller-driven features, ensure corresponding cache alias updates in `src/main/resources/ehcache.xml`.
