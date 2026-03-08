# ADR-0004 Read Model Interfaces as Pluggable Contracts

- Status: Accepted
- Date: 2026-02-17

## Context

Read-side consumers (UI/API) require stable query contracts while persistence implementations may evolve from in-memory to DB-backed storage.

## Decision

Define shared read models behind stable interfaces (`FrictionSummaryStore`, `ObservationDetailStore`) in core contracts.

## Consequences

- In-memory and DB-backed implementations can evolve independently.
- Projection logic remains decoupled from storage technology.
- Contract compatibility becomes a cross-repo integration boundary.

## References

- [READ_MODELS.md](../READ_MODELS.md)
- [friction-technical-and-conceptual-overview.md](../friction-technical-and-conceptual-overview.md)
