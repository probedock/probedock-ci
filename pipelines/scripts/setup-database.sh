#!/usr/bin/env bash

echo 'Create the database'
sudo -E docker-compose -f $PROBEDOCK_DOCKER_COMPOSE_FILE -p $PROBEDOCK_ENV run --rm --no-deps task db:schema:load db:seed