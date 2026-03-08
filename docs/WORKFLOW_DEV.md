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

## Notes

- The script is non-destructive and fast-fails on first error.
- If `.github/workflows` does not exist yet, the script exits successfully with a skip message.
- `act` dry-run validates workflow wiring without full execution.
- Before merge, still run a real GitHub-hosted workflow execution for runner parity.
