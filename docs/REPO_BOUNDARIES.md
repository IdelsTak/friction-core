# Multi-Repo Boundary Plan

This document defines the 2–3 standalone repos and their responsibilities.

## 1) `friction-core`

**Purpose:** Domain + application logic + SPI contracts.

Includes:
- Aggregates (`Source`, `Friction`)
- Entities (`Observation`, `IngestionRecord`)
- Value objects (`FrictionMetrics`, `SourceLocator`, `SourceConfig`, `TemporalTrend`)
- Domain events
- RxJava3 pipelines
- SPI contracts (`SourceInlet`, read model stores)

Excludes:
- UI
- Ingestion adapters
- Persistence implementations

## 2) `friction-adapters`

**Purpose:** Concrete adapters for ingestion and persistence.

Includes:
- Reddit ingestion adapter (first)
- In-memory read-model stores
- DB-backed read-model stores (future)
- Integration wiring between adapters and core

## 3) `friction-ui`

**Purpose:** JavaFX UI using MVU.

Includes:
- View models
- Update loop
- API/read model consumption
- No domain logic

## Why this split

- Keeps the core clean and framework-free.
- Enables independent release cadence per repo.
- Supports plug-in ingestion sources.

