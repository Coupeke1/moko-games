FROM node:20-alpine AS build
WORKDIR /app

COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine

RUN apk add --no-cache gettext

WORKDIR /usr/share/nginx/html
RUN rm -rf ./*

COPY --from=build /app/dist .

COPY public/runtime-config.js.template /usr/share/nginx/html/runtime-config.js.template
COPY entrypoint.sh /entrypoint.sh

RUN chmod +x /entrypoint.sh

EXPOSE 80
CMD ["/entrypoint.sh"]