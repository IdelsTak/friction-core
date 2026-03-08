# Friction Descriptor Generation Policy

This document defines canonical rules for generating and updating `Friction.descriptor`.

## Scope

- Applies to descriptor synthesis in `FrictionService`.
- Defines deterministic generation, update/rewrite policy, and stability controls.
- Complements canonical clustering and metrics specs.

## Input Signals for Descriptor Synthesis

Descriptor candidates are derived from accepted observations assigned to a friction using weighted signals:

- salient keyphrases (frequency and distinctiveness)
- semantic centroid terms from cluster content embeddings
- provenance context terms (domain/source cues when meaningful)
- recency weighting to surface active phrasing

Normalization baseline:

- lowercase normalization for scoring only
- punctuation/noise reduction for tokenization
- preserve meaningful product/domain terms
- remove platform boilerplate and stopword-only phrases

## Deterministic Candidate Pipeline

1. Build candidate phrase pool from latest valid observation set.
2. Score phrases using fixed weighted formula.
3. Rank descending and apply tie-breakers deterministically:
   - higher distinctiveness first
   - then higher frequency
   - then lexical sort for stable output
4. Select top candidate as descriptor.

Output constraints:

- concise phrase (target 3-12 words)
- avoid URLs, user handles, and platform-specific markup
- avoid purely sentiment-only descriptors (for example "users are angry")

## Update/Rewrite Policy

Descriptor recalculation triggers:

- new observation accepted
- friction merge completed
- correction workflow modifies observation set

Rewrite threshold policy:

- only replace current descriptor when new candidate score exceeds current descriptor score by configured delta (`rewrite_delta`)
- if below delta, retain existing descriptor to reduce churn

Hard rewrite paths:

- descriptor becomes invalid under policy constraints
- merge operation produces dominant new cluster semantics

## Stability Rules (Anti-Churn)

- enforce minimum update interval (`descriptor_cooldown`) unless hard rewrite path applies
- require score margin over existing descriptor (`rewrite_delta`) for non-hard rewrites
- keep previous descriptor history for auditability
- deterministic tie-breakers must yield same descriptor for same input state

## Sparse/Noisy Cluster Fallback Behavior

Sparse fallback:

- if insufficient high-quality phrases, use template:
  - `Recurring issue in <domain/source>`
- mark fallback metadata for later refinement

Noisy fallback:

- if top candidates are low-confidence/noise, keep prior descriptor when available
- otherwise use neutral fallback descriptor and emit low-confidence marker

## Localization and Formatting Boundaries

- canonical descriptor is stored as locale-neutral text
- localization/translation is a presentation concern outside core descriptor generation
- UI may format display casing; core output remains normalized policy output

## Auditability and Traceability

For each descriptor update, retain metadata:

- selected candidate phrase
- score and score margin vs prior descriptor
- trigger reason (new observation, merge, correction)
- timestamp and friction identity

This metadata supports reproducibility and debugging of descriptor changes.

## Failure Handling

- failure in descriptor synthesis should not mutate descriptor state
- emit failure context alongside standard failure path event handling (`FrictionUpdateFailed` where applicable)
- retain last valid descriptor on synthesis failure

## Change Control

- Weight, threshold, or fallback policy changes require ADR-backed update.
- Determinism guarantees must be preserved after policy changes.
