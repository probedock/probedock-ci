#!/usr/bin/env bash

# Stop the Probe Dock app containers
docker-compose -f $PROBEDOCK_DOCKER_COMPOSE_FILE -p $PROBEDOCK_ENV stop app

# Cleanup
docker-compose -f $PROBEDOCK_DOCKER_COMPOSE_FILE -p $PROBEDOCK_ENV rm -f app
