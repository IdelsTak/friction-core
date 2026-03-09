# Friction CI/CD Approach Report

## Purpose

Define an agreed CI/CD model for `friction-core`, `friction-adapters`, and `friction-ui` that enforces quality gates before merge, label-driven semantic versioning, and automated publishing.

## Repos in Scope

- `friction-core`
- `friction-adapters`
- `friction-ui`

## Operational Runbooks

- `docs/PUBLISH_RUNBOOK.md` - authoritative publish template and invariants for
  `friction-core`.
- Guardrail: keep `.github/workflows/publish.yml` aligned to this runbook;
  avoid variant implementations.

## Agreed Objectives

1. PRs must pass automated checks before merge.
2. Versioning must follow label-driven semantic policy (`version:*`).
3. `friction-core` and `friction-adapters` publish as GitHub Packages.
4. `friction-ui` publishes cross-platform release artifacts via `jpackage`.

## CI Policy (Pull Requests)

Trigger: `pull_request`

Required checks:

- Build success
- Test suite success
- Lint/format/static checks (as configured per repo)
- Version label validation check

Version label validation rules:

- Exactly one required: `version:major` or `version:minor` or `version:patch`
- Optional pre-release stage: `version:alpha` or `version:beta` or `version:rc`
- PR fails if impact labels are missing or conflicting

## Release and Versioning Policy

Trigger: merge to default branch after required checks pass

Release flow:

1. Read labels from merged PR.
2. Compute next version from latest `vX.Y.Z` tag:
   - `version:major` -> bump major
   - `version:minor` -> bump minor
   - `version:patch` -> bump patch
3. Apply optional pre-release suffix when present (`alpha`, `beta`, `rc`).
4. Create git tag and GitHub Release.

Source of truth for released versions:

- Git tags and GitHub Releases

## Publishing Strategy

## `friction-core`

- Build/package with Maven
- Publish artifact to GitHub Packages (Maven registry)
- Publish implementation is defined only by `docs/PUBLISH_RUNBOOK.md`

## `friction-adapters`

- Build/package with Maven
- Publish artifact to GitHub Packages (Maven registry)
- Publish only on release workflow success

## `friction-ui`

- Build JavaFX app
- Package with `jpackage` on matrix runners:
  - `ubuntu-latest`
  - `windows-latest`
  - `macos-latest`
- Attach platform artifacts to GitHub Release

## Artifact Storage and Retention Policy

Objective: minimize GitHub artifact storage usage and avoid quota exhaustion.

Policy:

- Keep CI workflow artifacts short-lived (recommended: 1-3 days for PR runs).
- Keep non-release build artifacts ephemeral; do not retain beyond debugging window.
- Reserve longer retention only for release artifacts attached to GitHub Releases.
- Avoid uploading duplicate artifacts across jobs; publish once from final packaging job.
- Prefer selective upload (only required binaries/log bundles), not entire workspaces.

Implementation guidance:

- Set explicit `retention-days` on all `actions/upload-artifact` steps.
- Use lower retention on high-frequency workflows (PRs, push validation).
- Use scheduled cleanup checks/reporting to monitor artifact usage trends.

## GitHub Actions Workflow Set

Each repo should have:

- `ci.yml`: PR validation pipeline
- `version-label-check.yml` (or equivalent job within `ci.yml`)
- `release.yml`: version calculation, tagging, GitHub Release creation

Publish workflows:

- `friction-core` / `friction-adapters`: `publish.yml` for Maven package deployment
- `friction-ui`: `publish-ui.yml` (or combined release workflow) for `jpackage` matrix artifacts

## Automated Changelog Generation

Objective: generate release changelogs automatically from commit messages and merged PR metadata.

Baseline approach:

- Generate changelog during release workflow from commits since previous tag.
- Group entries by semantic label and/or Conventional Commit type where available.
- Include links to PRs/issues and commit SHAs for traceability.

Recommended inputs:

- Commit messages (prefer Conventional Commit style for cleaner grouping).
- PR titles and labels (`version:*`, `architecture`, `mvu`, etc.).
- Merge metadata between `last_tag..new_tag`.

Output targets:

- GitHub Release notes body (primary).
- Optional `CHANGELOG.md` update in-repo for persistent history.

Operational guardrails:

- If commit messages are inconsistent, fall back to PR-title-first changelog entries.
- Keep changelog generation deterministic and tied to tag boundaries.
- Do not require manual edits for normal releases; allow override for exceptional releases.

## Security and Governance Controls

- Branch protection on default branch:
  - require pull requests
  - require status checks (`ci`, `version-label-check`)
  - require up-to-date branch before merge
- Minimal workflow permissions:
  - `contents: write` for tagging/releases
  - `packages: write` only for package-publish jobs
- Optional protected environments for release/publish approvals

## Operational Notes

- Keep workflow logic deterministic and label-driven.
- Keep release notes tied to PR and issue references.
- Prefer reusable composite actions or shared scripts only after first stable pass.

## Implementation Order

1. Add `ci.yml` + version-label validation in all repos.
2. Enable branch protection requiring those checks.
3. Add `release.yml` with semantic bump from PR labels.
4. Add publish workflows:
   - Maven packages for `friction-core` and `friction-adapters`
   - `jpackage` release artifacts for `friction-ui`
5. Dry-run on test tags/branches, then enable for default branch.

## Success Criteria

- No PR merges without passing CI and valid version labels.
- Every merged release PR produces deterministic semantic version tags/releases.
- `friction-core` and `friction-adapters` artifacts are available in GitHub Packages.
- `friction-ui` has downloadable cross-platform release artifacts from GitHub Releases.
