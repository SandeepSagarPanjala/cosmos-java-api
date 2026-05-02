# 1.2 — HTTP methods, safety, idempotency

---

## Hey Sandeep — game plan for this topic

This one is interview gold. Most engineers know CRUD words; fewer can explain method semantics clearly under pressure.

**Do this sequence today:**

1. Learn the table in **Core ideas #1** (safe vs idempotent) until it feels natural.
2. Open `UserController` and map each endpoint to the table.
3. Open `HttpMethodSemanticsDemoController` and read comments on `POST`, `PUT`, `PATCH`, `DELETE`.
4. Practise one 45-second answer: **“Why idempotency matters in banking transfers.”**

---

## What this sub-point means in plain words

The URL tells us **what resource** we target.  
The HTTP method tells us **what kind of operation** we intend.

That method is not just syntax. It carries behavior contracts:

- Can this call be retried safely?
- Can caches/proxies reason about it?
- Should side effects happen every time or only once?

---

## Interview: questions they ask here

| They ask | Strong answer direction |
|----------|--------------------------|
| Difference between safe and idempotent? | **Safe** means no state change (`GET`, `HEAD`, `OPTIONS`). **Idempotent** means repeating same request has same final state (`PUT`, `DELETE`, many `PATCH` designs). |
| Is POST idempotent? | Usually **no**. It often creates a new resource each time. But you can make POST effectively idempotent with an **Idempotency-Key** pattern. |
| Why idempotency in payments? | Network retries happen. Without idempotency, retry can double-charge. With a key, server returns same outcome for duplicate retries. |
| PUT vs PATCH? | **PUT** replaces whole representation; omitted fields are typically reset/removed. **PATCH** applies partial changes only. |
| Why use proper methods instead of one POST endpoint? | Clear semantics improve clients, observability, caching expectations, and governance consistency. |

---

## Core ideas

### 1) Method semantics cheat sheet

| Method | Safe? | Idempotent? | Typical use |
|--------|-------|-------------|-------------|
| GET | Yes | Yes | Read resource(s) |
| POST | No | Usually no | Create/command |
| PUT | No | Yes | Full replace |
| PATCH | No | Usually designed to be yes | Partial update |
| DELETE | No | Yes | Remove resource |

**Key nuance:** `DELETE` is idempotent even if second call returns `404` because final state remains “resource absent.”

### 2) Cosmos mapping today

- `GET /api/users` and `GET /api/users/{id}` -> safe/idempotent reads.
- `POST /api/users` -> create, usually non-idempotent.
- `DELETE /api/users/{id}` -> idempotent delete semantics.

### 3) Retry behavior (real-world)

- Client times out after sending request.
- It retries because uncertain if server processed it.
- For non-idempotent calls (like transfer create), retry can duplicate effects unless you design a protection mechanism.

### 4) Idempotency-Key pattern

Client sends unique key for operation intent:

- Header: `Idempotency-Key: 8ad8...`
- Server stores key + result (status/body or resource id).
- Duplicate same key -> return stored outcome, do not run side effect again.

---

## Real code in Cosmos for this topic

| Code | Why it matters |
|------|----------------|
| `src/main/java/com/cosmos/api/controller/UserController.java` | Existing real endpoints with `GET`, `POST`, `DELETE` semantics. |
| `src/main/java/com/cosmos/api/sample/HttpMethodSemanticsDemoController.java` | Learning controller with explicit examples for `PUT`, `PATCH`, and idempotency key behavior. |

---

## Practice prompts (say these out loud)

1. “POST is generally not idempotent, but we make transfer creation idempotent via idempotency key.”
2. “PUT replaces full representation; PATCH modifies partial fields.”
3. “GET is safe and idempotent; DELETE is not safe but idempotent.”

---

## Next sub-point

**1.3 — Status codes & headers** (201 + Location, 204, 409, 412, ETag basics).
