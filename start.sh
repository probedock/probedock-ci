#!/usr/bin/env bash

echo "Build the Docker images"

echo "Probe Dock Jenkins image"
docker build -t probedock/jenkins images/jenkins

echo "Probe Dock Nginx image"
docker build -t probedock/nginx images/nginx

echo "Probe Dock Postgres"
docker build -t probedock/postgres images/postgres

echo "Probe Dock base image"
git clone https://github.com/probedock/probedock images/probedock-base/probedock
docker build -f images/probedock-base/Dockerfile-base -t probedock/probedock-base images/probedock-base

echo "Starting Jenkins"
docker-compose -p ci up -d jenkins