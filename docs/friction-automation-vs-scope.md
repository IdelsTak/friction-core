# Friction: Automation vs Scope

This map shows how Friction stacks against competitors in terms of **automation** (Y-axis) and **scope / coverage** (X-axis). It clearly positions Friction as **high automation + cross-domain coverage**, unlike other tools.

```mermaid
graph TD
    style Friction fill:#4CAF50,stroke:#000,stroke-width:2px,color:#fff
    style PainScout fill:#FFC107,stroke:#000,stroke-width:1px
    style WorkflowRepo fill:#03A9F4,stroke:#000,stroke-width:1px
    style Enterprise fill:#E91E63,stroke:#000,stroke-width:1px
    style OSS fill:#9C27B0,stroke:#000,stroke-width:1px

    subgraph "Automation vs Scope"
        A1["Low Automation
        Narrow Scope"] --> OSS
        A2["Low Automation
        Medium Scope"] --> WorkflowRepo
        A3["Medium Automation
        Medium Scope"] --> PainScout
        A4["High Automation
        High Scope"] --> Friction
        A5["Medium Automation
        Wide Scope"] --> Enterprise
    end

    OSS["OSS Sentiment & Scrapers
    Keyword/Sentiment, Passive"]
    WorkflowRepo["Dovetail / Thematic
    Manual tagging + NLP, Partial traceability"]
    PainScout["BigIdeasDB / GummySearch
    Observation-focused, Partial automation"]
    Enterprise["Sprinklr / Brandwatch
    Marketing-heavy, noisy, expensive"]
    Friction["Friction
    System of Record
    Extra-Product
    Immutable + Event-Driven"]

    classDef highlighted fill:#4CAF50,color:#fff,stroke:#000,stroke-width:2px
    class Friction highlighted
```

**How to read this map:**

1. **Y-Axis:** Automation
   - OSS & Workflow: low to medium
   - Pain Scouts: medium
   - Friction: high
2. **X-Axis:** Scope / Coverage
   - OSS: narrow (single source / sentiment)
   - Workflow Repos: medium (internal research only)
   - Enterprise: wide but noisy / marketing-heavy
   - Friction: high (multi-source, cross-domain, developer-focused)
3. **Takeaway:** Friction occupies the **top-right quadrant**, making it the only tool that is both **automated** and **cross-domain**, with **immutable provenance** and **developer-first APIs**.