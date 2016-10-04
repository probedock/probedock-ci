#!/usr/bin/env bash

# Compile the assets and templates
docker-compose -f $PROBEDOCK_DOCKER_COMPOSE_FILE -p $PROBEDOCK_ENV run --rm --no-deps assets assets:precompile assets:clean
docker-compose -f $PROBEDOCK_DOCKER_COMPOSE_FILE -p $PROBEDOCK_ENV run --rm --no-deps assets templates:precompile static:copy
