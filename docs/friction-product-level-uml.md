# Friction - product-level UML

Models the Friction domain model into a **product-level UML** with **application services, read models, and flows**, keeping DDD + EO principles intact.

### What it does:

1. Keeps **aggregates immutable** (`Source`, `Friction`) with behavior-only interfaces (`SourceService`, `FrictionService`).
2. Adds **read models** (`FrictionSummary`, `ObservationDetail`) for **UI / API consumption**, without exposing aggregate internals.
3. Shows the **data flow** from ingestion → observations → friction → projections → presentation.
4. Keeps **traceability** intact: every `ObservationDetail` links back to `IngestionRecord` and `Provenance`.

```mermaid
erDiagram
    %% Domain Aggregates
    SOURCE {
        uuid sourceId PK
        string locator_type
        string locator_value
        string config_ingestionInterval
        string config_filterKeywords
        string status
    }

    INGESTION_RECORD {
        uuid ingestionRecordId PK
        uuid sourceId FK
        timestamp ingestedAt
        text rawContent
    }

    FRICTION {
        uuid frictionId PK
        string descriptor
        int metrics_prevalence
        float metrics_intensity
        float metrics_persistence
        float metrics_trend_slope
        float metrics_trend_confidence
        text metrics_trend_seasonality
        text metrics_trend_outlierInfo
    }

    OBSERVATION {
        uuid observationId PK
        uuid frictionId FK
        uuid provenance_sourceId FK
        string provenance_uri
        timestamp provenance_timestamp
        string provenance_authorHandle
        uuid recordLink FK
    }

    %% Read Models / DTOs
    FRICTION_SUMMARY {
        uuid frictionId PK
        string descriptor
        int metrics_prevalence
        float metrics_intensity
        float metrics_persistence
        float metrics_trend_slope
        float metrics_trend_confidence
        int topObservationCount
    }

    OBSERVATION_DETAIL {
        uuid observationId PK
        uuid frictionId FK
        string provenance_uri
        timestamp provenance_timestamp
        string provenance_authorHandle
        text excerpt
    }

    %% Application Services
    SOURCE_SERVICE {
        ingest()
        pause()
        resume()
        updateConfig()
    }

    FRICTION_SERVICE {
        addObservation()
        mergeWith()
        recalcMetrics()
        present()
    }

    %% Relationships
    SOURCE ||--o{ INGESTION_RECORD : "produces"
    INGESTION_RECORD ||--o{ OBSERVATION : "contains"
    SOURCE ||--o{ OBSERVATION : "referenced_by"
    FRICTION ||--o{ OBSERVATION : "aggregates"

    FRICTION ||--o{ FRICTION_SUMMARY : "projects"
    OBSERVATION ||--o{ OBSERVATION_DETAIL : "projects"

    SOURCE_SERVICE ||--|| SOURCE : "manages"
    FRICTION_SERVICE ||--|| FRICTION : "manages"


```

### from Gemini

```mermaid
classDiagram
    direction LR

    class IngestionService {
        +triggerIngestion(sourceId)
        +processRawPayload(payload)
    }

    class FrictionAnalysisService {
        +extractObservations(ingestionRecordId)
        +synthesizeFriction(observationId)
        +recalculateTrends(frictionId)
    }

    subgraph Ingestion_Bounded_Context
        class Source {
            +SourceId id
            +Config config
            +Status status
            +validateSource()
        }
        class IngestionRecord {
            +Timestamp ingestedAt
            +RawContent content
        }
    end

    subgraph Analysis_Bounded_Context
        class Friction {
            +FrictionId id
            +Descriptor descriptor
            +MetricsSet metrics
            +updateTrend(observation)
        }
        class Observation {
            +Provenance provenance
            +EvidenceLink link
        }
    end

    subgraph Read_Models
        class FrictionDashboardDTO {
            +frictionName
            +intensityScore
            +trendDirection
            +topSources
        }
    end

    IngestionService ..> Source : commands
    FrictionAnalysisService ..> Friction : orchestrates
    Observation "n" --* "1" Friction : supports
    IngestionRecord "1" --* "n" Observation : generates
    Friction ..> FrictionDashboardDTO : projects to
```

