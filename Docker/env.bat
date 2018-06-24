
set SCC_APP_WEB_HOST_PORT=docker.for.win.localhost:8090

docker-compose down
docker-compose up mysql-scc-dev scc-nginx

