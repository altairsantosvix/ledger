# Banking Resilience Lab

## System Overview

This project implements a set of Java Spring Boot microservices for a resilient banking flow:

- `payment-service`: receives payment requests, validates them, and publishes events to Kafka.
- `ledger-service`: consumes payment events and persists ledger entries in PostgreSQL.
- `fraud-service`: consumes payment events, performs fraud analysis, and publishes alerts.
- `common-kafka`: shared module with Kafka configuration and constants.

Each service includes monitoring with Spring Boot Actuator and Prometheus metrics export.

## Responsible Developer

- **Altair Santos**

## Docker Structure

The system includes configuration for both production and development environments.

### Main files

- `docker-compose.yml`: production-ready environment definition.
- `docker-compose.dev.yml`: development/test overlay configuration.
- `services/payment-service/Dockerfile`
- `services/ledger-service/Dockerfile`
- `services/fraud-service/Dockerfile`
- `infra/secrets/fetch-secrets.sh`: script to generate `.env` files from AWS Secrets Manager.

### Docker services

- `payment-service`: exposes port `8081`
- `ledger-service`: exposes port `8082`
- `fraud-service`: exposes port `8083`
- `kafka`: Kafka broker
- `zookeeper`: Kafka dependency
- `postgres`: PostgreSQL database
- `redis`: Redis cache

## Secrets configuration

The services depend on external environment variables. Secret files should not be committed to the repository.

Example files:

- `infra/secrets/production.env.example`
- `infra/secrets/development.env.example`

### Generate secrets from AWS Secrets Manager

```bash
cd /Users/altairsantos/projetos/Ledger
./infra/secrets/fetch-secrets.sh infra/secrets/production.env
```

For development:

```bash
./infra/secrets/fetch-secrets.sh infra/secrets/development.env
```

### Important variables

- `DB_USER`
- `DB_PASSWORD`
- `KAFKA_BOOTSTRAP`
- `REDIS_HOST`
- `REDIS_PORT`
- `POSTGRES_DB`
- `SPRING_PROFILES_ACTIVE`

## Running in production

```bash
cd /Users/altairsantos/projetos/Ledger
docker compose up --build
```

## Running in development

```bash
cd /Users/altairsantos/projetos/Ledger
docker compose -f docker-compose.yml -f docker-compose.dev.yml up --build
```

## Observability endpoints

Each service exposes Spring Boot Actuator endpoints:

- `/actuator/health`
- `/actuator/info`
- `/actuator/metrics`
- `/actuator/prometheus`

## Implemented metrics

### payment-service
- `payment_requests_total`
- `payment_transfer_duration_seconds`
- `payment_events_published_total`

### ledger-service
- `ledger_payment_events_processed_total`
- `ledger_entries_created_total`
- `ledger_processing_duration_seconds`

### fraud-service
- `fraud_payment_events_analyzed_total`
- `fraud_alerts_created_total`
- `fraud_known_recipient_accounts`
- `fraud_alerts_published_total`

## Notes

- `infra/secrets/production.env` and `infra/secrets/development.env` should remain out of version control.
- If `docker compose up` fails, verify Docker is installed and the required secrets file exists.
- The project uses Java 21 and Spring Boot 3.5.

## Additional documentation

- `docs/ARCHITECTURE.md`: architecture overview and production readiness.
- `docs/SECURITY.md`: security, compliance and secret management guidance.
- `docs/DEPLOYMENT.md`: deployment guide for production and development.
- `docs/OBSERVABILITY.md`: observability and monitoring guide.
- `docs/CI-CD.md`: continuous integration and delivery guide.
- `.github/workflows/ci.yml`: CI pipeline for build and test.
- `.github/workflows/codeql-analysis.yml`: static security scanning.
- `.github/dependabot.yml`: dependency update automation.
