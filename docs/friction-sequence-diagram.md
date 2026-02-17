# Friction - Sequence Diagram

The Friction **full sequence diagram** showing the **end-to-end flow** from user configuring sources to friction metrics surfacing in the UI. This captures the **dynamic behavior** of the application while keeping DDD + EO principles intact.

### Key Points:

1. **Behavior-first:** Aggregates (`Source`, `Friction`) never expose internal state: services handle orchestration.
2. **Immutable flow:** Each addition (`addObservation`) returns a new friction instance with recalculated metrics.
3. **Traceable evidence:** `ObservationDetail` links back to `IngestionRecord` and provenance info.
4. **Projections / read models:** `FrictionSummary` and `ObservationDetail` keep UI fast and decoupled from aggregate internals.
5. **Scalable design:** You can add new sources, metrics, or filters without touching aggregates.

```mermaid
sequenceDiagram
    participant User
    participant SourceService
    participant IngestionRecordStore
    participant FrictionService
    participant Friction
    participant MetricsCalculator
    participant FrictionSummaryStore
    participant UI

    %% 1. User configures a source
    User->>SourceService: configureSource(locator, config)
    SourceService->>SourceService: validate locator & config
    SourceService-->>User: confirmation

    %% 2. Ingestion triggered (manual or scheduled)
    SourceService->>IngestionRecordStore: ingest(sourceId)
    IngestionRecordStore->>IngestionRecordStore: persist raw content
    IngestionRecordStore-->>SourceService: list<IngestionRecord>

    %% 3. Observations creation
    SourceService->>FrictionService: createObservations(records)
    FrictionService->>Friction: addObservation(record)
    Friction->>MetricsCalculator: recalcMetrics()
    MetricsCalculator-->>Friction: updated metrics
    FrictionService-->>FrictionService: updated friction

    %% 4. Update read models / projections
    FrictionService->>FrictionSummaryStore: updateFrictionSummary(friction)
    FrictionSummaryStore-->>UI: updated friction summaries

    %% 5. UI presents evidence
    UI-->>User: display friction clusters & top observations
    UI->>FrictionSummaryStore: drillDown(frictionId)
    FrictionSummaryStore-->>UI: ObservationDetail[]
    UI-->>User: show provenance & excerpts

```

