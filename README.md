# Transactions API

A simple transactions management API with unit tests, containerization, and Kubernetes deployment.  
This project demonstrates API design, persistence in memory, test-driven development, and a containerized deployment pipeline.

## Continuous Integration

This project uses [CircleCI](https://circleci.com/) for continuous integration.

[![CircleCI](https://circleci.com/gh/austinatan22/transaction-api/tree/main.svg?style=svg)](https://circleci.com/gh/austinatan22/transaction-api/tree/main)

---

## Project Structure
```bash
transactions-api/
├── src/
│ ├── main/java/com/acme/transactions/ # Source code
│ │ ├── api/ # REST controllers
│ │ ├── model/ # Domain models
│ │ ├── repo/ # In-memory repository
│ │ └── service/ # Services (e.g. Exchange Rate)
│ └── test/java/... # Unit tests
├── k8s/ # Kubernetes manifests
├── pom.xml # Maven build file
├── .circleci/ # CircleCI pipeline
│ └── config.yml
└── README.md
```
---

## Requirements Implemented

- **API Layer**

  - Exposes endpoints for listing transactions with pagination and totals.
  - Security can be toggled off for easier testing.

- **Repository**

  - `InMemoryMonthlyStore` stores transactions per month, filtered by customer.

- **Service**

  - `ExchangeRateService` for currency normalization (mocked in tests).

- **Unit Tests**

  - `TransactionsControllerTest` validates filtering, totals, and pagination.
  - Implemented with JUnit 5 and Mockito.

- **Kubernetes**

  - `k8s/namespace.yaml` creates namespace `txapi`.
  - `k8s/deployment.yaml`, `service.yaml`, and `configmap.yaml` configure the app.
  - `k8s/redpanda.yaml` deploys Redpanda for messaging.

- **CI/CD**
  - CI pipeline runs `mvn test` automatically on push.

---

## Getting Started

### Build & Test

````bash
docker run --rm -v "${PWD}:/src" -w /src maven:3.9-eclipse-temurin-21 mvn clean test

docker build -t transactions-api .
docker run -p 8080:8080 transactions-api

kubectl create namespace txapi
kubectl apply -f k8s/ -n txapi
kubectl get pods -n txapi
````
---

## Context (C4 Level 1)

The **Transactions API** is part of a financial system.
It allows customers to view their transactions, credits, and debits.

**Actors**:
- **Customer** – retrieves transactions via API.
- **Back-office systems** – may query aggregates.

**Systems**:
- **Transactions API** – exposes REST endpoints.
- **Redpanda** – provides streaming/messaging backbone.
- **External FX Service** – converts amounts into base currency.



## Container Diagram (C4 Level 2)
![diagram](https://github.com/Austinatan22/Transaction-API/blob/main/C4L2%20Diagram.png?raw=true)

---

## Data Model

**Transaction**
- `id`: String
- `customerId`: String
- `amount`: (BigDecimal, currency)
- `account`: String
- `date`: String (ISO)
- `description`: String

**Totals**
- `credit`: BigDecimal
- `debit`: BigDecimal
- `baseCurrency`: String

---

## Deployment

- Packaged as a **Docker image**.
- Deployed to **Kubernetes (namespace: txapi)**.
- Redpanda deployed as dependency.
- Config via `ConfigMap`.







