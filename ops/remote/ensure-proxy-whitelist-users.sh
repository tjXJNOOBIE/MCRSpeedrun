#!/usr/bin/env bash
set -euo pipefail

CONFIG_FILE="${CONFIG_FILE:-/srv/proxy/plugins/portfolio-mc-whitelist/config.properties}"

if [[ $# -eq 0 ]]; then
  echo "Usage: $0 <minecraft-username> [more-usernames...]" >&2
  exit 1
fi

if [[ ! -f "$CONFIG_FILE" ]]; then
  echo "Missing whitelist config: $CONFIG_FILE" >&2
  exit 1
fi

property() {
  local key="$1"
  grep -E "^${key}=" "$CONFIG_FILE" | head -n 1 | cut -d'=' -f2-
}

jdbc_url="$(property 'postgres.url')"
db_user="$(property 'postgres.user')"
db_password="$(property 'postgres.password')"
server_key="$(property 'whitelist.serverKey')"

if [[ -z "$jdbc_url" || -z "$db_user" || -z "$db_password" || -z "$server_key" ]]; then
  echo "Whitelist config is missing required properties." >&2
  exit 1
fi

jdbc_target="${jdbc_url#jdbc:postgresql://}"
host_port="${jdbc_target%%/*}"
database_name="${jdbc_target#*/}"
host="${host_port%%:*}"
port="${host_port#*:}"
if [[ "$port" == "$host_port" ]]; then
  port="5432"
fi

for username in "$@"; do
  normalized="$(printf '%s' "$username" | tr '[:upper:]' '[:lower:]')"
  entry_id="$(python3 - <<'PY'
import uuid
print(uuid.uuid4())
PY
)"
  PGPASSWORD="$db_password" psql \
    --host="$host" \
    --port="$port" \
    --username="$db_user" \
    --dbname="$database_name" \
    --quiet \
    --command="
      INSERT INTO minecraft_demo_whitelist (
        id,
        minecraft_username,
        minecraft_username_normalized,
        minecraft_uuid,
        demo_name,
        server_address,
        server_address_normalized,
        active,
        created_at,
        updated_at
      ) VALUES (
        '$entry_id',
        '$username',
        '$normalized',
        NULL,
        'Codex Store Regression',
        '$server_key',
        '$server_key',
        TRUE,
        now(),
        now()
      )
      ON CONFLICT (minecraft_username_normalized, server_address_normalized)
      DO UPDATE SET
        minecraft_username = EXCLUDED.minecraft_username,
        active = TRUE,
        updated_at = EXCLUDED.updated_at;
    " >/dev/null
done
