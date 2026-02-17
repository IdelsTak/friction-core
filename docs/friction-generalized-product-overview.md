# **Friction – Generalized Product Overview**

Friction is a **system of record for recurring, unresolved challenges across any domain**, capturing what slows progress, frustrates participants, or produces inefficiencies. Its role is **observation, measurement, and traceability**, not solving problems for the user.

## **Core Function**

1. **Ingest**
   - Collect publicly available discourse from one or more sources (Reddit is initial implementation).
   - Accept configurable domains or topics (subreddits, forums, social media threads, reviews, transcripts).
   - Persist raw input in an append-only, immutable store.
2. **Detect Recurring Patterns**
   - Identify repeated mentions of issues, obstacles, or friction points.
   - Recognize clusters of related complaints.
   - Track frequency, engagement, and temporal trends.
   - Retain provenance (timestamp, source, author).
3. **Measure and Score**
   - Quantify:
     - prevalence
     - intensity or severity (e.g., length, engagement, sentiment)
     - persistence over time
     - engagement gap (unresolved vs. addressed)
   - Generate metrics that make friction **comparable** across topics or domains.
4. **Aggregate and Surface**
   - Summarize high-impact frictions.
   - Highlight emerging or persistent problem areas.
   - Enable filtering and grouping by:
     - topic
     - field
     - severity
     - time window
   - Provide drill-down to original source context for verification.
5. **Present Evidence**
   - UI, report, or API surface that shows:
     - measurable patterns
     - examples of the friction
     - traceable sources
   - Avoids interpretation or “advice”; surfaces data objectively.

## **Value Proposition**

- For the **creator/user**: quickly identify unsolved problems to focus effort, development, or research.
- For **teams or analysts**: provide evidence of recurring friction, reduce guesswork, highlight opportunities.
- For **portfolio purposes**: demonstrates disciplined, immutable, metric-driven design, cross-domain applicability, and a clear mapping from raw data to structured insight.

## **Design Principles (generalized)**

- **Domain-agnostic**: works in software, healthcare, manufacturing, education, finance, etc.
- **Immutable record**: once ingested, raw data is preserved.
- **Traceable provenance**: every metric can be traced to its source.
- **Metric-driven**: never rely on subjective interpretation in the core domain.
- **Incremental and observable**: can scale horizontally to new fields or data sources without redesign.

## **Typical Workflow (high-level)**

1. **Source configuration** → user defines domains/topics.
2. **Data ingestion** → raw posts/comments/etc. captured and timestamped.
3. **Friction extraction** → recurring issues identified and clustered.
4. **Metric calculation** → prevalence, persistence, severity quantified.
5. **Visualization / report** → friction surfaced with context and evidence.
6. **Decision or action** → user decides what to explore, improve, or build.

## **Key Constraints**

- **Observation only**: the system does not fix or suggest solutions.
- **Evidence first**: all metrics are traceable to raw input.
- **Cross-domain**: naming and modeling remain neutral, not tied to Reddit or software.
- **Scalable design**: new sources or topics can be added without breaking the core aggregates.