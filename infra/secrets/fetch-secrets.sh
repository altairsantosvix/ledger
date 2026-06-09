#!/usr/bin/env bash

set -e

OUTPUT=${1:-infra/secrets/production.env}

echo "Fetching secrets from AWS Secrets Manager..."

# exemplo: secret JSON no AWS
SECRET_JSON=$(aws secretsmanager get-secret-value \
  --secret-id banking-lab-secrets \
  --query SecretString \
  --output text)

echo "$SECRET_JSON" > infra/secrets/raw.json

# converter JSON → env file
jq -r 'to_entries | .[] | "\(.key)=\(.value)"' <<<"$SECRET_JSON" > "$OUTPUT"

echo "Secrets loaded into $OUTPUT"
