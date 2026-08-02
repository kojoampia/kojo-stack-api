---
description: "Review a security-sensitive API or authentication change in kojo-stack-api and return prioritized findings, authorization risks, JWT issues, configuration gaps, and missing tests."
name: "Review Security Change"
argument-hint: "Describe the change, affected files, or feature branch focus"
agent: "fullstack-engineer"
---
Review the described security-sensitive change in `kojo-stack-api` with a code review mindset.

Focus areas:
- `SecurityConfig` request matcher behavior
- `JwtAuthenticationFilter` and `JwtTokenProvider` correctness
- Method-level `@PreAuthorize` coverage and conflicts with HTTP-level rules
- Public versus protected `/api/v1/**` behavior
- JWT secret, expiry, and algorithm assumptions from `application.yml` and code
- Exposure through Swagger, Actuator, or health endpoints
- Whether `@EnableMethodSecurity` is still present: without it every `@PreAuthorize` in this codebase is silently ignored
- Missing or insufficient tests for authentication and authorization behavior

Project-specific review rules:
- Treat findings as the primary output. Order them by severity.
- Prefer evidence from source files over README or markdown docs when they disagree.
- Remember that this repo is stateless JWT-based and uses MongoDB, not JPA.
- Watch for the known mismatch where `SECURITY_CONFIG.md` may not match the implemented JWT algorithm.
- Watch for the nuance that HTTP-level permit rules and method-level `@PreAuthorize` can interact in non-obvious ways.

Return format:
1. Findings with severity, reasoning, and precise file references
2. Open questions or assumptions
3. Suggested tests or validation steps only after the findings

If no material findings exist, say so explicitly and call out any residual testing gaps.