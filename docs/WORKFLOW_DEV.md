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

## Workflow Partitioning

`friction-core` CI/CD uses three workflows:

- `.github/workflows/ci.yml`
  - Trigger: pull requests
  - Behavior: build/test validation + label policy checks
- `.github/workflows/release.yml`
  - Trigger: push to `master`
  - Behavior: resolve semantic bump from PR labels, update `pom.xml`, commit version bump, create tag, generate changelog notes
- `.github/workflows/publish.yml`
  - Trigger: `workflow_run` for `Release` completion
  - Behavior: checkout latest tag, verify `pom.xml` version matches tag, publish Maven package via `distributionManagement`

## Authentication and Permissions

- `release.yml` uses `GITHUB_TOKEN` for commit/tag/release-note operations.
- `publish.yml` uses `GITHUB_TOKEN` for Maven package deployment.
- `setup-java` writes `settings.xml` credentials for server id `github`:
  - username source: `GITHUB_ACTOR`
  - password source: `GITHUB_TOKEN`

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

## Troubleshooting

Symptom:

- Publish job fails with `401 Unauthorized` to `maven.pkg.github.com`.

Cause:

- Repo token lacks package write access, or package/repo permissions deny write.

Resolution:

- Ensure workflow/job permissions include `packages: write`.
- Ensure repository Actions permissions are set to read/write.
- Confirm package/repository access settings allow this repo to publish.
- Ensure `publish.yml` runs after successful `Release` workflow completion.
