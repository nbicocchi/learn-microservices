#!/bin/bash

curl -i -X POST http://localhost:7001/graphql -H "Content-Type: application/json" -d @introspection_query.json
