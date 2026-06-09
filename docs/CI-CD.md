# CI/CD Guide

## Overview

This guide describes the continuous integration and delivery strategy for the Ledger banking platform.

The project already includes GitHub Actions workflows for:

- build and test (`.github/workflows/ci.yml`)
- static security analysis (`.github/workflows/codeql-analysis.yml`)
- dependency updates via Dependabot (`.github/dependabot.yml`)

## CI principles

The CI pipeline should ensure that every change is:

- validated by compilation
- covered by unit tests
- built reproducibly
- checked for dependency and security issues

## Existing CI workflow

The current workflow runs on `push` and `pull_request` for the main branch.

Steps include:

1. Checkout repository.
2. Set up Java 21.
3. Cache Maven dependencies.
4. Build the Java modules and run tests.

## Recommended improvements

To strengthen this pipeline for a banking environment, add:

- integration tests with Testcontainers for Kafka/Postgres/Redis
- contract tests for Kafka event schemas
- code formatting and lint checks
- a security scan for container images (e.g. Trivy)
- performance smoke tests for critical flows
- pipeline stages for staging and production promotion

## Release and delivery

A professional CD process can include:

- build artifacts versioned by Git tag
- signed Docker images pushed to a registry
- deployment to a staging environment after successful CI
- manual approval before production release

### Sample release flow

1. Developer opens a pull request.
2. CI validates the code and tests.
3. On merge, a build artifact is created.
4. A container image is published to the registry.
5. Staging deployment runs automatically.
6. Production deployment is triggered after approval.

## GitHub Actions recommendations

- add a `release.yml` workflow to build and publish images.
- use `workflow_dispatch` for manual deployments.
- add branch protections on `main/master`.
- enable required checks for pull requests.

## Metrics and quality gates

Track and enforce:

- test coverage thresholds
- static analysis issue counts
- security vulnerabilities in dependencies
- package and container image scan results

## Notes

This guide is intended to make the project more attractive for enterprise banking roles by demonstrating a mature delivery process.
