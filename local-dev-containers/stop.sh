#!/usr/bin/env bash

docker stop $(docker ps -a -q)
docker rm $(docker ps -a -q)

docker volume prune
docker network prune
