#!/usr/bin/env bash

sudo -E docker-compose -f $PROBEDOCK_DOCKER_COMPOSE_FILE -p $PROBEDOCK_ENV run --rm --no-deps loadDump /scripts/load-dump.sh