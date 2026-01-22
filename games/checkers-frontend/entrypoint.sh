#!/bin/sh

envsubst < /usr/share/nginx/html/runtime-config.js.template \
         > /usr/share/nginx/html/runtime-config.js

nginx -g "daemon off;"