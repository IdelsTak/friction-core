# ADR-0007 Metrics Semantics and Edge-Case Policy

- Status: Accepted
- Date: 2026-03-08

## Context

Metric behavior across prevalence, intensity, persistence, and trend was partially described but not fully formalized for edge cases. This created ambiguity for implementation and projection consistency.

## Decision

Adopt a canonical metrics semantics policy defined in `METRICS_SEMANTICS.md`.

Key decisions:

- Prevalence counts accepted, non-duplicate observations.
- Intensity uses decayed weighted signal with normalized output.
- Persistence is timestamp-span with `PT0S` single-point behavior.
- Trend/confidence uses bucketed linear regression with minimum data requirements.
- Explicit edge-case behavior for insufficient data, missing required metadata, and failed recalculation paths.
- Projection updates must preserve last valid metric snapshot on recalculation failure.

## Consequences

- Metric outputs are now implementation-consistent across services/adapters.
- Failure behavior is explicit and auditable via domain events.
- Projection consumers can rely on stable semantics for unavailable trend states.
- Formula/policy changes now require governance via ADR update.

## Constraints

- Trend/confidence are unavailable below minimum bucket count and must be represented explicitly.
- Missing required provenance/timestamp fields trigger rejection, not partial metric updates.
- Failed recalculation paths must not mutate persisted metric projections.

## References

- [METRICS_SEMANTICS.md](../METRICS_SEMANTICS.md)
- [friction-metrics-incremental-computation.md](../friction-metrics-incremental-computation.md)
- [friction-technical-and-conceptual-overview.md](../friction-technical-and-conceptual-overview.md)
