---
description: "Generate or update MapStruct mappers and DTO mappings in kojo-stack-api, including field drift checks and explicit handling for nested/derived fields."
name: "Generate MapStruct Mapper"
argument-hint: "Entity/DTO pair, mapping direction, ignored fields, nested fields, and custom transforms"
agent: "fullstack-engineer"
---
Generate or update MapStruct mapping for `kojo-stack-api` with compile-safe DTO/domain conversions.

Scope:
- DTOs in `src/main/java/com/kojo/stack/api/dto`
- Mappers in `src/main/java/com/kojo/stack/api/mapper`
- Domain models in `src/main/java/com/kojo/stack/domain/model`

Rules:
- Prefer MapStruct interfaces over manual field copy logic.
- Keep naming and package placement aligned with existing mappers.
- Handle nested fields explicitly when implicit mapping is unclear.
- Call out ignored fields and explain why each is ignored.
- Preserve immutability and validation semantics expected by DTO contracts.
- If mapping depends on type conversion, add explicit mapping expressions or helper methods.

Drift checks to include:
- Identify fields present in DTO but not domain model.
- Identify fields present in domain model but not DTO.
- Flag risky silent omissions for security- or identity-related fields.
- Suggest explicit test cases for new or changed mappings.

Guardrails:
- Avoid introducing mapping logic inside controllers.
- Keep business decisions in services; mapper logic should remain transformation-focused.
- Note there is a single `Authority` type in `domain/model` and no `User` class; accounts are modelled by `Account`.

Return:
1. Mapper and DTO files changed
2. Field drift summary
3. Assumptions made
4. Validation command(s) used
