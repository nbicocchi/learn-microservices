#!/usr/bin/env bash

SERVICES="datetime-composite-service datetime-service eureka-service gateway-service"

for service in $SERVICES; do
  rm -rf "$service"
  cp -r ../service-discovery-routing/"$service" .
  echo copying "$service"...
  cp application.yml "$service"/src/main/resources
done