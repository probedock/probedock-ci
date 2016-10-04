#!/usr/bin/env bash

# Make sure Redis is running
docker-compose -f $PROBEDOCK_DOCKER_COMPOSE_FILE -p $PROBEDOCK_ENV up --no-recreate -d cache
docker-compose -f $PROBEDOCK_DOCKER_COMPOSE_FILE -p $PROBEDOCK_ENV run --rm waitCache
