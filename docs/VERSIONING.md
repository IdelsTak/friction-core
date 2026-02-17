# Versioning Policy (Label-Driven)

Releases are guided by GitHub labels that indicate semantic impact.

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

- A release must be tagged with **exactly one** of `major`, `minor`, `patch`.
- Pre-release labels (`alpha`, `beta`, `rc`) can be combined with a main label.
- If multiple impact labels appear, **highest impact wins** (`major > minor > patch`).
- Pre-releases should advance by stage: `alpha → beta → rc → stable`.

## Examples

- `version:minor + version:beta` → `v0.4.0-beta.1`
- `version:patch` → `v0.4.1`
- `version:major + version:rc` → `v1.0.0-rc.1`

