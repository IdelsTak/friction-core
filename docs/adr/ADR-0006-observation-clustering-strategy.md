# ADR-0006 Observation Clustering Strategy

- Status: Accepted
- Date: 2026-03-08

## Context

Observation clustering was previously an open question in the technical overview. Without a defined strategy, friction assignment can drift, reduce reproducibility, and weaken auditability.

## Decision

Adopt a weighted feature clustering strategy with explicit confidence thresholds and fail-fast rejection paths.

Key policy choices:

- Weighted signals: semantic similarity, lexical overlap, provenance proximity, temporal proximity
- Hard duplicate rejection before scoring
- Confidence bands:
  - `>= 0.78`: assign to existing friction
  - `0.60 - 0.77`: assign with low-confidence marker
  - `< 0.60`: create new friction
- Explicit merge criteria and optional split criteria for non-MVP phases
- Failure/rejection outcomes mapped to canonical events (`ObservationRejected`, `DuplicateDetected`, `FrictionUpdateFailed`)

## Consequences

- Clustering behavior is now explicit and testable.
- Assignment outcomes are more consistent and auditable.
- Threshold tuning now requires governance and documentation updates.
- Medium-confidence assignments introduce a review/tuning surface that must be monitored.

## Tradeoffs

- Fixed thresholds improve consistency but may require periodic retuning by domain.
- Additional feature extraction/scoring logic increases implementation complexity.
- Split behavior deferred for MVP limits automatic correction of heterogeneous frictions.

## References

- [OBSERVATION_CLUSTERING.md](../OBSERVATION_CLUSTERING.md)
- [friction-technical-and-conceptual-overview.md](../friction-technical-and-conceptual-overview.md)
- [friction-event-driven-architecture.md](../friction-event-driven-architecture.md)
