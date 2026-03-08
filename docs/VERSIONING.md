# Package Versioning Policy

`friction-core` is currently configured for package publishing only (no release workflow).

## Publish Trigger

- `.github/workflows/publish.yml` runs on push to `master`.
- `workflow_dispatch` is enabled for manual reruns.

## Version Strategy

- The workflow reads the base project version from `pom.xml`.
- Published package version is derived as:
  - `<baseVersion>-build.<GITHUB_RUN_NUMBER>`
- This guarantees unique versions per publish run and avoids redeploy conflicts.

## Authentication

- Publishing uses repository secret `PACKAGES_TOKEN` (PAT).
- Required scopes:
  - `write:packages`
  - `read:packages`
  - `repo` for private repositories

## Registry Target

- Packages are deployed to GitHub Maven registry:
  - `https://maven.pkg.github.com/<owner>/<repo>`
- Workflow normalizes owner/repo names to lowercase before deploy URL assembly.

## Notes

- PR version labels remain enforced by CI policy (`version:*`) as merge discipline.
- Release tags and GitHub Release notes are not part of this publishing mode.
