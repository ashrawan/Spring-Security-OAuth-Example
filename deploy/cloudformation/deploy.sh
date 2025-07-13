#!/bin/sh
# Deploy or remove the CloudFormation stack that provides AWS resources.
# Behaviour is controlled via `config.json`. Requires AWS CLI credentials.
set -e

command -v jq >/dev/null 2>&1 || { echo >&2 "jq is required but not installed."; exit 1; }

deploy_stack() {
  echo "Deploying stack $STACK_NAME"
  PARAMS="DBPassword=$DB_PASSWORD HostedZoneName=$HOSTED_ZONE"
  PARAMS="$PARAMS EnableS3=$S3_ENABLED EnableRDS=$RDS_ENABLED EnableCache=$CACHE_ENABLED EnableDNS=$DNS_ENABLED"
  PARAMS="$PARAMS DBInstanceClass=$DB_CLASS CacheNodeType=$CACHE_NODE"
  aws cloudformation deploy \
    --stack-name "$STACK_NAME" \
    --template-file "$TEMPLATE" \
    --capabilities CAPABILITY_NAMED_IAM \
    --parameter-overrides $PARAMS
  if [ "$PROTECT" = "true" ]; then
    aws cloudformation update-termination-protection \
      --stack-name "$STACK_NAME" --enable-termination-protection
  fi
  aws cloudformation describe-stacks --stack-name "$STACK_NAME" --query 'Stacks[0].Outputs'
}

remove_stack() {
  if [ "$PROTECT" = "true" ]; then
    echo "Stack is protected. Set protect=false in $CONFIG_FILE to allow deletion." >&2
    return
  fi
  aws cloudformation update-termination-protection \
    --stack-name "$STACK_NAME" --no-enable-termination-protection || true
  aws cloudformation delete-stack --stack-name "$STACK_NAME"
  echo "Waiting for stack deletion..."
  aws cloudformation wait stack-delete-complete --stack-name "$STACK_NAME"
}

CONFIG_FILE="config.json"
STACK_NAME="spring-oauth-example"
TEMPLATE=infra.yml

ACTION="start"
PROTECT=true
HOSTED_ZONE="example.com."
S3_ENABLED="true"
RDS_ENABLED="true"
CACHE_ENABLED="true"
DNS_ENABLED="true"
DB_CLASS="db.t3.micro"
CACHE_NODE="cache.t2.micro"

# Read configuration from JSON if present
if [ -f "$CONFIG_FILE" ]; then
  ACTION="$(jq -r '.action // "start"' "$CONFIG_FILE")"
  PROTECT="$(jq -r '.protect // true' "$CONFIG_FILE")"
  STACK_NAME="$(jq -r '.stack_name // "spring-oauth-example"' "$CONFIG_FILE")"
  HOSTED_ZONE="$(jq -r '.services.route53.hosted_zone // "example.com."' "$CONFIG_FILE")"
  DNS_ENABLED="$(jq -r '.services.route53.enabled // true' "$CONFIG_FILE")"
  RDS_ENABLED="$(jq -r '.services.rds.enabled // true' "$CONFIG_FILE")"
  S3_ENABLED="$(jq -r '.services.s3.enabled // true' "$CONFIG_FILE")"
  CACHE_ENABLED="$(jq -r '.services.cache.enabled // true' "$CONFIG_FILE")"
  DB_CLASS="$(jq -r '.services.rds.db_instance_class // "db.t3.micro"' "$CONFIG_FILE")"
  CACHE_NODE="$(jq -r '.services.cache.node_type // "cache.t2.micro"' "$CONFIG_FILE")"
fi

if [ -z "$1" ] && [ -z "$DB_PASSWORD" ]; then
  echo "Usage: $0 <db-password> [hosted-zone]" >&2
  exit 1
fi

DB_PASSWORD="${1:-$DB_PASSWORD}"
HOSTED_ZONE="${2:-$HOSTED_ZONE}"

case "$ACTION" in
  start)
    deploy_stack
    ;;
  stop)
    remove_stack
    ;;
  *)
    echo "Unknown ACTION: $ACTION" >&2
    exit 1
    ;;
esac


