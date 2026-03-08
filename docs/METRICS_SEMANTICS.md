# Metrics Semantics and Edge-Case Policy

This document defines canonical metric behavior for `Friction` aggregates and projection consistency.

## Scope

- Applies to metric computation for `FrictionMetrics`.
- Defines semantics, edge-case handling, recalculation triggers, and projection expectations.
- Provides implementation-neutral rules for consistent behavior across adapters/services.

## Canonical Metrics

## 1) Prevalence

Definition:

- Count of valid, non-duplicate observations assigned to a friction within the active analysis window.

Rules:

- Increment by 1 for each accepted observation assignment.
- Do not increment for duplicates or rejected observations.
- If an observation is removed via correction workflow, recompute prevalence from persisted observation set.

## 2) Intensity

Definition:

- Normalized weighted signal of how strongly the friction is expressed.

Canonical formula:

- For each observation `o`, compute `signal(o)` from configured factors (engagement, textual severity proxy, repetition context).
- Maintain exponentially decayed aggregate:
  - `intensity_t = intensity_(t-1) * decay + signal(o_t)`
- Normalize intensity into `[0, 1]` range for projection output.

Rules:

- `decay` must be fixed per run configuration and documented.
- Missing optional engagement factors default to neutral contribution, not zeroing entire signal.

## 3) Persistence

Definition:

- Duration across first and latest accepted observation timestamps.

Canonical formula:

- `persistence = latestTimestamp - firstTimestamp`

Rules:

- Requires valid timestamps on included observations.
- If only one valid timestamp exists, persistence is `PT0S`.
- If timestamps are missing for all candidate observations, persistence is unavailable and computation must follow failure/rejection policy.

## 4) Trend and Confidence

Definition:

- Trend is slope of observation activity/intensity over time buckets.
- Confidence is fit quality for slope estimate.

Canonical method:

- Build rolling time series by fixed bucket (for example hourly/daily by config).
- Compute linear regression slope on bucketed values.
- Confidence is regression quality metric (`r^2`) in `[0, 1]`.

Rules:

- Minimum data points for trend computation: 2 buckets.
- If fewer than 2 valid buckets, trend is unavailable and handled via edge-case policy.

## Edge-Case Policy

## Insufficient Observations

- `prevalence`: valid (0 or 1+).
- `intensity`: valid if at least one accepted observation signal exists.
- `persistence`: `PT0S` when exactly one valid timestamp.
- `trend/confidence`: unavailable when fewer than 2 valid time buckets.

Handling:

- Unavailable trend/confidence must not crash pipeline.
- Emit `FrictionUpdated` with explicit "unavailable trend" metadata where allowed by projection schema.

## Missing Timestamps or Metadata

- Missing required timestamp/provenance fields for a candidate observation -> reject observation (`ObservationRejected`).
- Missing optional signal inputs (for intensity factors) -> use neutral defaults.
- Observation accepted only if mandatory invariants remain satisfied.

## Failed Recalculation Paths

- If metric recalculation fails at aggregate update stage, emit `FrictionUpdateFailed` with reason category and context.
- Do not mutate aggregate/read model on failed recalculation path.
- Keep last known valid metric snapshot available for query consistency until next successful update.

## Recalculation Triggers

Recalculate friction metrics when any of the following occurs:

- New observation accepted into a friction.
- Explicit friction merge operation completes.
- Explicit correction/removal workflow changes observation set.

Do not recalculate on:

- Duplicate detection path.
- Observation rejection path.

## Projection Update Expectations

- Successful metric recalculation -> emit `FrictionUpdated` and update `FrictionSummary` projection.
- Observation detail projections may update independently but must remain provenance-consistent.
- Failed recalculation -> emit `FrictionUpdateFailed`; projection retains previous valid metrics.
- Projection handlers must treat missing/unavailable trend fields as explicit state, not implicit errors.

## Determinism and Testability

Implementations should provide deterministic coverage for:

- one-observation case (`persistence=PT0S`, trend unavailable)
- two-plus observation trend computation
- missing required timestamp rejection
- duplicate non-impact on prevalence/intensity
- failed recalculation path preserving last valid projection state

## Change Control

- Any formula, threshold, decay, or bucket-policy change requires ADR update and spec revision.
- Backward compatibility expectations for projection consumers must be documented before rollout.
