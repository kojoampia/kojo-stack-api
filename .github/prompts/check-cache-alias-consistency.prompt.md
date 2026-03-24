---
description: "Check cache usage consistency in kojo-stack-api by comparing @Cacheable/@CacheEvict names with ehcache.xml aliases and propose precise fixes."
name: "Check Cache Alias Consistency"
argument-hint: "Optional scope: package, class, or feature area to scan"
agent: "fullstack-engineer"
---
Review cache configuration consistency for `kojo-stack-api`.

Primary goal:
- Ensure every cache name used in annotations matches an alias in `src/main/resources/ehcache.xml`.

What to check:
- `@Cacheable`, `@CacheEvict`, `@CachePut`, and `@Caching` usages
- Alias definitions and TTL/capacity entries in `ehcache.xml`
- Duplicate aliases or near-miss naming mismatches
- Caches defined but never referenced from code

Rules:
- Prefer minimal, precise fixes over broad refactors.
- Keep existing cache semantics unless there is a clear defect.
- If adding a new alias, align naming with current project style.
- Flag potential runtime failures caused by missing aliases.

Return format:
1. Findings ordered by severity with file references
2. Concrete fixes applied or proposed
3. Remaining risks (for example, stale or overly broad cache invalidation)
4. Validation command(s) or checks performed
