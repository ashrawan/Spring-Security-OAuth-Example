#!/bin/sh
set -e
if [ ! -f ../../.env ]; then
  echo "Please create a .env file based on .env.example before running." >&2
  exit 1
fi
kubectl delete secret spring-oauth-example-secret 2>/dev/null || true
kubectl create secret generic spring-oauth-example-secret --from-env-file=../../.env
