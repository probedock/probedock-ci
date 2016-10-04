#!/usr/bin/env bash

# Start the Probe Dock background job containers
docker-compose -f $PROBEDOCK_DOCKER_COMPOSE_FILE -p $PROBEDOCK_ENV up --no-deps -d job

# Scale
docker-compose -f $PROBEDOCK_DOCKER_COMPOSE_FILE -p $PROBEDOCK_ENV scale job=$PROBEDOCK_DOCKER_JOB_CONTAINERS
