#!/usr/bin/env bash
set -euo pipefail

APP_ROOT="${APP_ROOT:-/srv/PROJECT-NOVUS/Website}"
JAR_PATH="${JAR_PATH:-$APP_ROOT/distribution/application.jar}"
LIB_DIR="${LIB_DIR:-$APP_ROOT/distribution/libs}"
MAIN_CLASS="${MAIN_CLASS:-com.tjxjnoobie.website.WebsiteApplication}"
ENV_FILE="${ENV_FILE:-$APP_ROOT/store-web.env}"
PID_FILE="${PID_FILE:-$APP_ROOT/store-web.pid}"
LOG_DIR="${LOG_DIR:-$APP_ROOT/logs}"
JAVA_BIN="${JAVA_BIN:-java}"
JAVA_OPTS="${JAVA_OPTS:--Xms512m -Xmx1024m}"

mkdir -p "$LOG_DIR"

if [[ -f "$ENV_FILE" ]]; then
  set -a
  # shellcheck disable=SC1090
  source "$ENV_FILE"
  set +a
fi

if [[ ! -f "$JAR_PATH" || ! -d "$LIB_DIR" ]]; then
  echo "Missing website distribution: $JAR_PATH and $LIB_DIR are required" >&2
  exit 1
fi

if [[ -f "$PID_FILE" ]]; then
  existing_pid="$(cat "$PID_FILE" 2>/dev/null || true)"
  if [[ -n "$existing_pid" ]] && kill -0 "$existing_pid" 2>/dev/null; then
    echo "Store web is already running with pid $existing_pid"
    exit 0
  fi
fi

nohup "$JAVA_BIN" $JAVA_OPTS -cp "$JAR_PATH:$LIB_DIR/*" "$MAIN_CLASS" \
  > "$LOG_DIR/store-web.out.log" 2> "$LOG_DIR/store-web.err.log" < /dev/null &
echo $! > "$PID_FILE"
echo "Started store web with pid $(cat "$PID_FILE")"
