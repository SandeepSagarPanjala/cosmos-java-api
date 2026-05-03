# 1.6 — List, search, pagination

---

## Hey Sandeep — game plan

Collection **`GET`** endpoints are where teams accidentally ship **unbounded** queries (“return all users forever”). Pagination and filters belong in **query parameters** (you already learned that in 1.1).

**Do today:**

1. Call `GET /api/v1/users` with no params — note **`content`**, **`page`**, **`totalElements`**, **`totalPages`**.
2. Try `?page=0&size=5&sortBy=email&sortDir=asc` and `?email=gmail` (substring search).
3. Send `?size=500` — watch **`size`** cap at **100** in the response (server-side guard).
4. Send `?page=-1` — should get **400** Problem Detail (`violations`) from **`@Min(0)`** on `page`.

---

## Core ideas

### 1) Pagination styles

| Style | Idea | When it shows up |
|--------|------|------------------|
| **Offset / page** | `page` + `size` (or `limit` + `offset`) | Simple admin UIs; can be slow on huge tables (“deep pages”). |
| **Cursor / keyset** | opaque `cursor` + `limit` | Large feeds; stable ordering while data changes. |

Cosmos uses **page + size** via Spring Data **`Pageable`**.

### 2) Search / filter on collections

Use **query params**: `GET /api/v1/users?email=gmail` — not a new path like `/searchUsers`.

Cosmos implements **substring** match on **email** (case-insensitive). In banks you’d document PII rules (logging, minimum query length).

### 3) Sorting

Never pass raw SQL `ORDER BY` from the client. **Whitelist** allowed fields (`createdAt`, `email`, `username` in Cosmos) and validate direction **`asc` / `desc`**.

### 4) Response shape

Returning a bare `[]` hides totals. Cosmos returns **`PagedUsersResponse`**: `content` + metadata (`totalElements`, `totalPages`, `first`, `last`). Alternative: **`Link`** headers (RFC 5988) for `next`/`prev` — good interview mention.

---

## Interview: what they ask

| They might say | Your direction |
|----------------|----------------|
| *Cursor vs offset?* | Offset is simpler; cursor avoids deep-offset cost and can be more stable for live feeds. |
| *How do you prevent huge pages?* | **Cap `size`**, default sensible page size, possibly rate limit. |
| *How do you document list APIs?* | OpenAPI query params + example responses + error cases (400 bad range). |

---

## In Cosmos API (real code)

| Topic | Path |
|--------|------|
| List endpoint + query params | [`UserController#listUsers`](../../src/main/java/com/cosmos/api/controller/UserController.java) |
| Page + search + sort whitelist | [`UserServiceImpl#searchUsers`](../../src/main/java/com/cosmos/api/service/impl/UserServiceImpl.java) |
| Email substring query | [`UserRepository`](../../src/main/java/com/cosmos/api/repository/UserRepository.java) — `findByEmailContainingIgnoreCase` |
| JSON wrapper | [`PagedUsersResponse`](../../src/main/java/com/cosmos/api/dto/response/PagedUsersResponse.java) |
| Bad query params → 400 | [`GlobalExceptionHandler`](../../src/main/java/com/cosmos/api/exception/GlobalExceptionHandler.java) — `ConstraintViolationException` |

---

## Example requests

```http
GET /api/v1/users
GET /api/v1/users?page=0&size=10&sortBy=createdAt&sortDir=desc
GET /api/v1/users?email=corp
```

---

## Quick self-check

1. Why not `GET /api/v1/users/getPage/1`?
2. Why whitelist `sortBy`?
3. When would you switch from offset to **cursor** pagination?

---

## Next sub-point

**1.7 — Actions & domain operations** — see [07-actions-and-domain-operations.md](./07-actions-and-domain-operations.md).
