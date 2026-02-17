# Source Ingestion SPI (EO-Compliant)

This spec defines the **pluggable source ingestion SPI** for Friction. It is EO-aligned (no `-er`/`-or` suffixes) and supports event-driven, immutable ingestion.

## Goals

- Enable multiple ingestion sources (start with Reddit).
- Keep the core pure and framework-free.
- Provide a stable, testable contract for adapters.

## Core Concepts (from docs)

- **Source** is an aggregate with immutable configuration and behavior-first interface.
- **IngestionRecord** is append-only evidence.
- Ingestion emits domain events (`RecordIngested`, `IngestionFailed`, `DuplicateDetected`).

## Naming Rules (EO)

Avoid names ending in `-er` or `-or`. Prefer nouns that describe role or meaning.

Examples used here:
- `SourceInlet` (SPI entry point)
- `SourceConfig`
- `SourceLocator`
- `IngestionRecord`
- `IngestionFeed` (reactive stream)

## SPI Contracts (Proposed)

### 1) `SourceInlet`
Represents a source adapter that can validate configuration and produce records.

- `SourceInlet` has:
  - `SourceLocator locator()`
  - `SourceConfig config()`
  - `IngestionFeed pull()`
  - `SourceInlet rebind(SourceConfig next)`

Notes:
- `pull()` returns a **reactive feed** of immutable `IngestionRecord` values.
- `rebind` returns a new instance (immutability).

### 2) `IngestionFeed`
Represents a reactive stream of records. Implemented using RxJava3 types.

- `IngestionFeed` has:
  - `Flowable<IngestionRecord> stream()`

### 3) `SourceLocator` (Value Object)
- `type` (e.g., `REDDIT`)
- `value` (e.g., `r/javahelp`)

### 4) `SourceConfig` (Value Object)
- `ingestionInterval`
- `filterKeywords`
- `rateLimit`
- `auth` (optional)

## Event Emission Rules

Adapters do not emit domain events directly. They **produce records**; the application layer emits events based on record outcomes:

- Valid record → `RecordIngested`
- Duplicate → `DuplicateDetected`
- Invalid/corrupt → `IngestionFailed`

## Reddit Adapter (First Implementation)

The Reddit adapter will implement `SourceInlet` and provide:

- `SourceLocator(type=REDDIT, value=r/subreddit)`
- A polling or streaming `IngestionFeed` using RxJava3.

## TDD Guidance

- Contract tests at the SPI boundary.
- Behavior tests for `SourceInlet.pull()` returning immutable records.
- Validation tests for invalid locator/config.

