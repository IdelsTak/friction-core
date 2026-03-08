# ADR-0008 Friction Descriptor Generation Policy

- Status: Accepted
- Date: 2026-03-08

## Context

`Friction.descriptor` generation was previously unspecified, leading to ambiguity, unstable naming, and inconsistent UX/API representation across implementations.

## Decision

Adopt a deterministic descriptor generation policy based on weighted phrase synthesis with explicit rewrite thresholds and anti-churn controls.

Key policy decisions:

- fixed signal set for candidate phrase scoring
- deterministic ranking and tie-breakers
- rewrite only above configured score margin (`rewrite_delta`)
- cooldown window to prevent frequent churn
- explicit sparse/noisy fallback descriptors
- locale-neutral canonical descriptor, presentation-localized downstream

## Consequences

- Descriptor behavior is now explicit and auditable.
- Same observation set yields stable descriptor output.
- Some semantic improvements may be delayed by anti-churn thresholds.
- Policy tuning must follow documented governance updates.

## Tradeoffs

- Stability controls reduce churn but can slow responsiveness to emerging language shifts.
- Deterministic constraints reduce ambiguity but may underfit nuanced semantic changes in edge cases.

## References

- [DESCRIPTOR_GENERATION.md](../DESCRIPTOR_GENERATION.md)
- [OBSERVATION_CLUSTERING.md](../OBSERVATION_CLUSTERING.md)
- [friction-technical-and-conceptual-overview.md](../friction-technical-and-conceptual-overview.md)
