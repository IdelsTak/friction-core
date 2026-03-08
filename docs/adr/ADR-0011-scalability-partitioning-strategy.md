# ADR-0011 Scalability and Partitioning Strategy

- Status: Accepted
- Date: 2026-03-08

## Context

Scalability and partitioning guidance was previously high-level and non-operational. High-volume ingestion and projection workloads need explicit keying, backpressure, and rebuild policy to remain predictable.

## Decision

Adopt canonical scalability/partitioning policy in `SCALABILITY_PARTITIONING.md`.

Key decisions:

- partition ingestion by `sourceId` and friction/projection processing by `frictionId`
- enforce bounded queues and explicit backpressure controls
- scale projections independently with idempotent handlers
- define deterministic, checkpointed replay/rebuild modes for read models
- establish SLO categories and bottleneck diagnostics as operational baseline

## Consequences

- Horizontal scaling behavior is now explicit and auditable.
- Operators gain concrete diagnostics and tuning controls.
- Implementation complexity increases for partition coordination and rebuild tooling.

## Tradeoffs

- Strong partition ordering simplifies correctness but can reduce raw throughput on hot keys.
- Conservative backpressure protects stability but may increase latency under bursty load.

## Constraints

- No unbounded buffering.
- Retries must be bounded and classified.
- Cross-partition operations require explicit idempotent coordination.

## References

- [SCALABILITY_PARTITIONING.md](../SCALABILITY_PARTITIONING.md)
- [friction-event-driven-architecture.md](../friction-event-driven-architecture.md)
- [friction-technical-and-conceptual-overview.md](../friction-technical-and-conceptual-overview.md)
