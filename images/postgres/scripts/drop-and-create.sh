#!/usr/bin/env bash


echo 'Drop the database'
dropdb -h db -U ${POSTGRES_USER} ${PROBEDOCK_DATABASE_NAME}

echo 'Create the database'
createdb -h db -U ${POSTGRES_USER} -O ${PROBEDOCK_DATABASE_USERNAME} ${PROBEDOCK_DATABASE_NAME}