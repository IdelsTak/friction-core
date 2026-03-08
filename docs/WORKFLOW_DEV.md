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

## Release and Publish Workflows

`friction-core` release automation workflow:

- `.github/workflows/release.yml`
  - Trigger: push to `master`
  - Behavior: resolve merged PR labels, compute semver tag, generate changelog,
    create GitHub Release, then publish Maven package to GitHub Packages
  - Jobs and permissions:
    - `release`: `contents: write`, `pull-requests: read`
    - `publish`: `contents: read`, `packages: write`

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

The same label constraints are enforced again during `release.yml` execution.

## Artifact Retention

- CI debug artifacts use explicit short retention:
  - `ci.yml` upload step sets `retention-days: 1`
- No long-lived workflow artifacts are retained for non-release runs.
- Long-lived distributables are published as GitHub Releases and GitHub Packages.

## Notes

- The script is non-destructive and fast-fails on first error.
- If `.github/workflows` does not exist yet, the script exits successfully with a skip message.
- `act` dry-run validates workflow wiring without full execution.
- Before merge, still run a real GitHub-hosted workflow execution for runner parity.

## Troubleshooting

Symptom:

- GitHub Release is created but a separate publish workflow shows no runs.

Cause:

- `GITHUB_TOKEN`-created events do not reliably trigger downstream workflows.

Resolution:

- Use unified release + publish jobs in `release.yml` (current model), or use a
  tag-push trigger strategy if workflows must remain split.
