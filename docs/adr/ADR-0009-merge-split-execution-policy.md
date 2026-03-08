# ADR-0009 Friction Merge/Split Execution Policy

- Status: Accepted
- Date: 2026-03-08

## Context

Merge/split behavior was partially described but lacked explicit operational and event semantics. This created ambiguity in conflict handling, retries, and projection behavior.

## Decision

Adopt a canonical merge/split execution policy defined in `MERGE_SPLIT_POLICY.md`.

Key decisions:

- Explicit merge triggers and strict execution prerequisites.
- Deterministic deduplication and recomputation during merge execution.
- Idempotent merge operations keyed by operation identity and friction pair.
- Explicit event semantics for merge success/rejection/failure.
- Split execution disabled for MVP; only split-candidate detection events are emitted.
- Atomic commit or compensating recovery requirements for failed merges.

## Consequences

- Merge behavior is now deterministic, auditable, and operationally safer.
- Consumers can rely on explicit merge outcome events.
- MVP avoids high-risk split mutation while retaining detection telemetry.
- Additional operational complexity is introduced for locking/recovery paths.

## Tradeoffs

- Disabling split execution in MVP reduces risk but delays automated cluster correction.
- Stricter prerequisites/conflict checks can reject borderline operations that might otherwise merge.

## Constraints

- No partial state mutation on rejected/conflicting operations.
- Retried operations must remain idempotent.
- Split completion events are not allowed during MVP mode.

## References

- [MERGE_SPLIT_POLICY.md](../MERGE_SPLIT_POLICY.md)
- [OBSERVATION_CLUSTERING.md](../OBSERVATION_CLUSTERING.md)
- [METRICS_SEMANTICS.md](../METRICS_SEMANTICS.md)
- [friction-technical-and-conceptual-overview.md](../friction-technical-and-conceptual-overview.md)
