<p align="center">
  <h1 align="center">Emras Backend</h1>
  <p align="center">Production-grade microservices e-commerce platform for a Bangladesh-based clothing shop</p>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk" />
  <img src="https://img.shields.io/badge/Spring%20Boot-4.0.6-brightgreen?style=flat-square&logo=springboot" />
  <img src="https://img.shields.io/badge/Spring%20Cloud-2025.1.1-brightgreen?style=flat-square" />
  <img src="https://img.shields.io/badge/Apache%20Kafka-7.7.1-black?style=flat-square&logo=apachekafka" />
  <img src="https://img.shields.io/badge/PostgreSQL-18-blue?style=flat-square&logo=postgresql" />
  <img src="https://img.shields.io/badge/Redis-7.2-red?style=flat-square&logo=redis" />
  <img src="https://img.shields.io/badge/License-MIT-yellow?style=flat-square" />
</p>

---

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Services](#services)
- [Tech Stack](#tech-stack)
- [Key Design Patterns](#key-design-patterns)
- [Port Assignments](#port-assignments)
- [Infrastructure](#infrastructure)
- [Getting Started](#getting-started)
- [API Overview](#api-overview)
- [Kafka Event Topics](#kafka-event-topics)
- [Project Structure](#project-structure)
- [Branch Strategy](#branch-strategy)
- [Environment Configuration](#environment-configuration)

---

## Overview

Emras is a real e-commerce backend platform built for a clothing retail shop in Bangladesh. It is designed as a learning project covering industry-grade distributed systems patterns while serving as a functional production backend.

**What it sells:** Shirts, t-shirts, and other garments — with full variant support (size × color).

**Target market:** Bangladesh — with Bangladesh-specific payment gateways (bKash, Nagad, Rocket), local courier integrations (Pathao, Steadfast), Bangla language support, and BD address format (division / district / thana).

---

## Architecture

The system follows a **microservices architecture** where each business domain is an independently deployable Spring Boot application. Services communicate in two ways:

```
React Frontend
       │
       ▼
┌─────────────────────────────────────────────────────┐
│             API Gateway  :8080                       │
│  JWT validation · CORS · Routing · Rate limiting     │
└──────┬───────┬───────┬───────┬───────┬──────────────┘
       │       │       │       │       │
       ▼       ▼       ▼       ▼       ▼
   Auth    User   Product  Inventory  Order  ...more
   :8081   :8082   :8083    :8084    :8085
       │       │       │       │       │
       └───────┴───────┴───────┴───────┘
                        │
                 Apache Kafka
              (async event bus)
                        │
       ┌────────────────┼────────────────┐
       ▼                ▼                ▼
   Payment          Notification    Inventory
   :8086             :8087          (consumer)
```

### Communication Patterns

| Pattern | When Used | Example |
|---|---|---|
| **Synchronous (FeignClient / HTTP)** | Need the answer immediately to continue | Order Service fetches product price before creating order |
| **Asynchronous (Kafka events)** | Fire-and-forget; downstream acts independently | `order.created` → Inventory Service reserves stock |

---

## Services

| Service | Port | Schema | Status | Responsibility |
|---|---|---|---|---|
| **API Gateway** | 8080 | — | ✅ Complete | Single entry point, JWT filter, CORS, routing |
| **Auth Service** | 8081 | `schema_auth` | ✅ Complete | Register, login (JWT/OTP), refresh tokens, password reset |
| **User Service** | 8082 | `schema_user` | ✅ Complete | Profile, addresses (BD format), wishlist |
| **Product Service** | 8083 | `schema_product` | ✅ Complete | Catalog, categories, variants (SKU), Redis cache |
| **Inventory Service** | 8084 | `schema_inventory` | ✅ Complete | Stock tracking, reservation, optimistic locking, outbox |
| **Order Service** | 8085 | `schema_order` | 🔨 In Progress | Order lifecycle, Saga choreography, outbox pattern |
| **Payment Service** | 8086 | `schema_payment` | ⏳ Planned | bKash, Nagad, Rocket, SSLCommerz, COD |
| **Notification Service** | 8087 | `schema_notification` | ⏳ Planned | Email, SMS (BD), push, in-app — EN/BN templates |
| **Delivery Service** | 8088 | `schema_delivery` | ⏳ Planned | Pathao/Steadfast/Redx API, district-based shipping fee |
| **AI Assistant Service** | 8089 | `schema_ai` | ⏳ Planned | LangChain4j, RAG, Bangla/English conversational shopping |
| **Review Service** | 8090 | `schema_review` | ⏳ Planned | Star ratings, verified-purchase reviews, moderation |
| **Analytics Service** | 8091 | `schema_analytics` | ⏳ Planned | Revenue dashboards, top products, Kafka-driven aggregation |
| **Discount Service** | 8092 | `schema_discount` | ⏳ Planned | Coupon CRUD, flash sale scheduler, discount rules |

---

## Tech Stack

### Backend
| Technology | Version | Purpose |
|---|---|---|
| Java | 21 (LTS) | Language runtime |
| Spring Boot | 4.0.6 | Core framework for all microservices |
| Spring Cloud | 2025.1.1 | Config, Eureka, Gateway, OpenFeign |
| Spring Cloud Gateway (MVC) | — | API Gateway — `spring-cloud-starter-gateway-server-webmvc` |
| Spring Data JPA | — | ORM layer (Hibernate 6.x) |
| Spring Kafka | — | Kafka producer/consumer integration |
| Spring Data Redis | — | Caching, session store, rate-limit state |
| OpenFeign | — | Declarative REST client for sync inter-service calls |
| MapStruct | 1.5.5.Final | Compile-time DTO ↔ Entity mapping |
| Lombok | — | Boilerplate elimination |
| Flyway | — | Versioned SQL migrations per service |
| Springdoc OpenAPI | 3.0.2 | Auto-generated Swagger UI |
| JJWT | 0.12.6 | JWT creation and validation |

### Messaging
| Technology | Version | Purpose |
|---|---|---|
| Apache Kafka | 7.7.1 (Confluent) | Core event streaming |
| Confluent Schema Registry | 7.7.1 | Schema management (port 8093) |

### Data & Storage
| Technology | Version | Purpose |
|---|---|---|
| PostgreSQL | 18 | Primary relational DB (native Windows, not Docker) |
| Redis | 7.2 | Cache, OTP store, session, rate-limit state |
| Elasticsearch | 8.x | Full-text product search (planned) |
| MinIO | — | Object storage for product images and PDFs |

### Observability
| Technology | Purpose |
|---|---|
| Prometheus + Grafana | Metrics collection and dashboards |
| Zipkin | Distributed tracing |
| Spring Actuator | Health checks, metrics endpoints |
| Micrometer | Metrics instrumentation |

---

## Key Design Patterns

### 1. Outbox Pattern
Guarantees that Kafka events are never lost even if Kafka is temporarily down.

```
Business logic executes
        │
        ▼
DB transaction (atomic):
  ├── Save entity to its table
  └── Write event to outbox_events table (PENDING)
        │
        ▼  (5 seconds later)
@Scheduled OutboxPublisherService reads PENDING events
        │
        ▼
Publishes to Kafka → marks event as PUBLISHED
```

Every service that publishes Kafka events implements this pattern. If Kafka fails, the event stays `PENDING` and gets retried up to 5 times before being marked `FAILED`.

### 2. Idempotency (Processed Events Table)
Every Kafka consumer checks a `processed_events` table before processing a message. If the `eventId` was already handled, the message is silently acknowledged and skipped. This makes all consumers safe against Kafka's at-least-once delivery.

### 3. Saga Pattern (Choreography)
The order placement flow spans multiple services with no central orchestrator. Each service reacts to the previous one's Kafka event.

```
Order Service → order.created
                    │
                    ▼
          Inventory Service reserves stock
          → inventory.reserved   (success)
          → inventory.reservation.failed  (failure — compensating)
                    │
                    ▼ (on success)
          Payment Service charges customer
          → payment.success
          → payment.failed  (compensating → order.cancelled → inventory releases)
                    │
                    ▼ (on success)
          Order status → COMPLETED
          Notification Service sends SMS/email
```

### 4. Optimistic Locking
The `InventoryItem` entity uses a `@Version` field. When two concurrent order requests try to reserve the last unit of stock simultaneously, one succeeds and the other gets an `ObjectOptimisticLockingFailureException` — which is caught and converted to a reservation failure event. No overselling possible.

### 5. FeignClient (Synchronous Inter-Service Calls)
When Order Service needs the current price of a product before creating an order, it calls Product Service directly via FeignClient. The target URL is read from the active profile's properties file — never hardcoded in Java.

```java
@FeignClient(name = "product-service", url = "${feign.clients.product-service-url}")
public interface ProductClient {
    @GetMapping("/api/v1/products/{id}")
    ApiResponse<ProductResponse> getProductById(@PathVariable Long id);
}
```

### 6. Database per Service (Schema Isolation)
All services share one PostgreSQL instance but each owns a separate schema. No service can query another service's schema from application code. Cross-service data flows only through REST APIs or Kafka events.

---

## Port Assignments

### Application Services
| Service | Port |
|---|---|
| API Gateway | 8080 |
| Auth Service | 8081 |
| User Service | 8082 |
| Product Service | 8083 |
| Inventory Service | 8084 |
| Order Service | 8085 |
| Payment Service | 8086 |
| Notification Service | 8087 |
| Delivery Service | 8088 |
| AI Assistant Service | 8089 |
| Review Service | 8090 |
| Analytics Service | 8091 |
| Discount Service | 8092 |

### Infrastructure (Docker)
| Service | Port |
|---|---|
| Kafka (external) | 29092 |
| Kafka UI | 9091 |
| Schema Registry | 8093 |
| Redis | 6379 |
| Elasticsearch | 9200 |
| MinIO (API) | 9000 |
| MinIO (Console) | 9001 |
| Prometheus | 9090 |
| Grafana | 3001 |
| Zipkin | 9411 |

> **Note:** PostgreSQL 18 runs natively on Windows, not inside Docker.

---

## Infrastructure

All infrastructure runs via Docker Compose. PostgreSQL runs natively on Windows.

### Starting infrastructure

```bash
# Start all infrastructure containers
docker-compose up -d

# Start only specific services
docker-compose up -d redis kafka zookeeper kafka-ui schema-registry
```

### Useful URLs once running

| Service | URL |
|---|---|
| Kafka UI | http://localhost:9091 |
| MinIO Console | http://localhost:9001 |
| Grafana | http://localhost:3001 |
| Zipkin | http://localhost:9411 |
| Prometheus | http://localhost:9090 |

---

## Getting Started

### Prerequisites

- Java 21 (LTS)
- Maven 3.9+
- Docker Desktop
- PostgreSQL 18 (native Windows installation)
- IntelliJ IDEA (recommended)

### 1. Clone the repository

```bash
git clone https://github.com/Rifat-Tipu/emras-backend.git
cd emras-backend
```

### 2. Configure the database

Create the database and user in PostgreSQL:

```sql
CREATE USER emras_user WITH PASSWORD 'your_password';
CREATE DATABASE emras_db OWNER emras_user;
```

### 3. Set up environment files

Each service has an `application-local.properties.example` file. Copy it and fill in your values:

```bash
# Example for auth-service
cp auth-service/src/main/resources/application-local.properties.example \
   auth-service/src/main/resources/application-local.properties
```

Fill in your database credentials, Redis password, JWT secret, and Kafka bootstrap server in each service's `application-local.properties`.

For Docker Compose secrets:

```bash
cp .env.example .env
# Edit .env with your Redis and MinIO passwords
```

### 4. Start infrastructure

```bash
docker-compose up -d
```

### 5. Run services in IntelliJ

Start services in this order (each is a separate Spring Boot run configuration):

1. `AuthServiceApplication` (8081)
2. `ApiGatewayApplication` (8080)
3. `UserServiceApplication` (8082)
4. `ProductServiceApplication` (8083)
5. `InventoryServiceApplication` (8084)
6. `OrderServiceApplication` (8085)

Flyway migrations run automatically on startup — tables are created on first run only.

### 6. Verify startup

Check that each service is healthy:

```bash
curl http://localhost:8080/actuator/health   # Gateway
curl http://localhost:8081/actuator/health   # Auth
curl http://localhost:8082/actuator/health   # User
```

### 7. Access Swagger UI

Each service exposes its own Swagger UI:

| Service | Swagger URL |
|---|---|
| Auth | http://localhost:8081/swagger-ui.html |
| User | http://localhost:8082/swagger-ui.html |
| Product | http://localhost:8083/swagger-ui.html |
| Inventory | http://localhost:8084/swagger-ui.html |
| Order | http://localhost:8085/swagger-ui.html |

---

## API Overview

All requests should go through the API Gateway on port `8080`.

### Authentication

```
POST /api/v1/auth/register     — Create account
POST /api/v1/auth/login        — Login with email + password → returns accessToken
POST /api/v1/auth/login/otp/send   — Send OTP to phone
POST /api/v1/auth/login/otp/verify — Verify OTP → returns accessToken
POST /api/v1/auth/refresh      — Refresh access token
POST /api/v1/auth/logout       — Invalidate refresh token
POST /api/v1/auth/password/forgot  — Request reset link
POST /api/v1/auth/password/reset   — Set new password
```

### Protected Endpoints (require `Authorization: Bearer <token>`)

```
GET  /api/v1/users/profile         — Get own profile
PUT  /api/v1/users/profile         — Update profile
GET  /api/v1/users/addresses       — List delivery addresses
POST /api/v1/users/addresses       — Add address
PUT  /api/v1/users/addresses/{id}  — Update address
DELETE /api/v1/users/addresses/{id} — Delete address
PUT  /api/v1/users/addresses/{id}/default — Set default

GET  /api/v1/products              — Browse products (public)
GET  /api/v1/products/{id}         — Get product detail (public)
GET  /api/v1/categories            — List categories (public)

POST /api/v1/admin/products        — Create product (ROLE_ADMIN)
POST /api/v1/admin/inventory       — Add inventory item (ROLE_ADMIN)
POST /api/v1/admin/inventory/{sku}/stock-in — Add stock (ROLE_ADMIN)

GET  /api/v1/inventory/{sku}       — Check stock (public)
POST /api/v1/orders                — Place order
GET  /api/v1/orders/history        — Order history
GET  /api/v1/orders/{id}           — Order detail
PATCH /api/v1/orders/{id}/cancel   — Cancel order
```

### Standard Response Format

Every endpoint returns the same `ApiResponse<T>` wrapper:

```json
{
  "success": true,
  "status": 200,
  "message": "Operation successful.",
  "data": { ... },
  "timestamp": "2026-06-27T10:00:00Z"
}
```

Error responses include `errorCode` and `path`:

```json
{
  "success": false,
  "status": 404,
  "message": "Order not found.",
  "errorCode": "ORDER_NOT_FOUND",
  "path": "/api/v1/orders/999",
  "timestamp": "2026-06-27T10:00:00Z"
}
```

---

## Kafka Event Topics

| Topic | Publisher | Consumers |
|---|---|---|
| `user.registered` | Auth Service | User Service (create profile) |
| `order.created` | Order Service | Inventory Service (reserve stock) |
| `inventory.reserved` | Inventory Service | Order Service (confirm order) |
| `inventory.reservation.failed` | Inventory Service | Order Service (cancel — compensating) |
| `order.confirmed` | Order Service | Payment Service, Notification Service |
| `order.cancelled` | Order Service | Inventory Service (release stock) |
| `payment.success` | Payment Service | Order Service, Notification Service |
| `payment.failed` | Payment Service | Order Service (cancel — compensating) |
| `inventory.low-stock` | Inventory Service | Notification Service (admin alert) |
| `inventory.out-of-stock` | Inventory Service | Product Service, Notification Service |
| `product.updated` | Product Service | Elasticsearch sync, Redis cache eviction |

All events carry an `eventId` (UUID) for consumer idempotency. Events are written to an `outbox_events` table in the same DB transaction as the triggering business operation, then published to Kafka by a `@Scheduled` outbox publisher every 5 seconds.

---

## Project Structure

```
emras-backend/
├── .env.example                    ← Docker secrets template (copy to .env)
├── .gitignore                      ← Excludes .env, application-local.properties
├── docker-compose.yml              ← All infrastructure containers
│
├── api-gateway/                    ← :8080 — single entry point
│   └── src/main/java/com/emras/api_gateway/
│       ├── config/
│       │   ├── GatewayConfig.java           ← Route definitions
│       │   ├── ServiceUrlProperties.java    ← Externalized service URLs
│       │   └── CorsConfig.java
│       ├── filter/
│       │   ├── JwtAuthenticationFilter.java ← JWT validation
│       │   └── MutableHttpServletRequest.java
│       └── constant/
│           └── GateWayConstant.java
│
├── auth-service/                   ← :8081 — JWT, OTP, refresh tokens
├── user-service/                   ← :8082 — profile, addresses, wishlist
├── product-service/                ← :8083 — catalog, variants, Redis cache
├── inventory-service/              ← :8084 — stock, outbox, idempotency
├── order-service/                  ← :8085 — Saga, FeignClient, outbox
│
└── (each service follows this internal structure)
    src/main/java/com/emras/{service}/
    ├── constant/         ← ApiEndpointConstant, KafkaTopic, ErrorMessages
    ├── model/            ← ApiResponse<T> wrapper
    ├── entity/           ← JPA entities extending BaseEntity
    ├── repository/       ← Spring Data JPA repositories
    ├── dto/
    │   ├── request/      ← Validated incoming request records
    │   └── response/     ← Outgoing response records
    ├── mapper/           ← MapStruct mappers (Entity ↔ DTO)
    ├── service/          ← Business logic interfaces + implementations
    ├── controller/       ← REST controllers
    ├── kafka/
    │   ├── producer/     ← Outbox-based event producers
    │   └── consumer/     ← Idempotent event consumers
    ├── exception/        ← Custom exceptions + GlobalExceptionHandler
    └── config/           ← KafkaConfig, AuditConfig
```

---

## Branch Strategy

This project uses **GitFlow** with squash-merge PRs.

| Branch | Purpose |
|---|---|
| `main` | Production-ready releases only |
| `develop` | Integration branch — all feature PRs merge here |
| `feature/auth-module` | Completed — Auth Service |
| `feature/api-gateway` | Completed — API Gateway |
| `feature/user-service` | Completed — User Service |
| `feature/product-service` | Completed — Product Service |
| `feature/inventory-service` | Completed — Inventory Service |
| `feature/order-service` | In progress — Order Service |

**Workflow:**
```bash
git checkout develop
git pull origin develop
git checkout -b feature/your-service

# ... implement ...

git add .
git commit -m "feat(service-name): description"
git push origin feature/your-service
# Open PR → develop → Squash and merge
```

---

## Environment Configuration

Each service uses Spring profiles: `local`, `dev`, `prod`.

### Files per service

| File | Committed? | Contains |
|---|---|---|
| `application.properties` | ✅ Yes | Port, Flyway config, Swagger, profile = local |
| `application-local.properties` | ❌ No (gitignored) | Real DB/Redis/Kafka credentials |
| `application-local.properties.example` | ✅ Yes | Placeholder template — copy and fill in |
| `application-dev.properties` | ✅ Yes | Docker container names as URLs |
| `application-prod.properties` | ❌ No (gitignored) | Production env var references |

### API Gateway service URLs

All downstream service URLs are externalized in `application-local.properties` of the Gateway — no URLs are hardcoded in Java:

```properties
gateway.services.auth-service-url=http://localhost:8081
gateway.services.user-service-url=http://localhost:8082
gateway.services.product-service-url=http://localhost:8083
gateway.services.inventory-service-url=http://localhost:8084
gateway.services.order-service-url=http://localhost:8085
# ...and so on
```

In Docker Compose (dev profile), these become container names:
```properties
gateway.services.auth-service-url=http://auth-service:8081
```

In Kubernetes (prod profile), these become environment variables:
```properties
gateway.services.auth-service-url=${AUTH_SERVICE_URL}
```

---

## Roles

| Role | Access |
|---|---|
| `ROLE_CUSTOMER` | Browse products, place orders, manage own profile |
| `ROLE_ADMIN` | All customer access + product/inventory management, order status updates |
| `ROLE_STAFF` | Order management, inventory viewing |
| `ROLE_VIEWER` | Read-only admin panel access |

The Gateway extracts roles from the JWT and forwards them as the `X-User-Role` header to downstream services. Services read `X-User-Id` and `X-User-Role` headers — they never validate JWT themselves.

---

## Author

**Rifat Hossain** — BCSE Student, IUBAT (ID: 22103318)

Built as both a real production backend for a friend's clothing shop and an advanced learning project covering Spring Boot microservices, Kafka event streaming, distributed systems patterns, and clean architecture.

> GitHub: [github.com/Rifat-Tipu/emras-backend](https://github.com/Rifat-Tipu/emras-backend)
