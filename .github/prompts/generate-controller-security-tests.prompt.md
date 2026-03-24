---
description: "Generate focused Spring tests for kojo-stack-api controllers and security behavior, including MockMvc authorization scenarios and JWT-related access verification."
name: "Generate Controller Security Tests"
argument-hint: "Controller/endpoints to test, expected roles, public vs protected behavior, and edge cases"
agent: "fullstack-engineer"
---
Generate tests for controller and security behavior in `kojo-stack-api`.

Target outcomes:
- Verify endpoint behavior and status codes
- Verify authorization rules for anonymous, authenticated, and role-restricted access
- Verify any JWT-dependent behavior involved in request authorization

Test design rules:
- Prefer focused `@WebMvcTest` + `MockMvc` for controller and authorization matrix checks.
- Use `@SpringBootTest` only when full security wiring or cross-bean integration is required.
- Name tests clearly by behavior, for example `shouldReturnForbiddenForNonAdminWhenListingAccounts`.
- Cover success and failure paths for each protected endpoint.
- Keep fixtures minimal and readable.

Project-specific guardrails:
- Do not assume an established large test baseline in `src/test/java`; create concise, isolated tests.
- Use MongoDB-aware assumptions and avoid SQL/JPA-only assertions.
- If behavior depends on both URL security rules and `@PreAuthorize`, include tests for both dimensions.

Return:
1. Added or updated test files
2. Behavior matrix covered (public/authenticated/admin)
3. Validation command(s) run and any remaining gaps