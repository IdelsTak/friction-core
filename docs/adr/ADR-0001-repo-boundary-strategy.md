# ADR-0001 Repo Boundary Strategy

- Status: Accepted
- Date: 2026-02-17

## Context

Friction separates domain/application logic, adapters, and UI concerns. Shared architecture guidance must remain stable while each repo evolves independently.

## Decision

Use three repos with explicit responsibilities:

- `friction-core`: domain, application orchestration, shared contracts
- `friction-adapters`: concrete ingestion and persistence implementations
- `friction-ui`: JavaFX MVU application

## Consequences

- Core remains framework-light and reusable.
- Adapter and UI release cadence can differ.
- Shared docs must stay canonical in `friction-core/docs` to avoid drift.

## References

- [REPO_BOUNDARIES.md](../REPO_BOUNDARIES.md)
- [friction-technical-and-conceptual-overview.md](../friction-technical-and-conceptual-overview.md)
