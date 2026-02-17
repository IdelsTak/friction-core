# Friction - Event-driven Architecture

This design is fully **DDD-aligned**, **EO-compliant**, and allows **horizontal scaling**: adding new sources, event handlers, or read models does not require changes to aggregates or the ingestion pipeline.

## **1. Event Types (including fail-fast)**

| Event                       | Source            | Payload                                                  | Notes                                       |
| --------------------------- | ----------------- | -------------------------------------------------------- | ------------------------------------------- |
| `SourceConfigured`          | SourceService     | `{sourceId, locatorType, locatorValue, config}`          | Success path                                |
| `SourceConfigurationFailed` | SourceService     | `{locator, reason}`                                      | Fail-fast invalid locator/config            |
| `IngestionRequested`        | SourceService     | `{sourceId, requestId, timestamp}`                       | Trigger ingestion                           |
| `IngestionFailed`           | IngestionService  | `{sourceId, reason}`                                     | Invalid/malformed/corrupt data              |
| `RecordIngested`            | IngestionService  | `{recordId, sourceId, content, timestamp}`               | Immutable raw record                        |
| `DuplicateDetected`         | IngestionService  | `{sourceId, recordId}`                                   | Duplicate prevention                        |
| `ObservationCreated`        | FrictionService   | `{observationId, recordId, sourceId, content, metadata}` | Normal path                                 |
| `ObservationRejected`       | FrictionService   | `{recordId, reason}`                                     | Duplicate, invalid content, metrics failure |
| `FrictionUpdated`           | FrictionService   | `{frictionId, addedObservations[], metrics}`             | Success path                                |
| `FrictionUpdateFailed`      | FrictionService   | `{frictionId, reason}`                                   | Metrics calculation failure                 |
| `FrictionSummaryUpdated`    | ProjectionService | `{frictionId, summary, topObservations}`                 | Projection updated                          |
| `ObservationDetailUpdated`  | ProjectionService | `{frictionId, observationDetails[]}`                     | Projection updated                          |

## **2. Enhanced Sequence Diagram with Fail-Fast Paths**

```mermaid
sequenceDiagram
    participant User
    participant SourceService
    participant IngestionService
    participant FrictionService
    participant ProjectionService
    participant UI

    %% 1. User configures a source
    User->>SourceService: configureSource(locator, config)
    SourceService->>SourceService: validate locator & config
    alt valid configuration
        SourceService-->>User: confirmation
        SourceService->>IngestionService: SourceConfigured(sourceId)
    else invalid configuration
        SourceService-->>User: SourceConfigurationFailed(reason)
    end

    %% 2. Ingestion triggered
    IngestionService->>IngestionService: ingest(sourceId)
    alt record valid
        IngestionService-->>FrictionService: RecordIngested(recordId, sourceId, content)
    else duplicate record
        IngestionService-->>ProjectionService: DuplicateDetected(sourceId, recordId)
    else malformed/corrupt
        IngestionService-->>ProjectionService: IngestionFailed(sourceId, reason)
    end

    %% 3. Observation creation
    FrictionService->>FrictionService: createObservation(record)
    alt valid observation
        FrictionService-->>FrictionService: ObservationCreated(observationId)
        FrictionService->>FrictionService: addObservation(observation) -> new Friction
        alt metrics success
            FrictionService-->>ProjectionService: FrictionUpdated(frictionId, metrics)
        else metrics failure
            FrictionService-->>ProjectionService: FrictionUpdateFailed(frictionId, reason)
        end
    else invalid/duplicate observation
        FrictionService-->>ProjectionService: ObservationRejected(recordId, reason)
    end

    %% 4. Update projections
    ProjectionService->>UI: FrictionSummaryUpdated(summary)
    ProjectionService->>UI: ObservationDetailUpdated(observations)
    UI-->>User: display friction clusters & details
```
## **3. Aggregate & Service Reactions**

**Friction Aggregate**

- `addObservation(obs)` → returns new instance, throws exception if metrics fail
- No internal mutation; old instance preserved
- Metrics recalculation pure; exceptions propagate to service

**Observation**

- Immutable; rejected at creation if duplicate or invalid content
- Links to raw `IngestionRecord` for full traceability

**SourceService**

- Throws `SourceConfigurationFailed` event if locator/config invalid
- Validation early, fail-fast prevents downstream ingestion

**IngestionService**

- Rejects duplicates (`DuplicateDetected`)
- Rejects corrupt/malformed records (`IngestionFailed`)
- All failures emitted as events for monitoring/UI

**ProjectionService**

- Listens to success/failure events
- Updates read models only on valid paths
- Logs errors with full provenance, discards invalid events

**UI**

- Subscribes to projection events
- Presents error feedback alongside normal friction summaries

### Error-handling Strategy

```mermaid
flowchart TD
    %% User configures source
    A["User configureSource"] --> B["SourceService validate locator & config"]
    B -->|valid| C["Emit SourceConfigured(sourceId)"]
    B -->|invalid| D["Emit SourceConfigurationFailed(reason)"]
    D --> E["UI displays configuration error"]

    %% Ingestion triggered by SourceConfigured
    C --> F["IngestionService ingest(sourceId)"]
    
    %% Ingestion outcomes
    F -->|record valid| G["Emit RecordIngested(recordId, sourceId, content)"]
    F -->|duplicate record| H["Emit DuplicateDetected(sourceId, recordId)"]
    F -->|malformed/corrupt| I["Emit IngestionFailed(sourceId, reason)"]

    %% Handling duplicates
    H --> J["ProjectionService log & discard duplicate"]
    J --> K["Optional alert/escalation"]

    %% Handling malformed/corrupt ingestion
    I --> L["ProjectionService log & discard record"]
    L --> M["Optional retry or escalate"]
    
    %% Observation creation
    G --> N["FrictionService createObservation(record)"]
    N -->|valid observation| O["Emit ObservationCreated(observationId)"]
    N -->|invalid/duplicate observation| P["Emit ObservationRejected(recordId, reason)"]
    
    %% Observation rejection handling
    P --> Q["ProjectionService log rejection"]
    Q --> R["Optional alert/escalation"]

    %% Adding observation to Friction
    O --> S["FrictionService addObservation(obs) -> new Friction"]
    S -->|metrics recalculated successfully| T["Emit FrictionUpdated(frictionId, metrics)"]
    S -->|metrics recalculation failed| U["Emit FrictionUpdateFailed(frictionId, reason)"]
    
    %% Metrics failure handling
    U --> V["ProjectionService log & escalate"]
    
    %% Projection updates
    T --> W["ProjectionService update FrictionSummary"]
    T --> X["ProjectionService update ObservationDetail"]
    
    %% UI consumption
    W --> Y["UI display friction summary"]
    X --> Y

```