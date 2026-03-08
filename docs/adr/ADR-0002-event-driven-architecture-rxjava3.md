# ADR-0002 Event-Driven Architecture with RxJava 3

- Status: Accepted
- Date: 2026-02-17

## Context

Friction ingestion, observation creation, projection updates, and failure handling are asynchronous workflows that benefit from composable stream processing.

## Decision

Adopt event-driven architecture with RxJava 3 as the reactive orchestration backbone.

## Consequences

- Clear event contracts and fail-fast paths become first-class.
- Services can scale independently across ingestion, friction analysis, and projections.
- Reactive complexity requires disciplined contract tests and deterministic projection behavior.

## References

- [friction-event-driven-architecture.md](../friction-event-driven-architecture.md)
- [friction-technical-and-conceptual-overview.md](../friction-technical-and-conceptual-overview.md)
