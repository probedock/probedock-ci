#!/usr/bin/env bash

echo 'Restore dump file'
psql -h db -U "${PROBEDOCK_DATABASE_USERNAME}" -f ${DUMP_PATH} ${PROBEDOCK_DATABASE_NAME}