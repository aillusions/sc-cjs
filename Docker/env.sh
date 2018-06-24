#!/bin/bash
# chmod +x ./env.sh
# ./env.sh

export SCC_APP_WEB_HOST_PORT=docker.for.mac.localhost:8090


cd ./nginx_common
docker build -t openresty-bundle:1.13.6.1-0 .
cd ..


cd ./nginx
docker build -t scc-nginx:1.13.6.1-0 .
cd ..


docker-compose down
docker-compose build
docker-compose up mysql-scc-dev scc-nginx

