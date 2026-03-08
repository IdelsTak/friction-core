# Scalability and Partitioning Strategy

This document defines the canonical strategy for scaling ingestion, processing, and projection updates in Friction.

## Scope

- Applies to high-volume ingestion and event-driven processing across source, friction, and projection paths.
- Defines partitioning boundaries, backpressure, fan-out, replay/rebuild, and performance diagnostics.
- Complements canonical event, clustering, metrics, merge/split, and security policies.

## 1) Partition Keys and Ownership Boundaries

## Partition Keys

Primary partition key:

- `sourceId` for ingestion-stage operations

Secondary processing key:

- `frictionId` for friction-update and projection-stage operations

## Ownership Boundaries

- Ingestion workers own `sourceId` partitions.
- Friction update workers own `frictionId` partitions.
- Projection workers own read-model shard partitions (by `frictionId` hash or equivalent stable shard key).

Rules:

- Maintain in-order handling per partition key where semantic ordering is required.
- Cross-partition operations (for example merge) must use explicit coordination and idempotent operation keys.

## 2) Ingestion Throughput and Backpressure

## Throughput Strategy

- Use bounded worker pools per partition class.
- Prefer pull/poll interval controls per source rather than unbounded fan-in.
- Apply adaptive polling where supported by source characteristics.

## Backpressure Policy

- Bound ingestion queue size by source partition.
- On pressure threshold breach:
  - slow polling rate
  - drop/skip low-priority retries before dropping new valid data
- Never allow unbounded buffering in memory.

Failure safety:

- Retry transient failures with bounded attempts and backoff.
- Route retry-exhausted events to explicit failure path without blocking entire partition.

## 3) Projection Fan-Out and Update Scaling

- Projection updates are event-driven and independently scalable from write-side aggregation.
- Use per-projection consumer groups/workers where possible (for example summary and detail projections).
- Keep projection handlers idempotent by entity identity/version marker.
- Isolate slow projection sinks to prevent global throughput collapse.

Consistency target:

- write-side aggregate updates are authoritative.
- read-side projections are eventually consistent with bounded lag objectives.

## 4) Replay/Rebuild Strategy for Read Models at Scale

- Rebuild read models from durable event/log history.
- Support full rebuild and partition-scoped rebuild modes.
- Rebuild process requirements:
  - deterministic event ordering per partition
  - checkpointing for resumable progress
  - idempotent projection writes

Operational guidance:

- Run rebuild in throttled mode to avoid starving live traffic.
- Compare rebuilt projection checksums/counts against live expectations before cutover.

## 5) Performance SLO Guidance and Bottleneck Diagnostics

## Baseline SLO Categories

Track at minimum:

- ingestion latency (source pull to record accepted)
- friction update latency (record accepted to friction updated)
- projection lag (friction updated to read-model visible)
- end-to-end freshness (ingestion to UI/API visibility)

## Bottleneck Diagnostics

Capture and monitor:

- per-partition queue depth
- worker utilization and saturation
- retry volume and retry-exhaustion rate
- projection lag distribution (p50/p95/p99)
- hot-partition detection (skewed key distribution)

Diagnosis actions:

- rebalance partitions for hot-key skew
- increase worker capacity for saturated stages
- tune poll/retry/backoff settings for pressure relief

## 6) Runtime Guardrails

- No stage may rely on unbounded in-memory queues.
- All retries must be bounded and classified by transient vs terminal failures.
- Partition ownership and keying must be explicit in implementation config.
- Cross-partition mutation operations require idempotent operation identifiers.

## 7) Change Control

- Partition-key or SLO policy changes require ADR update.
- Replay/rebuild semantics changes must include compatibility impact notes for operators and projection consumers.
