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

- `publish.yml` runs on `release.published`.
- Publishes `friction-core` Maven artifact to GitHub Packages using `GITHUB_TOKEN`.

## Workflow Permissions

- `release.yml`: `contents: write`, `pull-requests: read`
- `publish.yml`: `contents: read`, `packages: write`

## Examples

- `version:minor + version:beta` → `v0.4.0-beta.1`
- `version:patch` → `v0.4.1`
- `version:major + version:rc` → `v1.0.0-rc.1`
