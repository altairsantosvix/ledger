# Banking Resilience Lab

## Visão geral do sistema

Este projeto implementa um conjunto de microsserviços Java Spring Boot para um fluxo bancário resiliente:

- `payment-service`: recebe requisições de pagamento, valida e publica eventos em Kafka.
- `ledger-service`: consome eventos de pagamento e persiste lançamentos contábeis no PostgreSQL.
- `fraud-service`: consome eventos de pagamento, realiza análise de fraude e publica alertas.
- `common-kafka`: módulo compartilhado com configuração e constantes de Kafka.

Cada serviço possui monitoramento com Spring Boot Actuator e exportação de métricas Prometheus.

## Dev responsável

- **Altair Santos**

## Estrutura de Docker

O sistema possui configuração para ambientes de produção e desenvolvimento.

### Arquivos principais

- `docker-compose.yml`: ambiente de produção padrão.
- `docker-compose.dev.yml`: sobreposição para ambiente de desenvolvimento/teste.
- `services/payment-service/Dockerfile`
- `services/ledger-service/Dockerfile`
- `services/fraud-service/Dockerfile`
- `infra/secrets/fetch-secrets.sh`: script para gerar arquivos `.env` a partir do AWS Secrets Manager.

### Serviços Docker

- `payment-service`: expõe porta `8081`
- `ledger-service`: expõe porta `8082`
- `fraud-service`: expõe porta `8083`
- `kafka`: broker Kafka
- `zookeeper`: requisito Kafka
- `postgres`: banco de dados PostgreSQL
- `redis`: cache Redis

## Configuração de secrets

Os serviços dependem de variáveis de ambiente externas. Os arquivos de env não devem ser comitados no repositório.

Arquivos de exemplo:

- `infra/secrets/production.env.example`
- `infra/secrets/development.env.example`

### Gerar secrets a partir do AWS Secrets Manager

```bash
cd /path/to/project
./infra/secrets/fetch-secrets.sh infra/secrets/production.env
```

Para desenvolvimento:

```bash
./infra/secrets/fetch-secrets.sh infra/secrets/development.env
```

### Variáveis importantes

- `DB_USER`
- `DB_PASSWORD`
- `KAFKA_BOOTSTRAP`
- `REDIS_HOST`
- `REDIS_PORT`
- `POSTGRES_DB`
- `SPRING_PROFILES_ACTIVE`

## Executando em produção

```bash
cd /path/to/project
docker compose up --build
```

## Executando em desenvolvimento

```bash
cd /path/to/project
docker compose -f docker-compose.yml -f docker-compose.dev.yml up --build
```

## Serviços e endpoints de observabilidade

Cada serviço expõe endpoints do Spring Boot Actuator:

- `/actuator/health`
- `/actuator/info`
- `/actuator/metrics`
- `/actuator/prometheus`

## Métricas implementadas

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

## Observações finais

- `infra/secrets/production.env` e `infra/secrets/development.env` não devem ser adicionados ao controle de versão.
- Se houver erro no `docker compose up`, verifique se o comando `docker` está instalado e se o arquivo de secrets foi gerado corretamente.
- O projeto utiliza Java 21 e Spring Boot 3.5.

## Documentação adicional

- `docs/ARCHITECTURE.md`: visão de arquitetura e preparo para produção.
- `docs/SECURITY.md`: segurança, compliance e gerenciamento de segredos.
- `docs/DEPLOYMENT.md`: guia de implantação para produção e desenvolvimento.
- `docs/OBSERVABILITY.md`: guia de monitoramento e métricas.
- `docs/CI-CD.md`: guia de integração contínua e entrega contínua.
- `.github/workflows/ci.yml`: pipeline CI para build e teste.
- `.github/workflows/codeql-analysis.yml`: análise de segurança estática.
- `.github/dependabot.yml`: automação de atualização de dependências.
