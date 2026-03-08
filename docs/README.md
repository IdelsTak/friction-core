# friction-core Docs (Canonical)

This directory is the single source of truth for shared product, domain, and architecture documentation across Friction repos.

## Canonical Shared Docs

- [Technical and Conceptual Overview](./friction-technical-and-conceptual-overview.md)
- [SPI Contract](./SPI.md)
- [Read Model Contract](./READ_MODELS.md)
- [Observation Clustering Strategy](./OBSERVATION_CLUSTERING.md)
- [Metrics Semantics and Edge-Case Policy](./METRICS_SEMANTICS.md)
- [Descriptor Generation Policy](./DESCRIPTOR_GENERATION.md)
- [Merge/Split Execution Policy](./MERGE_SPLIT_POLICY.md)
- [Security and Access Control Policy](./SECURITY_ACCESS_CONTROL.md)
- [Repo Boundaries](./REPO_BOUNDARIES.md)
- [Versioning Policy](./VERSIONING.md)
- [Decision Records Index](./adr/README.md)

## Domain and Architecture Design

- [First Domain Model (DDD + EO)](./friction-first-domain-model-ddd-eo.md)
- [Event-Driven Architecture](./friction-event-driven-architecture.md)
- [Product-Level UML](./friction-product-level-uml.md)
- [Sequence Diagram](./friction-sequence-diagram.md)
- [Metrics Incremental Computation](./friction-metrics-incremental-computation.md)

## Product and Positioning

- [Generalized Product Overview](./friction-generalized-product-overview.md)
- [Market and Competitor Analysis](./friction-market-and-competitor-analysis.md)
- [OSS Competitor Analysis](./friction-market-oss-competitor-analysis.md)
- [Executive Positioning Brief](./friction-brief-market-competitive-analysis-executive-positioning.md)
- [Automation vs Scope Map](./friction-automation-vs-scope.md)

## Governance

- [Contributing Guide](./CONTRIBUTING.md)
- [ADR Index](./adr/README.md)

## Repo Usage Rule

- `friction-core/docs` owns shared docs.
- `friction-ui/docs` and `friction-adapters/docs` should use pointer docs for shared topics and keep only repo-specific details.
