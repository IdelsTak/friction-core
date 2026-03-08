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
   - `publish.yml`

## Publish Workflow

`friction-core` package publishing workflow:

- `.github/workflows/publish.yml`
  - Trigger: push to `master`
  - Behavior: build and publish Maven package to GitHub Packages
  - Authentication: `PACKAGES_TOKEN` repository secret (PAT)
  - Permissions: `contents: read`, `packages: write`

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

## Notes

- The script is non-destructive and fast-fails on first error.
- If `.github/workflows` does not exist yet, the script exits successfully with a skip message.
- `act` dry-run validates workflow wiring without full execution.
- Before merge, still run a real GitHub-hosted workflow execution for runner parity.

## Troubleshooting

Symptom:

- Publish job fails with `401 Unauthorized` to `maven.pkg.github.com`.

Cause:

- Missing/invalid `PACKAGES_TOKEN`, or package write access not granted.

Resolution:

- Ensure repo secret `PACKAGES_TOKEN` exists and has `write:packages`.
- For private repos, include `repo` scope as well.
- Confirm package/repository access settings allow this repo to publish.
