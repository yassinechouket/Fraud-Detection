#!/bin/bash
# deploy.sh

ECS_CLUSTER_NAME=$1
ECS_SERVICE_NAME=$2

if [ -z "$ECS_CLUSTER_NAME" ] || [ -z "$ECS_SERVICE_NAME" ]; then
  echo "Usage: ./deploy.sh <ECS_CLUSTER_NAME> <ECS_SERVICE_NAME>"
  exit 1
fi

echo "Deploying to ECS Cluster: $ECS_CLUSTER_NAME, Service: $ECS_SERVICE_NAME"

# Example: Update ECS service with new image
aws ecs update-service \
    --cluster "$ECS_CLUSTER_NAME" \
    --service "$ECS_SERVICE_NAME" \
    --force-new-deployment \
    --region "$AWS_DEFAULT_REGION"
