# friction-core Publish Runbook (Do This Once, Reuse Forever)

This runbook captures the exact CI/CD setup for publishing `friction-core` to GitHub Packages, including the common failure modes we hit and how to fix them quickly.

## Current Workflow Model

`friction-core` uses 3 workflows:

1. `ci.yml`
- Trigger: `pull_request`
- Purpose: build/test + version-label validation

2. `release.yml`
- Trigger: push to `master`
- Purpose: compute semver bump from PR labels, update `pom.xml`, commit bump, tag version, generate changelog notes

3. `publish.yml`
- Trigger: `workflow_run` on successful `Release`
- Purpose: checkout latest semver tag and publish package to GitHub Packages

## Source of Truth Files

- Workflow files:
  - `.github/workflows/ci.yml`
  - `.github/workflows/release.yml`
  - `.github/workflows/publish.yml`
- Maven publish target:
  - `pom.xml` (`distributionManagement`)
- Workflow docs:
  - `docs/WORKFLOW_DEV.md`
  - `docs/VERSIONING.md`

## One-Time GitHub Setup Checklist

Complete these once per repo/org setup.

1. Repository Actions permissions
- Repo: `friction-core` -> `Settings` -> `Actions` -> `General`
- Set `Workflow permissions` to `Read and write permissions`

2. Verify package/repo access policy (if org restrictions apply)
- Confirm this repo is allowed to publish packages
- Confirm package visibility/settings do not block writes from this repo

## Maven Requirements (Must Stay True)

`pom.xml` must include `distributionManagement` with repository id `github`.

Expected section:

```xml
<distributionManagement>
  <repository>
    <id>github</id>
    <name>GitHub Packages</name>
    <url>https://maven.pkg.github.com/IdelsTak/friction-core</url>
  </repository>
</distributionManagement>
```

Why this matters:
- `publish.yml` runs `mvn deploy` (no alternate deployment override).
- `actions/setup-java` writes Maven credentials for server id `github`.
- Deploy succeeds only if `distributionManagement.repository.id` matches setup-java `server-id`.

## Workflow Auth Contract

In `publish.yml`, the setup-java step must have:
- `server-id: github`
- `server-username: GITHUB_ACTOR`
- `server-password: GITHUB_TOKEN`
- `env.GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}`

If this env wiring is missing, Maven will deploy with invalid/missing auth and return `401`.

## Expected Happy Path

1. Merge PR to `master` with exactly one impact label:
- `version:major` or `version:minor` or `version:patch`

2. `release.yml` runs:
- resolves PR labels
- computes next version
- updates `pom.xml`
- commits bump
- creates `vX.Y.Z` tag
- creates changelog notes

3. `publish.yml` runs (`workflow_run` on Release success):
- checks out `master`
- resolves latest semver tag
- checks out that tag
- verifies `pom.xml` version equals tag version
- runs `mvn deploy`

4. Package appears in GitHub Packages.

## Fast Verification Commands

From repo root:

```bash
actionlint
yamllint .github/workflows
```

Quick local Maven check (no publish):

```bash
mvn -B -ntp -DskipTests verify
```

## Troubleshooting Matrix

### Symptom A
`publish.yml` does not run

Checks:
1. Did `release.yml` run and succeed?
2. Is `publish.yml` trigger exactly:
- `on.workflow_run.workflows: ["Release"]`
- `types: [completed]`
3. Is publish job gated correctly?
- `if: github.event.workflow_run.conclusion == 'success'`

### Symptom B
`401 Unauthorized` on `mvn deploy`

Checks in order:
1. Workflow/job permissions include `packages: write`
2. Repository Actions permissions are set to read/write
3. `setup-java` receives `GITHUB_TOKEN` env and `server-password: GITHUB_TOKEN`
4. `pom.xml` has correct `distributionManagement` with id `github`
5. `distributionManagement` URL points to exact repo path

### Symptom C
`pom.xml version (...) does not match tag (...)`

Cause:
- Release bump/tag and publish trigger got out of sync

Fix:
1. Verify latest tag points to commit that contains bumped `pom.xml`
2. Re-run `Release` then `Publish Package`
3. If needed, correct tag/version alignment in `master`

## Guardrails (Do Not Change Casually)

- Do not remove `distributionManagement` from `pom.xml`.
- Do not change setup-java `server-id` unless `pom.xml` repository id changes too.
- Do not move publish trigger back to `on: release` without redesign; use `workflow_run` to keep deterministic chaining.
- PATs are for external consumers pulling private packages, not for
  `friction-core` publishing itself.

## Change Checklist (When Editing Workflows)

After every workflow edit:

1. Run:
```bash
actionlint
yamllint .github/workflows
```

2. Confirm docs match behavior:
- `docs/WORKFLOW_DEV.md`
- `docs/VERSIONING.md`

3. Keep this runbook updated with any new failure modes.
