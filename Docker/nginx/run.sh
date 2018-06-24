#!/bin/bash

cp /usr/local/openresty/nginx/conf/nginx.conf.template /usr/local/openresty/nginx/conf/nginx.conf
sed -i s/MARKET_TC_HTTP_ENDPOINT/$sccInstanceHostPort/g /usr/local/openresty/nginx/conf/nginx.conf

/usr/local/openresty/bin/openresty -g "daemon off;"