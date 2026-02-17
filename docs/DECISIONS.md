# Design Decisions Log

This log captures project-level design choices and their rationale, grounded in the existing Friction docs in this directory.

## 2026-02-17 — Architecture & Repo Strategy

**Decision:** Separate repositories for core Java backend and JavaFX UI.
- **Rationale:** Maintains a pure, framework-free core and isolates UI concerns. Aligns with the docs' emphasis on clear boundaries (DDD, CQRS, event-driven), and supports independent evolution of UI/read models.
- **References:** `friction-technical-and-conceptual-overview.md`, `friction-product-level-uml.md`, `friction-event-driven-architecture.md`.

**Decision:** Highly modular backend with a clean architecture style.
- **Rationale:** The docs stress immutable aggregates, event-driven processing, and read-model projections. A modular layout enforces separation between domain, application services, adapters, and API.
- **References:** `friction-technical-and-conceptual-overview.md`, `friction-first-domain-model-ddd-eo.md`, `friction-event-driven-architecture.md`.

## 2026-02-17 — Language, Runtime, and Build

**Decision:** Java 25.
- **Rationale:** Consistent with a modern, long-term JVM stack for an event-driven system with strong typing and immutability patterns.

**Decision:** Maven build system.
- **Rationale:** Maven supports multi-module builds cleanly and is common in Java DDD/CQRS architectures.

## 2026-02-17 — Eventing & Reactive Core

**Decision:** RxJava 3 for event-driven orchestration.
- **Rationale:** The system is explicitly event-driven and needs composable streams for ingestion, observation creation, and projections. RxJava 3 is a stable reactive backbone for this architecture.
- **References:** `friction-event-driven-architecture.md`, `friction-technical-and-conceptual-overview.md`.

## 2026-02-17 — Testing & Quality

**Decision:** JUnit 5 + Hamcrest + JaCoCo.
- **Rationale:** JUnit 5 is the modern standard for Java testing; Hamcrest provides readable matchers; JaCoCo enables coverage visibility. Supports TDD workflow.

## 2026-02-17 — UI Architecture

**Decision:** MVU (Model-View-Update) for JavaFX UI.
- **Rationale:** MVU enforces unidirectional data flow and explicit state transitions, mirroring the system's event-driven and immutable design ethos.
- **References:** `friction-product-level-uml.md`, `friction-sequence-diagram.md`.

---

## Next Decisions To Make

1. Backend module boundaries and names (domain, application, adapters, api, launcher).
2. Event contract format and versioning strategy.
3. Read-model storage choice (in-memory vs persistent for MVP).
4. Minimum MVP ingestion source (Reddit only vs pluggable sources from day one).
5. API surface for FrictionSummary and ObservationDetail.

## 2026-02-17 — Repo Boundaries & Extension Strategy

**Decision:** Use 2–3 standalone repos aligned to major boundaries.
- **Rationale:** Keeps core logic isolated from UI and adapters, simplifies independent release cadence, and aligns with the docs’ separation of domain, application services, and projections.
- **References:** `friction-technical-and-conceptual-overview.md`, `friction-product-level-uml.md`.

**Decision:** Provide SPI/API for pluggable ingestion sources; start with Reddit.
- **Rationale:** The architecture is domain-agnostic and source-extensible. Starting with Reddit preserves focus while honoring the event-driven ingestion model.
- **References:** `friction-technical-and-conceptual-overview.md`, `friction-event-driven-architecture.md`.

**Decision:** Read models defined by interfaces to allow in-memory or DB-backed implementations.
- **Rationale:** CQRS/projection design expects independent read model implementations. Interfaces preserve modularity and make MVP storage choice reversible.
- **References:** `friction-technical-and-conceptual-overview.md`, `friction-product-level-uml.md`.

## 2026-02-17 — Versioning Strategy

**Decision:** Versioning labels driven by issue/PR labels.
- **Rationale:** Aligns release scope with explicit labels to distinguish breaking changes, new features, fixes, and pre-release milestones.
- **Labels:**
  - `version:major` (breaking change)
  - `version:minor` (backward-compatible feature)
  - `version:patch` (bugfix/refactor)
  - `version:alpha` (alpha pre-release)
  - `version:beta` (beta pre-release)
  - `version:rc` (release candidate)

## 2026-02-17 — EO Naming Constraints

**Decision:** Avoid class names ending in `-er` or `-or` to align with Elegant Objects (EO) style.
- **Rationale:** EO discourages procedural or actor-like naming. This reinforces object-as-behavior and supports immutability and behavior-first design.
- **Implication:** Avoid names like `Validator`, `Fetcher`, `Controller`, `Processor`. Prefer names that describe meaning or role (e.g., `SourceConfig`, `Ingestion`, `Projection`, `FrictionPipeline`).
- **References:** `friction-technical-and-conceptual-overview.md` (EO compliance emphasis).
