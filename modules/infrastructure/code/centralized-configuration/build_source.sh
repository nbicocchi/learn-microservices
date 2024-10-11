#!/usr/bin/env bash

SERVICES="datetime-composite-service datetime-service eureka-service gateway-service"
SERVICES_REPLACE="datetime-composite-service datetime-service gateway-service"

for service in $SERVICES; do
  echo copying "$service"...
  rm -rf "$service"
  cp -r ../service-discovery-routing/"$service" .
done

for service in $SERVICES_REPLACE; do
  echo fix config "$service"...
  cp application.yml "$service"/src/main/resources/application.yml
  sed -i s/_SERVICE_NAME_/"$service"/g "$service"/src/main/resources/application.yml
done