# Observation Clustering Strategy

This document defines the canonical strategy for assigning `Observation` records to `Friction` aggregates.

## Scope

- Applies to clustering behavior in `FrictionService`.
- Defines assignment, deduplication, merge/split criteria, confidence thresholds, and failure paths.
- Complements (does not replace) event and read-model contracts in canonical docs.

## Inputs and Feature Signals

Each candidate `Observation` is scored against existing `Friction` aggregates using a weighted feature set:

- Semantic similarity of normalized text content (embedding cosine similarity)
- Lexical overlap of keyphrases/tokens after normalization
- Provenance proximity (source/subreddit/category alignment)
- Temporal proximity (observation timestamp relative to recent cluster activity)

### Normalization Baseline

- Lowercase and whitespace normalization
- URL canonicalization for provenance URI
- Remove formatting noise and platform boilerplate
- Preserve domain terms and product names as meaningful tokens

## Assignment Pipeline

1. Validate required observation fields (content, timestamp, provenance URI/source).
2. Run deduplication check (hard duplicate rejection first).
3. Score candidate observation against active frictions.
4. Select best candidate friction and assignment confidence.
5. Apply threshold rules:
   - high confidence -> attach to existing friction
   - medium confidence -> attach with review marker
   - low confidence -> create new friction
6. Emit events for accepted/rejected paths.

## Deduplication Logic

### Hard Duplicate

Reject as duplicate if any of the following match an existing observation:

- Same canonical provenance URI
- Same external source identifier
- Same normalized content hash and timestamp bucket

Hard duplicates must emit `DuplicateDetected` and must not update friction metrics.

### Near Duplicate

If semantic similarity is extremely high (`>= 0.95`) and provenance source/time are near-identical, treat as duplicate and emit `ObservationRejected` with duplicate reason.

## Confidence Thresholds and Fallback

Use three confidence bands:

- `>= 0.78` high confidence: assign to best-matching existing friction
- `0.60 - 0.77` medium confidence: assign to existing friction and mark as low-confidence assignment metadata
- `< 0.60` low confidence: create a new friction aggregate

Fallback rules:

- If no candidate friction exists, create a new friction.
- If feature extraction fails, reject observation and emit failure path event.
- If scoring fails unexpectedly, emit `FrictionUpdateFailed` with context.

## Merge and Split Criteria

### Merge Criteria

Two frictions become merge candidates when all are true:

- centroid/descriptor similarity `>= 0.85`
- overlapping key observations across a rolling window
- trend direction alignment over recent intervals

Merge execution occurs as an explicit service action and emits standard `FrictionUpdated` events.

### Split Criteria

A friction becomes split candidate when:

- internal semantic variance exceeds configured threshold
- two stable sub-clusters persist across multiple windows

Split behavior is optional for MVP; if disabled, mark for review metadata only.

## Failure and Rejection Paths

Use explicit fail-fast outcomes:

- Missing required fields -> `ObservationRejected`
- Hard/near duplicate -> `DuplicateDetected` or `ObservationRejected`
- Feature extraction failure -> `ObservationRejected`
- Scoring/runtime failure during aggregate update -> `FrictionUpdateFailed`

Failure payloads must include actionable context (source, observation identifiers, reason category).

## Event Emission Summary

- Success assignment -> `ObservationCreated`, then `FrictionUpdated`
- Duplicate path -> `DuplicateDetected` or `ObservationRejected`
- Invalid observation -> `ObservationRejected`
- Processing failure -> `FrictionUpdateFailed`

## Testability Contract

Implementations should be testable through deterministic cases:

- high-confidence assignment to existing friction
- medium-confidence assignment path
- low-confidence new-friction creation path
- hard duplicate rejection
- malformed observation rejection
- scoring failure emitting `FrictionUpdateFailed`

## Tuning Notes

- Thresholds above are canonical defaults and may be tuned via config with ADR-backed change.
- Any threshold or feature-weight change must preserve event compatibility and be documented.
