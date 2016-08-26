#!/usr/bin/env bash

# Start the Probe Dock app containers
sudo -E docker-compose -f $PROBEDOCK_DOCKER_COMPOSE_FILE -p $PROBEDOCK_ENV up --no-deps -d app

# Scale
sudo -E docker-compose -f $PROBEDOCK_DOCKER_COMPOSE_FILE -p $PROBEDOCK_ENV scale app=$PROBEDOCK_DOCKER_APP_CONTAINERS
