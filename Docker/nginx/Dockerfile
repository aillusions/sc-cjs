#
# Note on prod: nginx 1.11.0 (with lua and pagespeed 1.11.3.2)
#

FROM ci.xplace.com/common/openresty:1.13.6.1-1

ENV sccInstanceHostPort=scc-tomcat-dev:8090

VOLUME /usr/local/openresty/nginx/webapp
VOLUME /usr/local/openresty/nginx/work_dir

COPY ./conf/ /usr/local/openresty/nginx/conf
COPY ./run.sh /usr/local/openresty/bin/run.sh

RUN chmod +x /usr/local/openresty/bin/run.sh

RUN mv /etc/localtime /etc/localtime.bak && ln -s /usr/share/zoneinfo/Israel /etc/localtime

RUN date

EXPOSE 80
EXPOSE 443

CMD ["/usr/local/openresty/bin/run.sh"]