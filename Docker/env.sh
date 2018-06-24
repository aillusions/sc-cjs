#!/bin/bash
# chmod +x ./env.sh
# ./env.sh

export SCC_APP_WEB_HOST_PORT=docker.for.mac.localhost:8090


docker-compose down
docker-compose build
docker-compose up mysql-scc-dev scc-nginx

