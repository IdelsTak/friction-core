# Workflow Development and Local Preflight

Use this guide to validate GitHub Actions workflows locally before pushing.

## Required Tools

- `actionlint`
- `yamllint`
- `act`

## Installation

### Arch Linux (`pacman`)

```bash
sudo pacman -S --needed actionlint yamllint act
```

### actionlint

- Project: `https://github.com/rhysd/actionlint`
- Example (Go): `go install github.com/rhysd/actionlint/cmd/actionlint@latest`

### yamllint

- Example (pipx): `pipx install yamllint`
- Alternative: `pip install yamllint`

### act

- Project: `https://github.com/nektos/act`
- Install using package manager or official release binary

## Run Local Preflight

From repo root:

```bash
scripts/check-workflows.sh
```

What it does:

1. Verifies required tools are installed.
2. Runs `actionlint`.
3. Runs `yamllint .github/workflows`.
4. Runs `act` dry-run smoke checks for key workflows if present:
   - `ci.yml`
   - `release.yml`
   - `publish.yml`

## Publish Reference

`docs/PUBLISH_RUNBOOK.md` is the only source of truth for publish behavior.
Use its template directly and avoid local variations.

## Workflow Partitioning

`friction-core` CI/CD uses three workflows:

- `.github/workflows/ci.yml`
  - Trigger: pull requests
  - Behavior: build/test validation + label policy checks
- `.github/workflows/release.yml`
  - Trigger: push to `master`
  - Behavior: resolve semantic bump from PR labels, update `pom.xml`, commit version bump, create tag, generate release notes in fixed format from current PR only (`# Changelog`, PR header line, PR body)
- `.github/workflows/publish.yml`
  - Trigger: `workflow_run` for `Release` completion
  - Behavior: defined only by `docs/PUBLISH_RUNBOOK.md` template

## Authentication and Permissions

- `release.yml` must use `GITHUB_TOKEN` for commit/tag/release-note operations.
- `publish.yml` auth and credential wiring must match
  `docs/PUBLISH_RUNBOOK.md` exactly.
- Do not introduce PAT-based auth into `friction-core` publish workflow.
- Do not infer auth setup from other docs; copy the runbook template as-is.

## PR Checks and Label Policy

`friction-core` PR validation is defined in `.github/workflows/ci.yml` and exposes these stable check names:

- `build-and-test`
- `version-label-check`

Branch protection should require both checks.

Version labels enforced by `version-label-check`:

- Exactly one impact label is required:
  - `version:major`
  - `version:minor`
  - `version:patch`
- At most one pre-release label is allowed:
  - `version:alpha`
  - `version:beta`
  - `version:rc`

## Artifact Retention

- CI debug artifacts use explicit short retention:
  - `ci.yml` upload step sets `retention-days: 1`
- No long-lived workflow artifacts are retained for non-release runs.
- Long-lived distributables are published to GitHub Packages.

## Publish Guardrail

If publish behavior needs changes, update `docs/PUBLISH_RUNBOOK.md` first, then
apply the exact template/content to `.github/workflows/publish.yml`.
