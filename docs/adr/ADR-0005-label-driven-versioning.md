# ADR-0005 Label-Driven Versioning Policy

- Status: Accepted
- Date: 2026-02-17

## Context

Friction uses issue/PR workflows across multiple repos and needs a consistent, explicit release impact mechanism.

## Decision

Use label-driven semantic versioning:

- Impact labels: `version:major`, `version:minor`, `version:patch`
- Optional pre-release labels: `version:alpha`, `version:beta`, `version:rc`

## Consequences

- Release impact is explicit at PR/issue time.
- CI/release automation can derive version bumps from labels.
- Teams must avoid conflicting impact labels on a single release path.

## References

- [VERSIONING.md](../VERSIONING.md)
