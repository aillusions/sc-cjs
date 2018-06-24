# Dockerfile - CentOS 7
# https://github.com/openresty/docker-openresty

# https://www.modpagespeed.com/doc/release_notes
# https://www.modpagespeed.com/doc/build_ngx_pagespeed_from_source
# https://opm.openresty.org/
# https://github.com/bungle/awesome-resty
# https://hub.docker.com/r/devgeniem/openresty-pagespeed/~/dockerfile/

# http://nginx.org/en/docs/http/ngx_http_geoip_module.html
# https://dev.maxmind.com/geoip/legacy/geolite/
# https://www.scalescale.com/tips/nginx/how-to-install-nginx-geoip-module/

# https://github.com/leev/ngx_http_geoip2_module
# http://www.treselle.com/blog/nginx-with-geoip2-maxmind-database-to-fetch-user-geo-location-data/
# mmdblookup --file /usr/local/openresty/nginx/GeoLite2-Country.mmdb --ip 8.8.8.8

FROM centos:7

# Docker Build Arguments
ARG RESTY_VERSION="1.13.6.1"
ARG RESTY_LUAROCKS_VERSION="2.4.3"
ARG RESTY_OPENSSL_VERSION="1.0.2k"
ARG RESTY_PCRE_VERSION="8.41"
ARG RESTY_J="1"
ARG NPS_VERSION=1.13.35.2
ARG PAGESPEED_NAME=${NPS_VERSION}-stable
ARG RESTY_CONFIG_OPTIONS="\
    --with-file-aio \
    --with-http_addition_module \
    --with-http_auth_request_module \
    --with-http_dav_module \
    --with-http_flv_module \
    --with-http_geoip_module \
    --with-http_gunzip_module \
    --with-http_gzip_static_module \
    --with-http_image_filter_module=dynamic \
    --with-http_mp4_module \
    --with-http_random_index_module \
    --with-http_realip_module \
    --with-http_secure_link_module \
    --with-http_slice_module \
    --with-http_ssl_module \
    --with-http_stub_status_module \
    --with-http_sub_module \
    --with-http_v2_module \
    --with-http_xslt_module=dynamic \
    --with-ipv6 \
    --with-mail \
    --with-mail_ssl_module \
    --with-md5-asm \
    --with-pcre-jit \
    --with-sha1-asm \
    --with-stream \
    --with-stream_ssl_module \
    --with-threads \
    --add-module=/tmp/pagespeed-${PAGESPEED_NAME}/incubator-pagespeed-ngx-${PAGESPEED_NAME} \
    --add-module=/tmp/ngx_http_geoip2_module \
    --add-module=/tmp/nginx-module-vts \
    --add-module=/tmp/nginx-module-sts \
    --add-module=/tmp/nginx-module-stream-sts \
    --user=nginx_user \
    --group=nginx_user"
ARG RESTY_CONFIG_OPTIONS_MORE=""

# These are not intended to be user-specified
ARG _RESTY_CONFIG_DEPS="--with-openssl=/tmp/openssl-${RESTY_OPENSSL_VERSION} --with-pcre=/tmp/pcre-${RESTY_PCRE_VERSION}"


# 1) Install yum dependencies
# 2) Download and untar OpenSSL, PCRE, and OpenResty
# 3) Build OpenResty
# 4) Cleanup

RUN yum install -y \
        gcc \
        gcc-c++ \
        gd-devel \
        gettext \
        GeoIP-devel \
        libxslt-devel \
        make \
        perl \
        perl-ExtUtils-Embed \
        readline-devel \
        unzip \
        which \
        wget \
        git \
        autoconf \
        automake \
        libtool \
        zlib-devel \
        gcc-c++ pcre-devel \
        libuuid-devel \
    && cd /tmp \
    && curl -fSL https://www.openssl.org/source/openssl-${RESTY_OPENSSL_VERSION}.tar.gz -o openssl-${RESTY_OPENSSL_VERSION}.tar.gz \
    && tar xzf openssl-${RESTY_OPENSSL_VERSION}.tar.gz \
    && curl -fSL https://ftp.pcre.org/pub/pcre/pcre-${RESTY_PCRE_VERSION}.tar.gz -o pcre-${RESTY_PCRE_VERSION}.tar.gz \
    && tar xzf pcre-${RESTY_PCRE_VERSION}.tar.gz \
    && curl -fSL https://openresty.org/download/openresty-${RESTY_VERSION}.tar.gz -o openresty-${RESTY_VERSION}.tar.gz \
    && tar xzf openresty-${RESTY_VERSION}.tar.gz \
    && wget https://github.com/apache/incubator-pagespeed-ngx/archive/v${PAGESPEED_NAME}.zip -O pagespeed-${PAGESPEED_NAME}.zip \
    && unzip pagespeed-${PAGESPEED_NAME}.zip -d /tmp/pagespeed-${PAGESPEED_NAME} \
    && cd /tmp/pagespeed-${PAGESPEED_NAME}/incubator-pagespeed-ngx-${PAGESPEED_NAME} \
    && wget https://dl.google.com/dl/page-speed/psol/${NPS_VERSION}-x64.tar.gz \
    && tar -xzvf ${NPS_VERSION}-x64.tar.gz \
    && cd /tmp \
    && git clone --recursive https://github.com/maxmind/libmaxminddb \
    && cd /tmp/libmaxminddb \
    && ./bootstrap \
    && ./configure \
    && make \
    && make check \
    && make install \
    && ldconfig \
    && cd /tmp \
    && git clone --recursive https://github.com/leev/ngx_http_geoip2_module.git \
    && cd /tmp \
    && git clone git://github.com/vozlt/nginx-module-sts.git \
    && git clone git://github.com/vozlt/nginx-module-stream-sts.git \
    && git clone git://github.com/vozlt/nginx-module-vts.git \
    && cd /tmp/openresty-${RESTY_VERSION} \
    && ./configure -j${RESTY_J} ${_RESTY_CONFIG_DEPS} ${RESTY_CONFIG_OPTIONS} ${RESTY_CONFIG_OPTIONS_MORE} \
    && make -j${RESTY_J} \
    && make -j${RESTY_J} install \
    && cd /tmp \
    && rm -rf \
        openssl-${RESTY_OPENSSL_VERSION} \
        openssl-${RESTY_OPENSSL_VERSION}.tar.gz \
        openresty-${RESTY_VERSION}.tar.gz openresty-${RESTY_VERSION} \
        pcre-${RESTY_PCRE_VERSION}.tar.gz pcre-${RESTY_PCRE_VERSION} \
    && curl -fSL https://github.com/luarocks/luarocks/archive/${RESTY_LUAROCKS_VERSION}.tar.gz -o luarocks-${RESTY_LUAROCKS_VERSION}.tar.gz \
    && tar xzf luarocks-${RESTY_LUAROCKS_VERSION}.tar.gz \
    && cd luarocks-${RESTY_LUAROCKS_VERSION} \
    && ./configure \
        --prefix=/usr/local/openresty/luajit \
        --with-lua=/usr/local/openresty/luajit \
        --lua-suffix=jit-2.1.0-beta3 \
        --with-lua-include=/usr/local/openresty/luajit/include/luajit-2.1 \
    && make build \
    && make install \
    && cd /tmp \
    && rm -rf luarocks-${RESTY_LUAROCKS_VERSION} luarocks-${RESTY_LUAROCKS_VERSION}.tar.gz \
    && yum clean all \
    && ln -sf /dev/stdout /usr/local/openresty/nginx/logs/access.log \
    && ln -sf /dev/stderr /usr/local/openresty/nginx/logs/error.log

RUN cd /tmp \
    && wget http://geolite.maxmind.com/download/geoip/database/GeoLiteCountry/GeoIP.dat.gz \
    && gzip -d GeoIP.dat.gz \
    && mv GeoIP.dat /usr/local/openresty/nginx/GeoIP.dat \
    && wget http://geolite.maxmind.com/download/geoip/database/GeoLiteCity.dat.gz \
    && gzip -d GeoLiteCity.dat.gz \
    && mv GeoLiteCity.dat /usr/local/openresty/nginx/GeoLiteCity.dat

RUN cd /tmp \
    && wget http://geolite.maxmind.com/download/geoip/database/GeoLite2-Country.mmdb.gz \
    && gzip -d GeoLite2-Country.mmdb.gz \
    && mv GeoLite2-Country.mmdb /usr/local/openresty/nginx/GeoLite2-Country.mmdb \
    && wget http://geolite.maxmind.com/download/geoip/database/GeoLite2-City.mmdb.gz \
    && gzip -d GeoLite2-City.mmdb.gz \
    && mv GeoLite2-City.mmdb /usr/local/openresty/nginx/GeoLite2-City.mmdb \
    && sh -c "echo /usr/local/lib  >> /etc/ld.so.conf.d/local.conf" \
    && ldconfig

RUN mkdir /usr/local/openresty/nginx/ngx_pagespeed_log \
    && mkdir /usr/local/openresty/nginx/ngx_pagespeed_cache \
    && adduser nginx_user \
    && chown -R nginx_user /usr/local/openresty/nginx

# Add additional binaries into PATH for convenience
ENV PATH=$PATH:/usr/local/openresty/luajit/bin:/usr/local/openresty/nginx/sbin:/usr/local/openresty/bin

# Copy nginx configuration files
#COPY nginx.conf /usr/local/openresty/nginx/conf/nginx.conf
#COPY nginx.vh.default.conf /etc/nginx/conf.d/default.conf
COPY ./serfver_fs/ /

CMD ["/usr/local/openresty/bin/openresty", "-g", "daemon off;"]