# Friction Merge/Split Execution Policy

This document defines canonical merge/split execution behavior for `Friction` aggregates.

## Scope

- Applies to merge/split orchestration in `FrictionService`.
- Defines triggers, prerequisites, conflict/idempotency rules, events, and failure recovery.
- Complements clustering and metrics semantics policies.

## Merge Policy

## Merge Triggers

A merge operation may be initiated when any of the following conditions are met:

- automated candidate detection meets merge threshold policy
- explicit operator/workflow request references two compatible frictions
- correction workflow identifies duplicate semantic clusters

## Merge Prerequisites

All must hold before execution:

- both frictions exist and are active
- no terminal conflict lock/state on either friction
- merge candidate confidence is above configured minimum threshold
- observation identity sets can be deduplicated deterministically

## Merge Execution Semantics

1. Lock merge scope for involved friction identities.
2. Build merged observation set with deduplication.
3. Recompute metrics and descriptor using canonical policies.
4. Persist merged friction as new authoritative state.
5. Mark source friction identities as merged/superseded.
6. Emit merge outcome events.

## Split Policy

## Split Triggers

Split candidate criteria include:

- sustained internal semantic divergence above threshold
- stable sub-clusters across configured time windows
- explicit correction workflow request

## MVP Status

- Split execution is **disabled for MVP**.
- MVP behavior: detect and mark split candidates with review metadata only.
- No automatic split mutation is applied during MVP.

## Conflict Handling and Idempotency

## Conflict Handling

Conflicts include:

- stale operation against already-merged friction
- overlapping operations on same friction identity
- version/sequence mismatch at write boundary

Conflict policy:

- reject stale/conflicting operation with explicit conflict reason
- do not partially mutate involved frictions on conflict

## Idempotency Expectations

- Merge request must be idempotent by operation key and friction pair identity.
- Retried identical merge operations must converge to same terminal state.
- Duplicate operation replays should return prior successful outcome metadata when available.

## Event Emission Semantics

## Merge Success

Emit in order:

- `FrictionMerged` (new event): `{targetFrictionId, sourceFrictionIds[], operationId}`
- `FrictionUpdated`: merged metrics/descriptor payload for target friction
- projection update events from projection service

## Merge Rejection/Failure

- conflict/stale request -> `FrictionMergeRejected` (new event)
- execution failure after start -> `FrictionMergeFailed` (new event)
- no projection mutation on rejected/failed merge paths

## Split Candidate (MVP)

- emit `FrictionSplitCandidateDetected` (new event) with candidate metadata
- no `FrictionSplitCompleted` during MVP

## Rollback/Recovery Behavior

- Merge execution must be atomic at persistence boundary where supported.
- If atomic commit is unavailable, use compensating recovery marker and restore prior authoritative state.
- On failure after partial work:
  - mark operation as failed with recovery-needed context
  - prevent further merge attempts on same scope until recovery checkpoint clears
- Recovery/replay must preserve idempotency guarantees.

## Observability Requirements

Capture:

- merge attempts/success/rejected/failed counts
- split-candidate detection counts
- conflict reason distribution
- operation latency and retry metrics

Required operation context:

- `operationId`
- source/target friction identities
- reason category
- attempt count

## Change Control

- Trigger thresholds, event contracts, or MVP split status changes require ADR update.
- Backward compatibility impact on projections/consumers must be documented before rollout.
