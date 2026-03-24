---
description: "Generate or extend a MongoRepository for kojo-stack-api with Spring Data derived query methods, optional @Query clauses, and practical MongoDB index recommendations."
name: "Generate MongoDB Repository"
argument-hint: "Entity name, query/filter needs, sort/pagination requirements, and expected access patterns"
agent: "fullstack-engineer"
---
Generate or update a MongoDB repository for `kojo-stack-api` using active project conventions.

Inputs to infer from the user argument:
- Target domain document and package
- Query and filter requirements
- Sort, pagination, and optional aggregation needs
- Expected access frequency and cardinality for index recommendations

Implementation requirements:
- Use active repositories under `src/main/java/com/kojo/stack/domain/repository`.
- Extend `MongoRepository<Entity, String>` unless there is a justified existing pattern to follow.
- Prefer Spring Data derived query methods first.
- Use `@Query` only when method naming becomes unreadable or cannot express the required filter.
- Keep method names explicit and aligned with the field names on the document class.
- If a method is likely hot-path, propose index additions on the associated document.

Index recommendation rules:
- Recommend indexes only when backed by a specific query pattern.
- Prefer single-field indexes for simple predicates.
- Recommend compound indexes only when field order follows the actual filter and sort pattern.
- Call out write-amplification trade-offs for each index suggestion.
- Indicate where index annotations should live on the document class.

Guardrails:
- Do not introduce JPA or SQL patterns.
- Avoid legacy repository package `src/main/java/com/kojo/stack/repository` unless explicitly requested.
- Verify duplicate class names (`Authority`, `User`) before finalizing imports.

Return:
1. Proposed repository interface changes
2. Optional document index changes
3. Assumptions and performance trade-offs
4. Validation command(s) used
