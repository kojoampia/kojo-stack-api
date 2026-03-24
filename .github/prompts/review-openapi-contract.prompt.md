---
description: "Review controller contracts in kojo-stack-api against OpenAPI annotations and endpoint behavior, highlighting mismatches in paths, methods, statuses, and response schemas."
name: "Review OpenAPI Contract"
argument-hint: "Controller(s) or endpoint area to review"
agent: "fullstack-engineer"
---
Perform an API contract review for `kojo-stack-api` with emphasis on OpenAPI correctness.

Review scope:
- Controllers in `src/main/java/com/kojo/stack/api/controller`
- DTO contracts in `src/main/java/com/kojo/stack/api/dto`
- OpenAPI annotations (`@Tag`, `@Operation`, response metadata)
- Endpoint versioning and path conventions under `/api/v1/**`

Checks to perform:
- Path and HTTP method consistency between mapping annotations and documentation
- Declared response status codes versus implemented `ResponseEntity` behavior
- Request/response schema alignment with DTO usage
- Missing, stale, or ambiguous operation descriptions
- Security-related annotation mismatches for protected endpoints

Project-specific rules:
- Prioritize source code behavior if markdown docs disagree.
- Treat endpoint behavior and contract clarity as first-class review criteria.
- Call out backward compatibility risks for clients when contract changes are detected.

Return format:
1. Findings ordered by severity with precise file references
2. Suggested annotation and contract fixes
3. Potential client impact and compatibility notes
4. Validation steps (for example, regenerate and inspect `/v3/api-docs`)