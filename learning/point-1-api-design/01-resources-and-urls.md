# 1.1 — REST: resources & URLs

---

## Hey Sandeep — how we’ll use this note

Treat this doc as something I’d tell you sitting next to you before your interview — same facts as below, plus **what to practise** and **what they’ll poke at**.

**Do this today (honest checklist):**

1. Read **“Core ideas”** once slowly — not to memorise wording, but so the picture sticks.
2. Open **`UserController`** in Cosmos and narrate **out loud** (seriously): *“This is the collection `/api/users`; this `{id}` is one user; POST creates in the collection; I’m not putting verbs in the path.”* If you stumble, skim the headings again — that’s the signal you need another pass.
3. Spend **five minutes** on **“Interview: questions they actually ask”** — don’t cram; practise **one short answer per question** in your own words.
4. If you have ten extra minutes: answer the **self-check** at the bottom **without scrolling up**. Miss one? Perfect — that’s what to revise.

You’re coming from APIs in general plus .NET; this is mostly **same ideas, Spring’s spelling**. You’re not starting from zero.

---

## What you are designing

In REST over HTTP, an **API exposes resources**. A **resource** is anything with an identity you care about: a user, an account, a transfer, a report. Clients use **URLs** to name resources and **HTTP methods** (later: 1.2) to act on them.

This note is only about **naming and structure** — *what* the paths look like — not full error or pagination theory (those are 1.5 and 1.6).

---

## Interview: questions they actually ask — and how you can answer

Use your own words. Length is a cue: **~20–40 seconds** for “explain”, **one sentence** for “what would you choose?”

| They might say | Your direction (truthful + strong) |
|----------------|--------------------------------------|
| *What’s a resource in REST?* | A **thing we expose with an identity** — e.g. a user, an account — **named by a URL**. The HTTP method describes the operation; the URL names *what*. |
| *Why plural `/users`?* | **`/users` is the collection**; **`/users/{id}` is one member**. Plural reminds you “many things live here”; it matches how most teams and OpenAPI examples read. |
| *Why not `/getUser` or verbs in the path?* | The **verb is already HTTP** (`GET`, `POST`, …). Verbs in the path **duplicate** meaning and confuse **caching**, **monitoring**, and **consistent design**. |
| *Path versus query — when?* | **Primary lookup** of one resource → **path** (`/users/{id}`). **Filters, search, paging** → **query** (`?role=admin&cursor=abc`). Cosmos already uses **path for id** on GET by id. |
| *How deep would you nest URLs?* | **Nest when ownership is clear** (`/customers/{id}/accounts`). **Don’t chase infinite depth** — if it gets clumsy, **flatten** or introduce a clearer aggregate (we’ll deepen in 1.7). |
| *How would you model a money transfer in URLs?* | Often **`POST /transfers`** with a body — treat **transfer as a resource/command**, not **`GET /moveMoney`**. Mention **idempotency** briefly (they’ll love it; detail in 1.2). |

**If they point at your resume or “this codebase”:**

- Cosmos: **`/api/users`** = user collection; **`/api/users/{id}`** = one user — **that's the story**. If they ask why no `/v1` yet: **“We haven’t broken the contract yet; I'd introduce `/api/v1/...` when we need a clear compatibility boundary.”** (We’ll deepen in 1.8.)

---

## What to memorise as a tiny “opening line” (optional)

If your mind goes blank, this one sentence is safe:

**“REST URLs name *things* with nouns — usually plural for collections and an id in the path for one item — and HTTP supplies the verbs.”**

Breathe. Then give one Cosmos or banking example.

---

## Core ideas

### 1. Resource vs collection

- **Collection resource:** many items of the same kind → path is usually **plural**: `/users`, `/accounts`.
- **Item resource:** one identified thing → path includes the **identifier**: `/users/{id}`.

**Cosmos today:** The collection is `GET/POST /api/users`. One user is `GET/DELETE /api/users/{id}`. That matches the usual pattern: plural segment for the collection, `{id}` for the item.

### 2. Nouns in paths, not verbs

Prefer:

- `GET /users/550e8400-e29b-41d4-a716-446655440000`

Avoid:

- `GET /getUser?id=...` or `GET /users/getById/...`

**Why:** The **verb** is already HTTP (`GET`, `POST`, …). Putting verbs in the URL duplicates meaning and makes caching, proxies, and standards harder to reason about.

### 3. Identifiers in the path (not only query string)

For **primary lookup** of a single resource, the stable pattern is **path segment**:

- `/users/{userId}`

Query strings are better for **filters, search, pagination** (see 1.6):

- `GET /users?role=admin&page=2`

**Cosmos:** `UserController` uses `@PathVariable UUID id` on `/{id}` — path-based identity.

### 4. Hierarchy: parent/child resources

When one thing **belongs to** another in your domain, reflect that in the URL:

- `/customers/{customerId}/accounts`
- `/accounts/{accountId}/transactions`

Rules of thumb:

- **Don’t nest forever** — if URLs get 5 levels deep, consider flattening or a separate aggregate root.
- **Child IDs should be unique in context** — often `(parentId, childId)` is clear; sometimes a global `childId` is enough.

**Cosmos (future example):** If “profiles” only exist in the context of a user, you might expose `GET /users/{userId}/profile` instead of a top-level `/profiles/{id}` until you have a reason to split.

### 5. `/api` and versioning

Common conventions:

- **`/api`** — separates machine-facing JSON APIs from human pages or legacy paths.
- **`/api/v1/...`** — embeds **API version** in the path (full tradeoffs in 1.8).

**Cosmos today:** Base path is `/api/users` — there is **no `v1` segment yet**. For interviews, be ready to say: *“We can add `/api/v1/users` when we need a breaking change boundary.”*

### 6. What does *not* belong in the URL

- **Secrets** (passwords, API keys) — never in path or query; use headers/body/TLS.
- **Large payloads** — use request body (`POST`/`PUT`/`PATCH`), not query strings.

---

## Banking-flavored examples (interview language)

| Intent | Reasonable resource-oriented design |
|--------|-------------------------------------|
| List my accounts | `GET /customers/{cid}/accounts` or `GET /accounts?customerId=` (style choice) |
| Initiate transfer | Often `POST /transfers` with body `{ fromAccountId, toAccountId, amount, ... }` — **transfer** is modeled as its own resource (command), not `GET /transferMoney` |

You will deepen **actions vs pure CRUD** in **1.7**.

---

## In Cosmos API (real code)

| Concept | Where |
|---------|--------|
| Collection + item paths | [`UserController.java`](../../src/main/java/com/cosmos/api/controller/UserController.java) — `@RequestMapping("/api/users")`, `GET ""`, `GET/DELETE "/{id}"` |
| Path variable as resource id | Same file — `@PathVariable UUID id` |
| Request body for create | Same file — `@RequestBody UserRegistrationRequest` on `POST` |
| DTOs (representation) | [`UserRegistrationRequest.java`](../../src/main/java/com/cosmos/api/dto/request/UserRegistrationRequest.java), [`UserResponse.java`](../../src/main/java/com/cosmos/api/dto/response/UserResponse.java) |

Read the **comments in `UserController`** — they map lines to the bullets above.

---

## Quick self-check (EOD)

1. Why is `/users` plural?
2. Why avoid `/getUser`?
3. Where would you put **pagination** for listing users — path or query? Why?
4. Name one **nested** URL you might add for Cosmos if “orders” belonged to a user.

---

## Next sub-point

**1.2 — HTTP methods & safety / idempotency** — what `GET` vs `POST` promises, and why retries matter for banking commands.
