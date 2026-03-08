# ADR-0003 EO Naming Constraints

- Status: Accepted
- Date: 2026-02-17

## Context

The project follows Elegant Objects (EO) style to reinforce behavior-first design and immutability.

## Decision

Avoid class/interface names ending in `-er` or `-or` for shared domain/application contracts and structures.

## Consequences

- Naming shifts toward role/meaning nouns (`SourceInlet`, `SourceConfig`, `IngestionFeed`).
- Team must enforce consistency during reviews and refactors.
- Legacy names that violate EO style should be treated as debt-removal candidates.

## References

- [SPI.md](../SPI.md)
- [friction-first-domain-model-ddd-eo.md](../friction-first-domain-model-ddd-eo.md)
