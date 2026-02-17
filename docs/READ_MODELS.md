# Read Model Interfaces (Pluggable Persistence)

This spec defines read-model interfaces that allow **in-memory** or **DB-backed** implementations without changing core logic.

## Goals

- Keep read models decoupled from aggregates.
- Enable CQRS projections from domain events.
- Allow multiple persistence strategies.

## Read Models (from docs)

- `FrictionSummary`
- `ObservationDetail`

## EO-Compliant Interface Names

Avoid `-er` or `-or` suffixes. Use nouns for role or meaning.

### Suggested Interfaces

- `FrictionSummaryStore`
- `ObservationDetailStore`

## Interface Sketch

### `FrictionSummaryStore`
- `FrictionSummaryStore save(FrictionSummary summary)`
- `Optional<FrictionSummary> find(FrictionId id)`
- `List<FrictionSummary> top(int limit)`
- `FrictionSummaryStore clear()`

### `ObservationDetailStore`
- `ObservationDetailStore save(ObservationDetail detail)`
- `List<ObservationDetail> byFriction(FrictionId id)`
- `ObservationDetailStore clear()`

Notes:
- Methods return new instances to preserve immutability (EO style).
- If operationally heavy, this can be relaxed for DB-backed adapters, but the core interface remains stable.

## Implementations

### In-Memory (MVP)
- Simple maps/lists
- Deterministic for tests

### DB-Backed (Future)
- JPA/JDBC/R2DBC
- Same interface contract

## Projection Flow

- Domain events → projection layer → read model stores
- Event handlers build `FrictionSummary` and `ObservationDetail`

