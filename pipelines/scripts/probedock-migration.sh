#!/usr/bin/env bash

# Migrate the database
sudo -E docker-compose -f $PROBEDOCK_DOCKER_COMPOSE_FILE -p $PROBEDOCK_ENV run --rm task db:migrate