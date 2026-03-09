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

- `release.yml` generates notes from the current merged PR only.
- Notes include:
  - `## Change Summary` with current PR number/title
  - `## Details` from cleaned PR body content
- If PR body is empty, release notes include a clear fallback message.
- Notes are attached to the tag release metadata.

## Publishing

Publish implementation details are defined only in
`docs/PUBLISH_RUNBOOK.md`.

Rule:
- Keep `.github/workflows/publish.yml` aligned to the runbook template exactly.
