# 1.5 — Errors as data (Problem Details)

---

## Hey Sandeep — game plan

Clients (and OpenAPI generators) do better when **errors look like errors**, not random JSON shapes.

**Do today:**

1. Read **“What is Problem Detail?”** below — one pass.
2. Trigger a **400**: `POST /api/v1/users` with invalid email → read JSON keys **`type`**, **`title`**, **`status`**, **`detail`**, **`instance`**, **`fieldErrors`**.
3. Trigger a **404**: `GET /api/v1/users/{random-uuid}` → same shape, no `fieldErrors`.
4. Say out loud: *“We use stable `type` URIs so machines can branch; humans read `title` and `detail`.”*

---

## What is Problem Detail? (RFC 7807)

A small **standard JSON shape** for errors:

| Member | Role |
|--------|------|
| **`type`** | URI identifying the problem category (often links to docs) |
| **`title`** | Short, stable summary for humans |
| **`status`** | HTTP status code (echo) |
| **`detail`** | Specific explanation for this occurrence |
| **`instance`** | URI of the request that failed (helps support/debug) |

You may add **extension** members (e.g. **`fieldErrors`**) for validation.

**Content-Type:** often **`application/problem+json`** (Spring sets this when `spring.mvc.problemdetails.enabled=true`).

---

## Interview: what they ask

| They might say | Your direction |
|----------------|----------------|
| *How do you structure API errors?* | **Problem Details (RFC 7807)** or same fields manually — stable **`type`**, human **`title`/`detail`**, optional **`fieldErrors`**. |
| *Why not `{ "error": "..." }` only?* | Works for one case; breaks at scale — clients can’t reliably parse; i18n and monitoring want **typed** errors. |
| *Where do you map exceptions to HTTP?* | **`@RestControllerAdvice`** + **`@ExceptionHandler`** — one place, consistent responses. |
| *Validation errors?* | **400** + Problem Detail + **`fieldErrors`** map (or `violations` array in some APIs). |

---

## In Cosmos API (real code)

| Topic | Path |
|--------|------|
| Problem Detail handlers | [`GlobalExceptionHandler.java`](../../src/main/java/com/cosmos/api/exception/GlobalExceptionHandler.java) |
| Stable `type` URIs | [`ProblemTypeUri.java`](../../src/main/java/com/cosmos/api/exception/ProblemTypeUri.java) |
| Enable `problem+json` | [`application.properties`](../../src/main/resources/application.properties) — `spring.mvc.problemdetails.enabled=true` |

---

## Example bodies (shape)

**404 — user not found**

```json
{
  "type": "https://api.cosmos.local/problems/user-not-found",
  "title": "User not found",
  "status": 404,
  "detail": "User not found with id: …",
  "instance": "/api/v1/users/…"
}
```

**400 — validation**

```json
{
  "type": "https://api.cosmos.local/problems/validation-failed",
  "title": "Validation failed",
  "status": 400,
  "detail": "One or more fields failed validation.",
  "instance": "/api/v1/users",
  "fieldErrors": {
    "email": "Invalid email format"
  }
}
```

---

## Quick self-check

1. What is **`type`** for — humans or machines?
2. Why include **`instance`**?
3. Where would you add **`traceId`** for support (extension field)?

---

## Next sub-point

**1.6 — List / search / pagination** — see [06-list-search-pagination.md](./06-list-search-pagination.md).
