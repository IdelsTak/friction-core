# Versioning Policy (Release + Publish)

`friction-core` versioning is driven by PR labels and applied by `release.yml`.

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

- A merged PR must include exactly one impact label:
  - `version:major` or `version:minor` or `version:patch`
- A merged PR may include at most one pre-release label:
  - `version:alpha` or `version:beta` or `version:rc`
- Missing/conflicting labels fail CI.

## Release Version Computation

- `release.yml` reads latest stable tag `vX.Y.Z`.
- Computes next version from impact label:
  - `major` -> `X+1.0.0`
  - `minor` -> `X.Y+1.0`
  - `patch` -> `X.Y.Z+1`
- If pre-release label is present, appends `-alpha.1` / `-beta.1` / `-rc.1`.

## pom.xml Version Updates

- `release.yml` sets `<version>` in `pom.xml` to computed next version.
- Commits version bump to `master`.
- Creates matching git tag `v<version>`.

## Changelog Notes

- `release.yml` generates human-readable notes focused on merged PR entries.
- Notes are attached to the tag release metadata.
- Merge-noise commit lines are excluded from main entries.

## Publishing

- `publish.yml` runs on successful `workflow_run` completion of `Release`.
- It checks out latest semver tag and verifies:
  - `pom.xml` version == tag version
- Publishes Maven package to GitHub Packages using `GITHUB_TOKEN`.
- Deploy target is defined in `pom.xml` via `<distributionManagement>` with
  repository id `github`.
