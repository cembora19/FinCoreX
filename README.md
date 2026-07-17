# FinCoreX

FinCoreX is a Spring Boot financial trading backend with JWT authentication,
wallet operations, asset trading, portfolio calculations, and audit history.

It is designed as a focused backend project that demonstrates secure financial
domain workflows, transactional consistency, database migrations, and
containerized delivery.

[Architecture](docs/architecture.md) | [API Examples](docs/api-examples.md) |
[Production Guide](docs/production.md)

## Features

- User registration and login with JWT bearer authentication
- BCrypt password hashing and USER/ADMIN authorization
- One wallet per registered user
- Deposits and withdrawals with balance validation
- BUY and SELL trades with average buy price tracking
- Portfolio valuation and realized/unrealized profit and loss
- Transaction history and wallet audit logs
- Simulated market price updates through a configurable scheduler
- Wallet ownership checks to prevent IDOR access
- PostgreSQL schema management with Flyway migrations
- JSON security and validation error responses
- Docker Compose development stack and GitHub Actions CI

## Stack

- Java 21 and Spring Boot 3
- PostgreSQL 16 and Flyway
- Spring Security with JWT
- Spring Data JPA
- OpenAPI / Swagger UI
- Docker Compose

## Architecture

The application follows a layered Spring architecture:

```text
HTTP / Swagger
      |
Controllers + validation
      |
Services + authorization + transactions
      |
Spring Data repositories
      |
PostgreSQL + Flyway
```

The domain is organized around `User`, `Wallet`, `Asset`, `WalletAsset`,
`Transaction`, and `AuditLog`. Financial mutations are transactional and use
database locking for wallet and asset records so concurrent operations cannot
reuse the same balance or asset quantity.

## Run Locally

1. Copy `.env.example` to `.env` and replace the passwords and JWT secret.
2. Start the application:

```bash
docker compose up --build -d
```

To also start the optional pgAdmin tools container:

```bash
docker compose --profile tools up --build -d
```

3. Check the running services:

```bash
curl http://localhost:8080/health
```

The application is available at `http://localhost:8080`.

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Actuator health: `http://localhost:8080/actuator/health`
- PostgreSQL: available to the Compose network only
- pgAdmin: `http://localhost:5050`

The Compose file requires `.env` values and does not provide default database
or JWT credentials. Set `JWT_SECRET` explicitly; it must be a Base64-encoded
secret that decodes to at least 32 bytes.

## Authentication Flow

Register and log in to receive a JWT:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"name":"Alice","email":"alice@example.com","password":"password123"}'

curl -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"alice@example.com","password":"password123"}'
```

Use the returned token as `Authorization: Bearer <token>` for protected
endpoints. The user wallet is created during registration.

For complete request and response examples, see
[docs/api-examples.md](docs/api-examples.md).

## API Overview

| Area | Endpoint | Access |
| --- | --- | --- |
| Health | `GET /health` | Public |
| Authentication | `POST /api/auth/register` | Public |
| Authentication | `POST /api/auth/login` | Public |
| Assets | `GET /api/assets` | Authenticated |
| Wallet | `POST /api/wallets/deposit` | Wallet owner |
| Wallet | `POST /api/wallets/withdraw` | Wallet owner |
| Wallet | `GET /api/wallets/{walletId}/portfolio` | Wallet owner |
| Wallet | `GET /api/wallets/{walletId}/transactions` | Wallet owner |
| Wallet | `GET /api/wallets/{walletId}/audit-logs` | Wallet owner |
| Trading | `POST /api/trades` | Wallet owner |
| Users | `/api/users/**` | Admin only |

## Configuration

Supported environment variables are documented in `.env.example`:

- `POSTGRES_USER`, `POSTGRES_PASSWORD`, `POSTGRES_DB`
- `PGADMIN_DEFAULT_EMAIL`, `PGADMIN_DEFAULT_PASSWORD`
- `JWT_SECRET`, `JWT_EXPIRATION_MS`
- `CORS_ALLOWED_ORIGINS` (comma-separated origins, default `http://localhost:3000`)
- `OPENAPI_ENABLED`, `MARKET_PRICE_UPDATE_MS`

## Tests

Run the test suite with Maven:

```bash
./mvnw test
```

The security test suite covers JWT authentication, role restrictions, wallet
ownership, JSON security errors, and CORS preflight handling.

Tests use Testcontainers and require Docker. They run Flyway migrations against
an isolated PostgreSQL container instead of a developer database.

## Project Structure

```text
src/main/java/com/fincorex/
  config/       Security and OpenAPI configuration
  controller/   HTTP endpoints
  dto/          Request and response contracts
  entity/       JPA domain entities
  exception/    Business and API error handling
  listener/     User and trade domain event handlers
  repository/   Spring Data persistence interfaces
  scheduler/    Simulated market price updates
  security/     JWT service and request filter
  service/      Domain services and implementations
src/main/resources/
  db/migration/ Flyway schema migrations
docs/            Architecture, API, and production guides
```

## Delivery Status

The repository contains a working, tested backend release. The CI workflow
runs Maven verification, builds the Docker image, and scans the image for high
and critical vulnerabilities. Production deployments should still provide
their own secrets, database backups, monitoring, and reverse proxy.

## Scope and Roadmap

The current release intentionally uses simulated market prices. A real market
data provider can be added behind the scheduler without changing the wallet
and portfolio contracts.

Potential future extensions include refresh tokens, password reset, external
market data, pagination for large history datasets, and distributed rate
limiting.

## Operational Notes

- Market prices are simulated by a configurable scheduler for this release.
- Financial wallet and asset updates use database locking to prevent concurrent
  overspending and double-selling.
- Production deployments should set `OPENAPI_ENABLED=false` and expose only the
  application port through the reverse proxy.
- Back up the PostgreSQL volume before applying production migrations.
