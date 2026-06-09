# Security and Compliance Guide

## Secrets management

- Do not commit secret files to the repository.
- Use external secret management like AWS Secrets Manager, HashiCorp Vault, or Azure Key Vault.
- The script `infra/secrets/fetch-secrets.sh` is a helper to convert secrets from AWS into `.env` files.
- Store production secrets in a secure vault and mount them into the runtime environment.

## Environment variables

Required environment variables for production:

- `DB_USER`
- `DB_PASSWORD`
- `KAFKA_BOOTSTRAP`
- `REDIS_HOST`
- `REDIS_PORT`
- `POSTGRES_DB`
- `SPRING_PROFILES_ACTIVE`

Use `infra/secrets/production.env.example` and `infra/secrets/development.env.example` only as templates.

## Dependency and container security

- Keep Java dependencies up to date.
- Use automated dependency scanning with Dependabot.
- Run container scanning on built images with tools like Trivy.

## Transport security

In production, all traffic should be encrypted:

- Use TLS for Kafka communication.
- Use TLS for PostgreSQL connections.
- Use TLS for Redis if the service depends on it.
- Secure actuator endpoints behind authentication and network policies.

## Access control and least privilege

- Run services as non-root users inside containers.
- Grant databases only the permissions required for each service.
- Keep service accounts isolated by environment.

## Monitoring and incident readiness

- Expose Actuator health and metrics for each service.
- Configure Prometheus and Grafana dashboards for:
  - request throughput
  - latency
  - error rates
  - fraud alerts
- Set up alerts for critical conditions such as failed database connections or Kafka consumer lag.

## Compliance considerations for banking

- Use audit-friendly logging with trace IDs and event IDs.
- Ensure sensitive data is never written to logs.
- Define retention and disposal policies for logs and metrics.
- Separate development, staging, and production environments.

## Recommended next steps

- Add OpenTelemetry distributed tracing across services.
- Add schema validation for Kafka events (Avro/JSON Schema).
- Harden the Docker runtime with container security benchmarks.
