# FinCoreX

FinCoreX is a Spring Boot financial trading backend with JWT authentication,
wallet operations, asset trading, portfolio calculations, and audit history.

## Stack

- Java 21 and Spring Boot 3
- PostgreSQL 16 and Flyway
- Spring Security with JWT
- Spring Data JPA
- OpenAPI / Swagger UI
- Docker Compose

## Run Locally

1. Copy `.env.example` to `.env` and replace the passwords and JWT secret.
2. Start the application:

```bash
docker compose up --build -d
```

3. Check the running services:

```bash
curl http://localhost:8080/health
```

The application is available at `http://localhost:8080`.

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- PostgreSQL: `localhost:5432`
- pgAdmin: `http://localhost:5050`

The Compose file provides demo fallback values for local development. Set
`JWT_SECRET` explicitly for any shared or deployed environment. It must be a
Base64-encoded secret that decodes to at least 32 bytes.

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

## Tests

Run the test suite with Maven:

```bash
./mvnw test
```

The security test suite covers JWT authentication, role restrictions, wallet
ownership, JSON security errors, and CORS preflight handling.
