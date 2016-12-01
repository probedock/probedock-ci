#!/usr/bin/with-contenv bash
set -e

export PGPASSWORD="$PROBEDOCK_DATABASE_PASSWORD"
until psql -h $PROBEDOCK_DATABASE_HOST -U "$PROBEDOCK_DATABASE_USERNAME" -c '\l'; do
  >&2 echo "Postgres is unavailable - waiting"
  sleep 1
done

>&2 echo "Postgres is up"

REDIS_ADDRESS=$(echo "$PROBEDOCK_REDIS_URL"|cut -d : -f 1)
REDIS_PORT=$(echo "$PROBEDOCK_REDIS_URL"|cut -d : -f 2)
until (echo > "/dev/tcp/$REDIS_ADDRESS/$REDIS_PORT") >/dev/null 2>&1; do
  >&2 echo "Redis is unavailable - waiting"
  sleep 1
done

>&2 echo "Redis is up"

exec "$@"
