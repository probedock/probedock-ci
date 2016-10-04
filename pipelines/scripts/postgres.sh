#!/usr/bin/env bash

# Make sure PostgreSQL is running
docker-compose -f $PROBEDOCK_DOCKER_COMPOSE_FILE -p $PROBEDOCK_ENV up --no-recreate -d db
docker-compose -f $PROBEDOCK_DOCKER_COMPOSE_FILE -p $PROBEDOCK_ENV run --rm waitDb
