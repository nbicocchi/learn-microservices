#!/bin/bash

for dir in *; do
  [ ! -d "$dir" ] && continue
  cd "$dir"
  mvn compile jib:dockerBuild
  cd ..
done