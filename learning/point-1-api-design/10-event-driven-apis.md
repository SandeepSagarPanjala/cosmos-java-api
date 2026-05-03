# 1.10 — Event-driven APIs (topics, consumers, reliability)

---

## Hey Sandeep — game plan

**Event-driven** means services react to **facts that already happened** (messages on a log or broker), not only to **synchronous requests** (REST/gRPC). In banking you see this for **notifications**, **analytics**, **downstream ledgers**, **anti-fraud**, and **decoupling** teams — but it adds **delivery semantics**, **ordering**, and **duplicate handling** you must design for.

**Do today:**

1. Read **“At-least-once and why duplicates exist”** — this is the bar for interviews.
2. Skim the **sample event JSON** [`examples/transfer_completed_event_v1.json`](./examples/transfer_completed_event_v1.json) and name three fields you’d want on **every** domain event for traceability.
3. Tie mentally to Cosmos **`Idempotency-Key`** on `POST /api/v1/transfers` (JD 1.7): **same idea** (stable client intent), different layer (HTTP vs message consumer).

---

## Core vocabulary

| Term | What to say |
|------|-------------|
| **Producer** | Publishes events after something happened (or will happen — be careful with wording). |
| **Consumer** | Subscribes to a **topic** (or queue), processes messages, may **ack** after work. |
| **Topic / stream** | Named channel of events; often **partitioned** for scale. |
| **Partition key** | Controls which **partition** a message lands in → **per-key ordering** only, not global ordering. |
| **Schema** | Avro / Protobuf / JSON Schema attached to the topic — **evolution rules** (compatible changes). |

Cosmos does **not** run a broker in this repo; the JSON file is a **contract sketch** for how you might broadcast “transfer completed” after the HTTP command succeeds.

---

## Delivery semantics (they always ask)

| Guarantee | Meaning | Typical broker config |
|-----------|---------|-------------------------|
| **At-most-once** | May **lose** messages under failure | Rare for money-adjacent processing |
| **At-least-once** | May **deliver again** — consumer must be **idempotent** | Very common (Kafka consumers, many queues) |
| **Exactly-once** (end-to-end) | Marketing-grade phrase — in practice you aim for **exactly-once *effect*** via **idempotent writes** + **dedupe** + **transactions** where possible | Complex; often “effectively once” |

Interview line: **“We assume at-least-once delivery; the consumer stores a processed `event_id` (or business key) so replays don’t double-post.”**

---

## Transactional outbox (pattern name-drop)

**Problem:** You commit a DB row **and** want to emit an event. If you emit first then DB fails → ghost event. If DB commits then emit fails → missing event.

**Outbox:** In the **same database transaction** as the business write, insert a row into an **`outbox`** table. A separate **publisher** process reads outbox, publishes to Kafka (etc.), marks rows **sent**. **Same transaction** = no committed transfer without a durable outbox row (or equivalent).

You don’t need to implement it in Cosmos for the interview — you need to **describe** it cleanly.

---

## Consumer idempotency (minimal design)

1. **Natural idempotency** — “set balance to X” is safer than “add 10” if duplicates replay wrong. Prefer **state assertions** where domain allows.
2. **Processed-event log** — table `(consumer_group, event_id)` unique; insert before side effects; duplicate insert → skip work.
3. **Business key** — e.g. “apply fee once per `transfer_id`” with unique constraint.

Link to REST: **`Idempotency-Key`** on transfers is the **command** side of the same lesson.

---

## Event shape (envelope vs payload)

Good practice: **envelope** metadata is stable across events:

- **`event_id`** — unique per emission (UUID).
- **`event_type`** — string for routing / metrics (`cosmos.transfer.completed`).
- **`occurred_at`** — clock for ordering **awareness** (not a substitute for broker ordering guarantees).
- **`schema_version`** — so consumers know how to parse **`data`**.

See [`examples/transfer_completed_event_v1.json`](./examples/transfer_completed_event_v1.json).

---

## Failure paths you should mention

| Concern | Mitigation |
|---------|------------|
| **Poison message** | Retry with backoff; after N failures → **DLQ** + alert; fix and **replay** from DLQ. |
| **Slow consumer** | Scale consumers **per partition** cap; tune batch size; **backpressure**. |
| **Ordering bugs** | Don’t assume global order; use **partition key** = entity id when per-entity order matters. |
| **Schema drift** | Registry + **backward compatible** changes; version in envelope. |

---

## Interview: what they ask

| They might say | Your direction |
|----------------|----------------|
| *REST vs events for the same use case?* | **Sync** for **command + immediate outcome** (HTTP 201, Problem Details). **Async** when **many subscribers**, **burst traffic**, or **slow downstream** must not block the client. |
| *How do you avoid double-charge on duplicate events?* | **Idempotent consumer**, unique keys, outbox + single transaction for “write + intent to publish.” |
| *What is the outbox pattern?* | **Co-locate** “business commit” and “message row” in **one DB transaction**; async publisher drains outbox. |

---

## In Cosmos API today (related ideas)

| Idea | Where |
|------|--------|
| Command idempotency (HTTP) | [`TransferController`](../../src/main/java/com/cosmos/api/controller/TransferController.java), [`TransferServiceImpl`](../../src/main/java/com/cosmos/api/service/impl/TransferServiceImpl.java) — `Idempotency-Key` |
| No message broker in repo | — |

---

## Quick self-check

1. Why is **at-least-once** the default mental model for Kafka consumers?
2. What two problems does the **transactional outbox** solve?
3. Why is **`event_id`** not the same as **`transfer_id`**?

---

## Next sub-point

**1.11 — OpenAPI / contract-first** — spec as source of truth, codegen, breaking-change detection.
