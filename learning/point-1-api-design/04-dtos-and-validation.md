# 1.4 — DTOs & validation

---

## Hey Sandeep — game plan

**DTO** (Data Transfer Object) here means: **types that define what goes over HTTP** — request bodies and response JSON — **separate from** your database entity.

**Do today:**

1. Read **“Why not expose `User` entity?”** once — interviewers love this.
2. Open `UserRegistrationRequest`, `UserPatchRequest`, `UserResponse`, `User` — say which is **in**, **out**, **DB**.
3. Break `POST /api/users` on purpose (bad email, short password) and watch **400** + field errors from `GlobalExceptionHandler`.
4. Call **`PATCH /api/users/{id}`** with `{"displayName":"..."}` and with a **too-long** string to see validation fire.

---

## What you are designing

- **Request DTOs** — shape and rules for **incoming** JSON (`UserRegistrationRequest`, `UserPatchRequest`).
- **Response DTOs** — what clients **see** (`UserResponse`); omit secrets and internal columns.
- **Validation** — rules on request DTOs; **`@Valid`** on the controller parameter triggers Bean Validation **before** your service runs.

---

## Interview: what they ask

| They might say | Your direction |
|----------------|----------------|
| *Why use DTOs instead of returning JPA entities?* | **Decouple** API contract from schema; avoid leaking fields, lazy-load surprises, and circular JSON; **version** the API without rewriting tables. |
| *What does `@Valid` do?* | Runs **Jakarta Bean Validation** on the object; failures become **`MethodArgumentNotValidException`** → typically **400** with per-field messages. |
| *Where do you put validation — controller or service?* | **Declarative rules on DTOs** for input shape; **business rules** (e.g. “email already registered”) in **service** returning **409** or domain errors — both are valid split. |
| *POST body vs PATCH body?* | Often **different DTOs**: create needs password + required fields; patch has **optional** fields only (see `UserPatchRequest`). |

---

## Core ideas

### 1) Three layers in Cosmos

| Layer | Type | Role |
|--------|------|------|
| Wire **in** | `UserRegistrationRequest`, `UserPatchRequest` | JSON clients send; validation annotations |
| Domain / DB | `User` | Hibernate mapping; not returned from controllers |
| Wire **out** | `UserResponse` | JSON clients read; no password hash |

### 2) Bean Validation (what you already use)

Annotations like `@NotBlank`, `@Email`, `@Size` on fields. Controller uses **`@Valid`**.

### 3) Optional fields on PATCH

`UserPatchRequest` only has fields that may be updated. **`null`** means “leave existing value” in `UserServiceImpl.patchUser`.

---

## In Cosmos API (real code)

| Topic | Path |
|--------|------|
| Registration request + validation | [`UserRegistrationRequest.java`](../../src/main/java/com/cosmos/api/dto/request/UserRegistrationRequest.java) |
| Partial update request | [`UserPatchRequest.java`](../../src/main/java/com/cosmos/api/dto/request/UserPatchRequest.java) |
| Response projection | [`UserResponse.java`](../../src/main/java/com/cosmos/api/dto/response/UserResponse.java) |
| Entity (not API) | [`User.java`](../../src/main/java/com/cosmos/api/entity/User.java) |
| `@Valid` on create + patch | [`UserController.java`](../../src/main/java/com/cosmos/api/controller/UserController.java) |
| 400 mapping for validation | [`GlobalExceptionHandler.java`](../../src/main/java/com/cosmos/api/exception/GlobalExceptionHandler.java) |

---

## Quick self-check

1. Why must **`passwordHash`** stay off `UserResponse`?
2. Why is **`UserPatchRequest`** smaller than **`UserRegistrationRequest`**?
3. What happens if you remove **`@Valid`** from `createUser`?

---

## Next sub-point

**1.5 — Errors as data (Problem Details)** — consistent error JSON (`type`, `title`, `status`, …).
