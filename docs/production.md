# Production Guide

## Required Configuration

Production must provide these values through a secret manager or protected
environment configuration:

```text
POSTGRES_USER
POSTGRES_PASSWORD
POSTGRES_DB
JWT_SECRET
JWT_EXPIRATION_MS
CORS_ALLOWED_ORIGINS
OPENAPI_ENABLED=false
MARKET_PRICE_UPDATE_MS
```

`JWT_SECRET` must be Base64-encoded and decode to at least 32 bytes. Do not
commit `.env` files or production credentials.

## Deployment Checklist

- Use a managed or protected PostgreSQL instance.
- Restrict PostgreSQL and pgAdmin access to private networks.
- Expose the application through TLS and a reverse proxy.
- Set `OPENAPI_ENABLED=false` unless protected access is required.
- Configure only trusted CORS origins.
- Provide database backups and test restoration regularly.
- Apply Flyway migrations during a controlled deployment step.
- Keep the application container non-root.
- Configure container restart and readiness checks.
- Collect application logs and Actuator health metrics.
- Rotate JWT and database credentials according to operational policy.

## Docker Compose

For local development:

```bash
cp .env.example .env
# Replace all change-me and placeholder values in .env.
docker compose up --build -d
```

For optional pgAdmin tooling:

```bash
docker compose --profile tools up --build -d
```

The application listens on `8080`. PostgreSQL is intentionally available only
inside the Compose network. pgAdmin is a development tool and should not be
enabled in a production deployment.

## Migrations

Flyway runs migrations automatically on application startup. The application
uses `ddl-auto: validate`, so Hibernate will not modify the database schema.

Before a production migration:

1. Create and verify a database backup.
2. Test the migration against a database snapshot.
3. Deploy the application with the migration.
4. Check `/actuator/health` and application logs.
5. Keep the previous application image available for rollback.

The financial constraints introduced in V13 are marked `NOT VALID` for existing
legacy rows so old data is not silently rewritten. New writes are protected by
the constraints; existing data should be audited and cleaned before validating
those constraints in a controlled maintenance window.

## CI/CD

`.github/workflows/ci.yml` runs on pushes to `main` and pull requests. It:

- Runs Maven verification with isolated Testcontainers PostgreSQL.
- Builds the Docker image.
- Scans the image for high and critical vulnerabilities with Trivy.
- Reviews dependency changes on pull requests.

Deployment should only proceed after the workflow succeeds.

## Observability

- Liveness and readiness probes are available through Actuator.
- PostgreSQL health is included in readiness checks.
- Business and security errors use structured API responses.
- Trade execution and scheduler activity use application logging.

For a complete production setup, connect these signals to the hosting
platform's log aggregation, metrics, and alerting services.
