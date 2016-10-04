#!/usr/bin/env bash

docker-compose -f $PROBEDOCK_DOCKER_COMPOSE_FILE -p $PROBEDOCK_ENV run --rm bakDb
