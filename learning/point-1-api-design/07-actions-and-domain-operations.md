# 1.7 — Actions & domain operations

---

## Hey Sandeep — game plan

Pure CRUD (`POST /users`, `GET /users/{id}`) is not enough for banking. **Money movement** is a **business process**: it needs its own **command resource**, **strong validation**, and **idempotency** (you saw the pattern in JD 1.2).

**Do today:**

1. Read **“Command vs CRUD”** once.
2. Call **`POST /api/transfers`** twice with the **same** `Idempotency-Key` and **same** JSON → second response should match first (no duplicate row intent).
3. Same key, **different** body (e.g. change `amount`) → **409 Conflict** Problem Detail.
4. Same `fromAccountId` and `toAccountId` → **400** invalid transfer.
5. **`GET /api/transfers/{id}`** using the `Location` from the `201` response.

---

## Command vs CRUD

| Style | Example | When |
|--------|---------|------|
| **Resource CRUD** | `POST /users` creates a user row | Entity lifecycle matches HTTP resource cleanly. |
| **Domain command** | `POST /transfers` records a transfer intent | Operation spans balances, limits, fraud checks, ledger entries — not “edit one row by id” from URL alone. |

Interview line: **“We model the transfer as its own resource so the API matches the business transaction boundary, not an arbitrary database table shortcut.”**

---

## Idempotency + conflict (1.2 + 1.7 together)

- **Same `Idempotency-Key` + same payload** → return the **existing** transfer (safe retry).
- **Same key + different payload** → **409 Conflict** — client must not assume the first intent still applies.

Cosmos compares **from**, **to**, **amount**, **currency** (currency normalized to uppercase).

---

## Interview: what they ask

| They might say | Your direction |
|----------------|----------------|
| *Why not `POST /accounts/{id}/debit`?* | Often **couples** too many concerns; **transfer** as a resource keeps **one coherent command**, audit trail, and idempotency scope. (Teams vary; defend tradeoffs.) |
| *How do you avoid double spend on retry?* | **`Idempotency-Key`** persisted with outcome; at-least-once networks + exactly-once **effect** on server. |
| *What status if key reused wrongly?* | **409 Conflict** — distinct from **400** validation. |

---

## In Cosmos API (real code)

| Topic | Path |
|--------|------|
| Command endpoint | [`TransferController`](../../src/main/java/com/cosmos/api/controller/TransferController.java) — `POST /api/transfers`, `GET /api/transfers/{id}` |
| Command + idempotency logic | [`TransferServiceImpl`](../../src/main/java/com/cosmos/api/service/impl/TransferServiceImpl.java) |
| Request / response DTOs | [`TransferCommandRequest`](../../src/main/java/com/cosmos/api/dto/request/TransferCommandRequest.java), [`TransferResponse`](../../src/main/java/com/cosmos/api/dto/response/TransferResponse.java) |
| Persistence | [`Transfer`](../../src/main/java/com/cosmos/api/entity/Transfer.java), [`TransferRepository`](../../src/main/java/com/cosmos/api/repository/TransferRepository.java) |
| **409** / **400** / **404** | [`GlobalExceptionHandler`](../../src/main/java/com/cosmos/api/exception/GlobalExceptionHandler.java), [`IdempotencyConflictException`](../../src/main/java/com/cosmos/api/exception/IdempotencyConflictException.java), … |
| In-memory demo (earlier lesson) | [`HttpMethodSemanticsDemoController`](../../src/main/java/com/cosmos/api/sample/HttpMethodSemanticsDemoController.java) — `/api/learning/http-methods/transfers` |

---

## Example `POST /api/transfers`

```http
POST /api/transfers
Idempotency-Key: 7f2c3b1a-9e8d-4c7b-9a2f-1e3d5b7c9a0f
Content-Type: application/json

{
  "fromAccountId": "11111111-1111-1111-1111-111111111111",
  "toAccountId": "22222222-2222-2222-2222-222222222222",
  "amount": "100.00",
  "currency": "USD"
}
```

---

## Quick self-check

1. Name one reason **`POST /transfers`** beats hiding the operation inside a generic **`POST /accounts`** sub-path.
2. Why **409** for “same idempotency key, different body”?
3. What would you add next in production before going live? (ledger, authz, limits, fraud, outbox events — pick two.)

---

## Next sub-point

**1.8 — Versioning & compatibility** — URI vs header vs media type; additive vs breaking changes.
