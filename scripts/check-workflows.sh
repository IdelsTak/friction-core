#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
WORKFLOW_DIR="$ROOT_DIR/.github/workflows"

require_cmd() {
  local cmd="$1"
  local install_hint="$2"
  if ! command -v "$cmd" >/dev/null 2>&1; then
    echo "ERROR: Missing required command: $cmd"
    echo "Install hint: $install_hint"
    exit 1
  fi
}

print_step() {
  echo
  echo "==> $1"
}

print_step "Validating local tooling"
require_cmd "actionlint" "https://github.com/rhysd/actionlint"
require_cmd "yamllint" "pipx install yamllint (or pip install yamllint)"
require_cmd "act" "https://github.com/nektos/act"

if [[ ! -d "$WORKFLOW_DIR" ]]; then
  echo "No workflow directory found at $WORKFLOW_DIR"
  echo "Nothing to validate yet"
  exit 0
fi

print_step "Running actionlint"
(
  cd "$ROOT_DIR"
  actionlint
)

print_step "Running yamllint on workflow files"
(
  cd "$ROOT_DIR"
  yamllint .github/workflows
)

print_step "Running act dry-run smoke checks for key workflows"
KEY_WORKFLOWS=("ci.yml" "release.yml" "publish.yml")
FOUND=0

for wf in "${KEY_WORKFLOWS[@]}"; do
  full_path="$WORKFLOW_DIR/$wf"
  if [[ -f "$full_path" ]]; then
    FOUND=1
    echo "Dry-run: $wf"
    (
      cd "$ROOT_DIR"
      act -n pull_request -W ".github/workflows/$wf"
    )
  fi
done

if [[ "$FOUND" -eq 0 ]]; then
  echo "No key workflows found (${KEY_WORKFLOWS[*]}); skipping act smoke checks"
fi

echo
echo "Workflow preflight checks passed"
