# 1.3 — Status codes & headers

---

## Hey Sandeep — how to use this note

Status codes are how the client **knows what happened** without parsing your prose. Headers carry **metadata** (where the new resource lives, caching, auth, tracing).

**Do today:**

1. Skim the **common codes** table once.
2. In Cosmos, open `UserController` and say for each method: **which status** and **which important header** (especially `Location` on create, empty body on delete).
3. Hit the API once (Postman/curl): create a user, copy **`Location`**, GET that URL, GET a random UUID → **404**.
4. Glance at **Interview** — one pass, your own words.

---

## What you are designing

- **2xx** = success semantics (which one tells the client *what kind* of success).
- **4xx** = client should fix something (bad input, auth, not found, conflict).
- **5xx** = server failed; retry *might* help (with backoff), idempotency matters.

Headers often matter as much as the body: **`Location`** after create, **`Content-Type`**, **`Cache-Control`**, **`ETag`** / **`If-Match`** for concurrency (preview here; patterns in production vary).

---

## Interview: what they often ask

| They might say | Your direction |
|----------------|----------------|
| *What do you return after POST create?* | **`201 Created`**, body with representation, and usually **`Location: /api/users/{id}`** so the client can fetch without guessing the URL. |
| *What about DELETE success?* | Often **`204 No Content`** — success, no body. Second delete may be **204** or **404**; both can be argued as idempotent (resource absent). |
| *404 vs 400?* | **400** = bad syntax or validation (wrong shape, missing required field). **404** = we understood the request but **that resource id does not exist** (for GET/DELETE/PATCH/PUT by id). |
| *401 vs 403?* | **401** = not authenticated. **403** = authenticated but **not allowed** for this action. |
| *409?* | **Conflict** — duplicate key, state machine violation, version mismatch. |
| *429?* | **Too Many Requests** — rate limiting; often with **`Retry-After`**. |

---

## Common codes (REST APIs)

| Code | When |
|------|------|
| **200 OK** | GET success, PUT/PATCH success with body |
| **201 Created** | POST created a resource; pair with **`Location`** |
| **204 No Content** | Success, no body (common for DELETE; sometimes PUT) |
| **400 Bad Request** | Validation / malformed input |
| **401 Unauthorized** | Missing/invalid auth |
| **403 Forbidden** | Auth ok, policy denies |
| **404 Not Found** | Resource does not exist (for that identifier) |
| **409 Conflict** | Duplicate, wrong state |
| **412 Precondition Failed** | precondition header failed (e.g. ETag) |
| **422 Unprocessable Entity** | Used by some APIs for semantic validation (opinions vary) |
| **500 Internal Server Error** | Unexpected server failure |

---

## Headers you should name in interviews

| Header | Role |
|--------|------|
| **`Location`** | URI of created resource after **201** |
| **`Content-Type`** | e.g. `application/json` |
| **`Cache-Control`**, **`ETag`** | Caching and conditional GET |
| **`If-Match`** / **`If-None-Match`** | Optimistic concurrency on updates |
| **`Retry-After`** | With **429** or **503** |
| **`Idempotency-Key`** | Request header for idempotent **POST** (JD 1.2) — not a standard RFC for all servers, but industry pattern |

---

## In Cosmos API (real code)

| Topic | Where |
|--------|--------|
| **201 + `Location`** on create | [`UserController#createUser`](../../src/main/java/com/cosmos/api/controller/UserController.java) — `ResponseEntity.created(location).body(created)` |
| **200** on GET, **204** on DELETE | Same file |
| **404** for missing user | [`UserNotFoundException`](../../src/main/java/com/cosmos/api/exception/UserNotFoundException.java) + [`GlobalExceptionHandler`](../../src/main/java/com/cosmos/api/exception/GlobalExceptionHandler.java) |
| **400** field validation | `GlobalExceptionHandler` — `MethodArgumentNotValidException` |
| Learning demos (404 body shape, etc.) | [`HttpMethodSemanticsDemoController`](../../src/main/java/com/cosmos/api/sample/HttpMethodSemanticsDemoController.java) |

---

## Quick self-check

1. Why is **201** better than **200** for “created user”?
2. What header helps the client find the new user without constructing the URL?
3. Missing user by id — **404** or **400**? Why?

---

## Next sub-point

**1.4 — DTOs & validation** — see [04-dtos-and-validation.md](./04-dtos-and-validation.md).
