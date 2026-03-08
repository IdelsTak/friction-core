# Security and Access Control Policy

This document defines baseline security and access-control policy for `friction-core` contracts and operational behavior.

## Scope

- Applies to handling of source configuration, credentials, sensitive data access, and logging.
- Defines minimum controls for adapters and services consuming core contracts.
- Complements canonical event, clustering, metrics, and merge/split policies.

## 1) `SourceConfig.auth` and Secret Material

### Principles

- Secrets are never stored in plaintext in source code, VCS, or static config files.
- `SourceConfig.auth` must reference secure secret material (token/provider reference), not raw credentials when avoidable.
- Secret values must be injected at runtime from approved secret stores or environment boundaries.

### Handling Rules

- In-memory secret lifetime should be minimized.
- Secret values must not be echoed, serialized to logs, or included in exception messages.
- Secret-bearing objects should be treated as sensitive and excluded from debug dumps.

## 2) Access-Control Boundaries

### Configuration Access

- Source configuration write access is restricted to authorized operator/service principals.
- Read access to sensitive source configuration fields must be least-privilege.
- Non-sensitive configuration views should be provided separately from sensitive material.

### Data Access

- Provenance/evidence data access is role-scoped according to operational need.
- Raw ingestion content access should be controllable independently from summary/read-model access.
- Internal service-to-service access should use explicit service identity, not shared global credentials.

## 3) Redaction and Sanitization Policy

### Logs

- Never log raw tokens, API keys, secrets, or auth headers.
- Redact sensitive fields with deterministic placeholders (for example `***REDACTED***`).
- Keep logs single-sentence and contextual without exposing secret material.

### Failure Events

- Failure payloads must include actionable reason category and identifiers, not secret values.
- Error context should include operation identity/correlation IDs where available.
- Schema validation failures should report missing/invalid field names only, not full sensitive payloads.

## 4) Auditability Requirements

Audit records are required for sensitive operations:

- create/update/delete source configuration
- auth/credential binding changes
- key/credential rotation events
- access-control policy changes

Each audit record should include:

- actor identity (user/service principal)
- operation type
- target entity identity
- timestamp
- outcome (`SUCCESS`/`FAILURE`) and reason category

## 5) Credential Rotation Baseline

Minimum baseline policy:

- credentials/tokens must support periodic rotation.
- rotation procedure must be non-destructive and support rollback window.
- old credentials should be revoked after successful cutover.
- rotation failures must surface explicit operational alerts.

Operational expectations:

- support dual-credential overlap window when provider allows.
- test auth continuity before final revocation.

## 6) Fail-Fast Security Behavior

- Missing required auth material for protected source ingestion -> reject configuration/operation early.
- Invalid/expired credentials -> fail operation with non-sensitive context and explicit reason category.
- Unauthorized access attempts -> deny by default and emit audit record.

## 7) Implementation Baseline for Adapters/Services

- Use TLS for network transport where supported.
- Validate endpoint/domain allowlists for external source integrations when practical.
- Keep config immutable after startup unless explicit secure rebind workflow exists.
- Ensure retries do not amplify unauthorized/invalid-auth failures.

## 8) Change Control

- Security policy changes require ADR update and documented rollout impact.
- Any relaxation of redaction/access boundaries requires explicit risk acceptance.
