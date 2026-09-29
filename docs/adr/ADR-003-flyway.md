# ADR-003 — Flyway

**Status:** Accepted

Flyway é a autoridade do schema. Hibernate usa `ddl-auto=validate`; alterações estruturais exigem migration versionada.
