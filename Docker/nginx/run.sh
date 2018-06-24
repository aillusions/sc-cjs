#!/bin/bash

cp /usr/local/openresty/nginx/conf/nginx.conf.template /usr/local/openresty/nginx/conf/nginx.conf
sed -i s/SCC_SERVER_HTTP_ENDPOINT/$sccInstanceHostPort/g /usr/local/openresty/nginx/conf/nginx.conf

/usr/local/openresty/bin/openresty -g "daemon off;"