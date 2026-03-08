# ADR-0010 Security and Access Control Policy

- Status: Accepted
- Date: 2026-03-08

## Context

Security and access-control guidance for credentials, data access, and log redaction was previously underdefined. This created risk for accidental secret exposure and inconsistent operational controls.

## Decision

Adopt a canonical security/access-control policy defined in `SECURITY_ACCESS_CONTROL.md`.

Key decisions:

- `SourceConfig.auth` handled as sensitive material with runtime secret injection.
- Least-privilege boundaries for configuration and data access.
- Mandatory redaction/sanitization for logs and failure contexts.
- Audit records required for sensitive configuration and credential operations.
- Baseline credential rotation requirements and fail-fast behavior for invalid/missing auth.

## Consequences

- Security handling is explicit and consistent across implementations.
- Operational traceability improves via mandatory audit context.
- Additional implementation overhead is required for redaction, audit, and rotation workflows.

## Constraints

- Secrets must never appear in logs/events/exceptions.
- Unauthorized access defaults to deny with auditable outcome.
- Security policy relaxations require explicit governance.

## References

- [SECURITY_ACCESS_CONTROL.md](../SECURITY_ACCESS_CONTROL.md)
- [SPI.md](../SPI.md)
- [friction-technical-and-conceptual-overview.md](../friction-technical-and-conceptual-overview.md)
