---
description: "Use when creating or updating unit tests, integration tests, controller tests, security tests, MockMvc tests, or Spring Boot test coverage for kojo-stack-api."
name: "Java Test Guidelines"
applyTo: "src/test/java/**/*.java"
---
# Java Test Guidelines

- This module currently has little to no established test suite under `src/test/java`, so prefer small, focused tests over trying to imitate a missing local pattern.
- Mirror production packages under `src/test/java/com/kojo/stack/...` and keep test names explicit, such as `AuthControllerTest`, `ProfileServiceTest`, or `ExperienceRepositoryIT`.
- For controller behavior, prefer `@WebMvcTest` plus `MockMvc` when the slice is narrow; use `@SpringBootTest` only when security, serialization, or multi-bean wiring is part of the behavior under test.
- For service logic, prefer unit tests with mocked collaborators unless the behavior depends on Spring configuration, caching, or repository integration.
- For repository or persistence integration, test MongoDB behavior. Do not assume PostgreSQL even though some stale docs and a leftover Testcontainers dependency suggest otherwise.
- When security rules matter, test both allowed and denied cases and verify the actual combination of URL rules and method-level `@PreAuthorize` checks.
- Keep fixtures small and readable. Reuse builder patterns from production models where available rather than assembling oversized object graphs.
- Run the narrowest relevant Maven test command for the files you changed, then fall back to `./mvnw clean package` when broader validation is warranted.
- If you add integration tests that rely on external infrastructure, document the requirement in the test class and keep the setup local to the test instead of depending on ambient machine state.
