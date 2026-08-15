# E-commerce Monolith Java

Spring Boot e-commerce backend focused on clean layered APIs, JPA query optimization, secure JWT authentication, multi-warehouse checkout, and reliable order fulfilment flows.

## Stack

- Java 21, Spring Boot 3.5, Spring Web MVC, Spring Data JPA
- MySQL 8 with Flyway migrations
- Spring Security OAuth2 Resource Server with HS256 JWTs
- Caffeine cache for product detail reads
- Springdoc OpenAPI UI at `http://localhost:8080/swagger-ui.html`

## Core Modules

- Auth and user profile/address management.
- Category, product, product variant, cart, and checkout.
- Warehouse, inventory, order, payment, shipment, and tracking.
- Notification jobs and return requests.

## Key Features

- Full e-commerce monolith: auth, user address, product variant, cart, checkout, order, payment, inventory, warehouse, shipment, tracking, notification, and return APIs.
- JWT security with OAuth2 Resource Server, access/refresh token flow, refresh-token rotation, and role-based access for customer, admin, warehouse staff, and shipper.
- Spring Data JPA query optimization with JPQL, Specification, EntityManager Criteria, keyset pagination, projection reads, and `join fetch` to reduce N+1 queries.
- Caffeine product-detail cache with cache eviction on product, variant, and inventory writes.
- Multi-warehouse checkout using Haversine nearest-warehouse allocation, split shipment fallback, and JPA `PESSIMISTIC_WRITE` pessimistic lock to prevent overselling.
- Clean layered code with request/response DTOs, response builders, global/business exceptions, Flyway schema migrations, UUID ids, and five audit fields.

## Run Locally

```bash
docker compose up -d mysql
./gradlew bootRun
```

Default local configuration:

```text
ECOM_DB_URL=jdbc:mysql://localhost:3307/ecom_monolith_java
ECOM_DB_USERNAME=ecom
ECOM_DB_PASSWORD=ecom
JWT_SECRET=change-me-change-me-change-me-32bytes
```

Enable real email delivery only with runtime variables:

```bash
ECOM_EMAIL_ENABLED=true \
ECOM_MAIL_USERNAME=your-gmail@example.com \
ECOM_MAIL_PASSWORD=your-gmail-app-password \
./gradlew bootRun
```

Run checks:

```bash
./gradlew check
```
