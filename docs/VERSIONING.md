# Versioning Policy (Label-Driven)

Releases are guided by PR labels and automated on merge to `master`.

## Labels

| Label | Meaning | Impact |
| --- | --- | --- |
| `version:major` | Breaking change | MAJOR bump |
| `version:minor` | Backward-compatible feature | MINOR bump |
| `version:patch` | Bugfix / refactor | PATCH bump |
| `version:alpha` | Alpha pre-release | Pre-release |
| `version:beta` | Beta pre-release | Pre-release |
| `version:rc` | Release candidate | Pre-release |

## Rules

- A merged PR must include **exactly one** impact label:
  - `version:major` or `version:minor` or `version:patch`
- A merged PR may include **at most one** pre-release label:
  - `version:alpha` or `version:beta` or `version:rc`
- Missing or conflicting impact labels fail validation.
- Merge to `master` computes the next tag from the latest stable `vX.Y.Z`.
- If a pre-release label is present, release tag suffix is appended as:
  - `-alpha.1`, `-beta.1`, or `-rc.1`

## Release Trigger

- `release.yml` runs on push to `master` (post-merge path).
- Workflow resolves the merged PR associated with the merge commit, reads labels, computes the next version, creates tag, and creates GitHub Release.

## Changelog Source

- Release notes are generated automatically in `release.yml`.
- Commit window:
  - from latest stable tag to current merge commit
  - or recent commit window when no prior stable tag exists
- Release notes include:
  - merged PR reference (`#<number> <title>`)
  - commit list (`<sha7> <summary>`)

## Publishing

- Publishing is executed in the `publish` job inside `release.yml`.
- `publish` runs only after successful semantic version resolution and release creation.
- Publishes `friction-core` Maven artifact to GitHub Packages using `GITHUB_TOKEN`.

## Workflow Permissions

- `release.yml` / `release` job: `contents: write`, `pull-requests: read`
- `release.yml` / `publish` job: `contents: read`, `packages: write`

## Trigger Rationale

- GitHub does not trigger downstream workflows from events created by
  `GITHUB_TOKEN` (anti-recursion guard).
- Because releases are created by `release.yml` using `GITHUB_TOKEN`, standalone
  `on: release` publish workflows may not run.
- Unified release + publish in `release.yml` ensures deterministic package
  publication on every valid release.

## Examples

- `version:minor + version:beta` → `v0.4.0-beta.1`
- `version:patch` → `v0.4.1`
- `version:major + version:rc` → `v1.0.0-rc.1`
