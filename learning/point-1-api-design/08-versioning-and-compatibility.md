# 1.8 — Versioning & compatibility (design)

---

## Hey Sandeep — game plan

Versioning is how you ship **breaking changes** without silently breaking every mobile app and partner integration.

**Do today:**

1. Read **“Three common styles”** once — you only need to defend one choice clearly in interview.
2. In Cosmos, notice **`ApiPaths`** and **`/api/v1/users`**, **`/api/v1/transfers`** — version is in the **path**.
3. Ask yourself: *“If I add a required JSON field, is that breaking?”* (Usually **yes** for strict clients.)

---

## Why version at all?

- **Additive change** (new optional field, new endpoint) → often **same** version.
- **Breaking change** (remove field, change type, change semantics) → usually **new** version (`v2`) or negotiated contract.

Without a version boundary, every client is coupled to **one** evolving contract.

---

## Three common styles

| Style | Example | Pros / cons |
|--------|---------|-------------|
| **URI path** | `/api/v1/users` | Obvious in logs and curl; easy CDN routing; **duplicate** controllers or routing rules per version. |
| **Header** | `Api-Version: 1` | Clean URLs; harder to see in access logs; clients must remember header. |
| **Content type** | `Accept: application/vnd.cosmos.v1+json` | Very explicit; more ceremony for simple clients. |

Cosmos uses **URI path** + constants in **`ApiPaths`** so the prefix is not copy-pasted.

---

## Compatibility rules (how teams talk)

- **Additive:** new **optional** response field; new query param with default — usually **OK** in same major version if documented.
- **Breaking:** remove/rename field; change `404` → `200`; stricter validation — needs **new version** or very careful rollout.
- **Deprecation:** HTTP **`Deprecation`** (RFC 9745) + **`Sunset`** (RFC 8594) + docs + timeline; keep old path working for a window.

---

## Interview: what they ask

| They might say | Your direction |
|----------------|----------------|
| *URI vs header versioning?* | **Tradeoffs:** path is visible and cache-friendly; header keeps URLs stable. Pick one per org style guide. |
| *How do you avoid breaking mobile apps?* | **Contract tests**, **OpenAPI diff**, **deprecation period**, optional **parallel** `/v1` + `/v2` during migration. |
| *Where is version in Cosmos?* | **`/api/v1/...`** via `ApiPaths.V1_USERS` / `V1_TRANSFERS`. |

---

## In Cosmos API (real code)

| Topic | Path |
|--------|------|
| Version constants | [`ApiPaths.java`](../../src/main/java/com/cosmos/api/web/ApiPaths.java) |
| Users API | [`UserController`](../../src/main/java/com/cosmos/api/controller/UserController.java) — `@RequestMapping(ApiPaths.V1_USERS)` |
| Transfers API | [`TransferController`](../../src/main/java/com/cosmos/api/controller/TransferController.java) — `@RequestMapping(ApiPaths.V1_TRANSFERS)` |

---

## Quick self-check

1. Is adding a **required** request field usually breaking? Why?
2. Name one reason banks like **explicit** versioning.
3. What would you put in **`Sunset`** header meaning?

---

## Next sub-point

**1.9 — gRPC** — see [09-grpc-when-and-how.md](./09-grpc-when-and-how.md).
