#!/bin/sh
# Apply Kubernetes manifests.
# Requires kubectl configured with cluster access.
set -e

kubectl apply -f deployment.yaml
kubectl apply -f service.yaml

