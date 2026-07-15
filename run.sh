#!/usr/bin/env bash

set -euo pipefail

cd "$(dirname "$0")"

if [[ -f ".env" ]]; then
  set -a
  source ".env"
  set +a
fi

APP_PORT="${SERVER_PORT:-}"
if [[ -z "${APP_PORT}" && -f "src/main/resources/application.properties" ]]; then
  APP_PORT="$(awk -F= '/^server\.port=/ { print $2; exit }' src/main/resources/application.properties || true)"
fi
APP_PORT="${APP_PORT:-8080}"

echo "Verification du port ${APP_PORT}..."

if command -v lsof >/dev/null 2>&1; then
  pids="$(lsof -ti tcp:${APP_PORT} || true)"
  if [[ -n "$pids" ]]; then
    echo "Processus trouve sur le port ${APP_PORT} : PID(s)=${pids}"
    kill -9 ${pids} || true
    echo "Processus termine."
  fi
elif command -v fuser >/dev/null 2>&1; then
  pids="$(fuser -n tcp ${APP_PORT} 2>/dev/null || true)"
  if [[ -n "$pids" ]]; then
    echo "Processus trouve sur le port ${APP_PORT} : PID(s)=${pids}"
    fuser -k -n tcp ${APP_PORT} >/dev/null 2>&1 || true
    echo "Processus termine."
  fi
else
  echo "lsof introuvable, verification du port ignoree."
fi

sleep 1

if [[ -x "./mvnw" ]]; then
  exec ./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=${APP_PORT}
fi

if command -v mvn >/dev/null 2>&1; then
  exec mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=${APP_PORT}
fi

echo "Maven wrapper not found and Maven is not available on PATH."
exit 1
