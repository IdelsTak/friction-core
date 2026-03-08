# Docs Contributing Guide

## Scope Ownership

- `friction-core/docs` is canonical for shared product/domain/architecture docs.
- `friction-ui/docs` and `friction-adapters/docs` should contain repo-specific docs and pointers to canonical shared docs.

## What Belongs Here

- Shared architecture contracts (SPI, read models, boundaries, versioning)
- Cross-repo product/domain design
- Decision records (ADRs)

## What Does Not Belong Here

- UI-only implementation details (MVU state shape, JavaFX view wiring)
- Adapter-only operational details (source-specific auth/rate limits, runbooks)

## ADR Process

1. Create a new file in `docs/adr/` named `ADR-XXXX-short-title.md`.
2. Use the template structure: Status, Date, Context, Decision, Consequences, References.
3. Update `docs/adr/README.md` index with the new ADR.
4. If replacing an ADR, mark old ADR as superseded and link the new ADR.

## Editing Rules

- Keep shared docs objective and implementation-agnostic where possible.
- Prefer small, traceable updates tied to an issue/PR.
- Do not duplicate canonical content in non-canonical repos.
