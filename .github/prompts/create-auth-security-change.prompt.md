---
description: "Implement authentication or authorization features in kojo-stack-api, including JWT handling, SecurityConfig request rules, and method-level @PreAuthorize updates with focused verification."
name: "Create Auth Security Change"
argument-hint: "Describe the auth feature or rule change, affected endpoints, roles, and token behavior"
agent: "fullstack-engineer"
---
Implement an authentication or authorization change for `kojo-stack-api` using the existing stateless JWT model.

Scope to analyze before editing:
- `src/main/java/com/kojo/stack/config/SecurityConfig.java`
- `src/main/java/com/kojo/stack/security/JwtAuthenticationFilter.java`
- `src/main/java/com/kojo/stack/security/JwtTokenProvider.java`
- Affected controllers and service methods with `@PreAuthorize`
- Relevant keys in `src/main/resources/application.yml` and profile overrides

Implementation rules:
- Keep the app stateless; do not introduce session-based auth.
- Keep endpoints under `/api/v1/**` unless explicitly requested otherwise.
- Apply defense-in-depth: align HTTP request matcher rules with method-level authorization.
- Preserve or improve clear role semantics (`ROLE_*`) and avoid ambiguous broad grants.
- If token behavior changes (algorithm, claims, expiry, secret usage), update code and configuration coherently.
- Prefer source code truth when markdown docs conflict with implementation details.

Required output behavior:
1. Summarize changes made with file paths
2. Explain why each security decision was applied
3. Call out potential regressions (privilege escalation, broken access, token invalidation)
4. Provide focused validation steps and tests to run

Validation checklist:
- Anonymous access where expected
- Authenticated access where expected
- Forbidden responses for insufficient roles
- Token generation and validation behavior
- Any changed actuator or docs endpoint exposure
