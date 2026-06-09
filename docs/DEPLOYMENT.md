# Deployment Guide

## Overview

This guide describes how to deploy the Ledger banking platform in a production-like environment and in development/test mode.

## Deployment goals

- Use containerized services with clear separation of runtime configuration.
- Enable health checks and easy service updates.
- Keep secrets out of the source tree.
- Support both production and development workflows.

## Production deployment

### Prerequisites

- Docker and Docker Compose installed.
- External secret management in place.
- Network access to Kafka, PostgreSQL, and Redis.

### Production startup

1. Generate or obtain the production secret file:

```bash
cd /Users/altairsantos/projetos/Ledger
./infra/secrets/fetch-secrets.sh infra/secrets/production.env
```

2. Start the production stack:

```bash
docker compose up --build -d
```

3. Verify services:

- `http://localhost:8081/actuator/health`
- `http://localhost:8082/actuator/health`
- `http://localhost:8083/actuator/health`

### Production best practices

- Run Docker in a secured environment behind a firewall.
- Use persistent volumes for PostgreSQL, Kafka, and Redis data.
- Do not store credentials in version control.
- Monitor service health and collect logs centrally.

## Development deployment

### Prerequisites

- Docker and Docker Compose installed.
- A development secrets file generated from the template.

### Development startup

1. Create the development secrets file from the template:

```bash
cp infra/secrets/development.env.example infra/secrets/development.env
```

2. Start the development stack:

```bash
docker compose -f docker-compose.yml -f docker-compose.dev.yml up --build
```

3. Use `SPRING_PROFILES_ACTIVE=dev` for service configurations.

### Development tips

- Use mock or local testing data rather than production secrets.
- Prefer the development overlay for feature testing and faster iteration.
- Keep the same container and networking structure as production.

## Kubernetes readiness

This project is currently containerized and can be adapted to Kubernetes.

### Recommended next steps

- Create YAML manifests or Helm charts for each service.
- Use a Kubernetes Secret for credentials instead of `.env` files.
- Deploy PostgreSQL, Kafka, Redis, and services into a shared namespace.
- Add readiness and liveness probes for each Spring Boot service.

## Rollback strategy

- Keep old container images available.
- Roll back by re-deploying the previous image tag.
- Validate health checks before marking the deployment as successful.

## Service scaling

- Scale `payment-service` and `fraud-service` horizontally as needed.
- Keep `ledger-service` scaling aligned with database capacity.
- Use Kafka topic partitions and consumer groups for throughput.
