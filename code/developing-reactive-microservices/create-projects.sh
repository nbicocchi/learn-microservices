#!/usr/bin/env bash

VERSION="3.2.1"

delete_unwanted() {
  # remove useless file for multi-project build
  find "$1" -depth -name "gradle" -exec rm -rfv "{}" \;
  find "$1" -depth -name "gradlew*" -exec rm -fv "{}" \;
  
  find "$1" -depth -name ".mvn" -exec rm -rfv "{}" \;
  find "$1" -depth -name "mvnw*" -exec rm -fv "{}" \;
  
  find "$1" -depth -name "*.gitignore" -exec rm -fv "{}" \;
  find "$1" -depth -name "*.md" -exec rm -fv "{}" \;
}

spring init \
--boot-version="$VERSION" \
--type=maven-project \
--java-version=17 \
--packaging=jar \
--name=api \
--package-name=com.nbicocchi.api \
--groupId=com.nbicocchi.api \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
api

spring init \
--boot-version="$VERSION" \
--type=maven-project \
--java-version=17 \
--packaging=jar \
--name=util \
--package-name=com.nbicocchi.util \
--groupId=com.nbicocchi.util \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
util

mkdir microservices
cd microservices

spring init \
--boot-version="$VERSION" \
--type=maven-project \
--java-version=17 \
--packaging=jar \
--name=product-service \
--package-name=com.nbicocchi.microservices.core.product \
--groupId=com.nbicocchi.microservices.core.product \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
product-service

spring init \
--boot-version="$VERSION" \
--type=maven-project \
--java-version=17 \
--packaging=jar \
--name=review-service \
--package-name=com.nbicocchi.microservices.core.review \
--groupId=com.nbicocchi.microservices.core.review \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
review-service

spring init \
--boot-version="$VERSION" \
--type=maven-project \
--java-version=17 \
--packaging=jar \
--name=recommendation-service \
--package-name=com.nbicocchi.microservices.core.recommendation \
--groupId=com.nbicocchi.microservices.core.recommendation \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
recommendation-service

spring init \
--boot-version="$VERSION" \
--type=maven-project \
--java-version=17 \
--packaging=jar \
--name=product-composite-service \
--package-name=com.nbicocchi.microservices.composite.product \
--groupId=com.nbicocchi.microservices.composite.product \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
product-composite-service

cd ..
cp microservices/product-service/.gitignore .
delete_unwanted microservices
delete_unwanted api
delete_unwanted util
exit 0
