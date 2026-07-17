# API Examples

All successful responses use this envelope:

```json
{
  "success": true,
  "data": {},
  "message": "success",
  "timestamp": "2026-07-17T12:00:00"
}
```

Errors use the same envelope with `success: false` and `data: null`.

## Register and Login

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"name":"Alice","email":"alice@example.com","password":"password123"}'
```

```json
{
  "success": true,
  "data": {
    "token": "<jwt>",
    "email": "alice@example.com",
    "role": "USER"
  },
  "message": "Registration successful",
  "timestamp": "2026-07-17T12:00:00"
}
```

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"alice@example.com","password":"password123"}'
```

Store the returned token in a shell variable for subsequent requests:

```bash
TOKEN='<jwt>'
```

## Assets

```bash
curl http://localhost:8080/api/assets \
  -H "Authorization: Bearer $TOKEN"
```

The response contains seeded assets such as `BTC`, `ETH`, and `AAPL`, including
their current simulated prices.

## Deposit and Withdraw

The `userId` is the registered user ID. It can be obtained through the
admin-only user management endpoint or an integration-specific user profile
flow. A dedicated self-service profile endpoint is a future extension.

```bash
curl -X POST http://localhost:8080/api/wallets/deposit \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"userId":"<user-uuid>","amount":1000.00}'
```

```bash
curl -X POST http://localhost:8080/api/wallets/withdraw \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"userId":"<user-uuid>","amount":100.00}'
```

Amounts must be positive and support up to two decimal places.

## Execute a Trade

```bash
curl -X POST http://localhost:8080/api/trades \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "walletId":"<wallet-uuid>",
    "assetSymbol":"BTC",
    "quantity":0.2500,
    "type":"BUY"
  }'
```

Supported trade types are `BUY` and `SELL`. Quantities support up to four
decimal places. The response includes the execution price, total trade amount,
and the wallet balance after execution.

## Portfolio and History

```bash
curl http://localhost:8080/api/wallets/<wallet-uuid>/portfolio \
  -H "Authorization: Bearer $TOKEN"

curl http://localhost:8080/api/wallets/<wallet-uuid>/transactions \
  -H "Authorization: Bearer $TOKEN"

curl http://localhost:8080/api/wallets/<wallet-uuid>/audit-logs \
  -H "Authorization: Bearer $TOKEN"
```

These endpoints are restricted to the wallet owner. Portfolio responses include
cash balance, asset value, total value, cost basis, realized P/L, unrealized P/L,
and per-asset positions.

## Admin User Management

`/api/users/**` requires an authenticated user with the `ADMIN` role.

```bash
curl http://localhost:8080/api/users \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

## Health and OpenAPI

```bash
curl http://localhost:8080/health
curl http://localhost:8080/actuator/health
```

When `OPENAPI_ENABLED=true`, interactive documentation is available at
`http://localhost:8080/swagger-ui/index.html` and the OpenAPI document is
available at `/v3/api-docs`.

## Common Status Codes

| Status | Meaning |
| --- | --- |
| `200` | Successful request |
| `400` | Validation or malformed request |
| `401` | Missing, invalid, or expired authentication |
| `403` | Authenticated user lacks permission or ownership |
| `404` | Requested resource does not exist |
| `409` | Duplicate or conflicting data |
| `500` | Unexpected server error |
