#!/bin/bash

# docker stop $(docker ps -a -q)
docker compose down
docker compose build
docker compose up -d

exit 0
