#!/usr/bin/env bash
set -euo pipefail

PROXY_DIR="${PROXY_DIR:-/srv/proxy}"
WEBSITE_ENV_FILE="${WEBSITE_ENV_FILE:-/srv/PROJECT-NOVUS/Website/store-web.env}"
STORE_BASE_URL="${STORE_BASE_URL:-http://127.0.0.1:8081}"
STORE_NODE_ID="${STORE_NODE_ID:-velocity-proxy}"
STORE_FALLBACK_RANK="${STORE_FALLBACK_RANK:-Member}"
PROXY_OUT_LOG="${PROXY_OUT_LOG:-$PROXY_DIR/logs/proxy-store.out.log}"
PROXY_ERR_LOG="${PROXY_ERR_LOG:-$PROXY_DIR/logs/proxy-store.err.log}"

if [[ ! -f "$WEBSITE_ENV_FILE" ]]; then
  echo "Missing website env file: $WEBSITE_ENV_FILE" >&2
  exit 1
fi

set -a
# shellcheck disable=SC1090
source "$WEBSITE_ENV_FILE"
set +a

if [[ -z "${APP_STORE_INTEGRATION_BOOTSTRAP_SECRET:-}" ]]; then
  echo "APP_STORE_INTEGRATION_BOOTSTRAP_SECRET is not present in $WEBSITE_ENV_FILE" >&2
  exit 1
fi

mkdir -p "$(dirname "$PROXY_OUT_LOG")"

pkill -f '[v]elocity.jar' || true
sleep 2

cd "$PROXY_DIR"
nohup env \
  NOVUS_STORE_BASE_URL="$STORE_BASE_URL" \
  NOVUS_STORE_BOOTSTRAP_SECRET="$APP_STORE_INTEGRATION_BOOTSTRAP_SECRET" \
  NOVUS_STORE_NODE_ID="$STORE_NODE_ID" \
  NOVUS_STORE_FALLBACK_RANK="$STORE_FALLBACK_RANK" \
  java -Xms1G -Xmx1G -XX:+UseG1GC -XX:G1HeapRegionSize=4M -XX:+UnlockExperimentalVMOptions \
    -XX:+ParallelRefProcEnabled -XX:+AlwaysPreTouch -jar velocity.jar \
    > "$PROXY_OUT_LOG" 2> "$PROXY_ERR_LOG" < /dev/null &
