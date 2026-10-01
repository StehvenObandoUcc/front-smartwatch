#!/usr/bin/env bash
# Descarga el contrato del backend fijado en contract.lock a .contract/openapi.yaml (ignorado por git).
set -euo pipefail
root="$(cd "$(dirname "$0")/.." && pwd)"
sha="$(tr -d '[:space:]' < "$root/contract.lock")"
mkdir -p "$root/.contract"
gh api -H "Accept: application/vnd.github.raw" \
  "repos/StehvenObandoUcc/back-smartwatch/contents/contracts/openapi.yaml?ref=$sha" > "$root/.contract/openapi.yaml"
