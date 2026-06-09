# Architecture Overview

## Goal

This project is designed as a resilient banking microservices platform with event-driven processing, strong observability, and clear separation of responsibilities.

## Core components

- `payment-service`
  - Receives payment requests
  - Validates payment payloads
  - Publishes `PaymentEvent` messages to Kafka

- `ledger-service`
  - Consumes payment events
  - Creates ledger entries in PostgreSQL
  - Tracks accounting events for audit and reconciliation

- `fraud-service`
  - Consumes payment events
  - Applies fraud detection rules
  - Publishes fraud alerts to Kafka

- `common-kafka`
  - Shared Kafka configuration and topic constants

## Event flow

1. A request arrives at `payment-service`.
2. `payment-service` validates and publishes a `PaymentEvent` to Kafka.
3. `ledger-service` consumes the `PAYMENTS` topic and persists ledger entries.
4. `fraud-service` consumes the same payment event stream for fraud analysis.
5. If fraud is detected, `fraud-service` publishes an alert to the `FRAUD_ALERTS` topic.

## Infrastructure layout

- Kafka broker and Zookeeper for event streaming
- PostgreSQL for persistent ledger storage
- Redis used by payment service for caching or application state

## Deployment and runtime

- Each service runs in its own container.
- Services expose Spring Boot Actuator endpoints for health, metrics, and info.
- The system is designed to support:
  - independent scaling per service
  - rolling deployments
  - centralized monitoring

## Production readiness

Key production-ready features:

- containerized builds with dedicated Dockerfiles
- healthchecks for service availability
- persistent volumes for database and broker storage
- external secret management for credentials
- observability via Prometheus metrics endpoints

## Recommended improvements

To further strengthen the architecture for a banking environment:

- add an API gateway or ingress for routing and security
- enable TLS for all service-to-service communication
- implement schema registry for Kafka event contracts
- add distributed tracing with OpenTelemetry
- deploy on Kubernetes/OpenShift with Helm charts
