#!/usr/bin/env bash

set -euo pipefail

cd "$(dirname "$0")"

if [[ -f ".env" ]]; then
  set -a
  source ".env"
  set +a
fi

echo "Verification du port 8080..."

if command -v lsof >/dev/null 2>&1; then
  pids="$(lsof -ti tcp:8080 || true)"
  if [[ -n "$pids" ]]; then
    echo "Processus trouve sur le port 8080 : PID(s)=${pids}"
    kill -9 $pids || true
    echo "Processus termine."
  fi
else
  echo "lsof introuvable, verification du port ignoree."
fi

sleep 1

if [[ -x "./mvnw" ]]; then
  exec ./mvnw spring-boot:run
fi

if command -v mvn >/dev/null 2>&1; then
  exec mvn spring-boot:run
fi

echo "Maven wrapper not found and Maven is not available on PATH."
exit 1
