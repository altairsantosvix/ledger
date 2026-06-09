# Observability Guide

## Goals

This guide explains how to monitor the Ledger platform and gather telemetry data for performance, reliability, and incident response.

## Actuator endpoints

Each service exposes Spring Boot Actuator endpoints:

- `/actuator/health`
- `/actuator/info`
- `/actuator/metrics`
- `/actuator/prometheus`

These endpoints provide basic health checks and Prometheus-compatible metrics.

## Prometheus metrics

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

## Recommended observability stack

- Prometheus for metrics scraping.
- Grafana for dashboards.
- Alertmanager for alerts.
- OpenTelemetry for distributed tracing.

## Example Prometheus scrape configuration

```yaml
scrape_configs:
  - job_name: 'payment-service'
    metrics_path: /actuator/prometheus
    static_configs:
      - targets: ['localhost:8081']

  - job_name: 'ledger-service'
    metrics_path: /actuator/prometheus
    static_configs:
      - targets: ['localhost:8082']

  - job_name: 'fraud-service'
    metrics_path: /actuator/prometheus
    static_configs:
      - targets: ['localhost:8083']
```

## Grafana dashboard ideas

- Payment throughput and latency
- Ledger processing rate and entry creation
- Fraud alerts and alert creation rate
- System health and service availability

## Distributed tracing

For more advanced observability, add distributed tracing with OpenTelemetry:

- instrument HTTP requests
- instrument Kafka producer/consumer flows
- propagate trace IDs across services
- correlate logs, metrics, and traces

## Alerting

Configure alerts for key conditions:

- service health failure
- high error rate
- Kafka consumer lag
- database connection issues
- fraud alert spikes

## Logging best practices

- Use structured logs when possible.
- Include trace IDs in log events.
- Avoid logging sensitive data such as passwords or full payment details.
- Centralize logs in a system like ELK, Loki, or another observability platform.
