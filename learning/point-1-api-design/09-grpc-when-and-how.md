# 1.9 — gRPC: contracts, runtime, when to use it

---

## Hey Sandeep — game plan

gRPC is **not** “REST but faster.” It is a **different RPC style**: **HTTP/2** framing, **strongly typed** messages from **`.proto`**, and code generation for **clients and servers**. Banks often use it **inside** the mesh (service-to-service) while keeping **REST + OpenAPI** at the edge for browsers and partners.

**Do today:**

1. Read **“IDL → generated code”** once — you should be able to point at **`service`**, **`rpc`**, and **`message`** in a `.proto`.
2. Open the **study proto** [`examples/cosmos_user_lookup_v1.proto`](./examples/cosmos_user_lookup_v1.proto) and map each RPC to “what would this be in REST?”
3. Memorize **one** “when gRPC vs REST” answer you believe (see table below).

---

## What gRPC is (interview-safe)

| Piece | What to say |
|--------|-------------|
| **IDL** | **Protocol Buffers** in `.proto` files define **messages** and **services** — the contract is explicit before implementation. |
| **Wire format** | Usually **protobuf** (binary, compact). Not human-readable like JSON in a browser devtools tab. |
| **Transport** | **HTTP/2** — multiplexing, header compression, one connection reused. |
| **Code gen** | `protoc` (plus language plugins) emits **stubs**; you implement **server** interfaces and call **clients** with types checked at compile time. |

Cosmos today is **Spring Web MVC + JSON REST** (`UserController`, etc.). The sample `.proto` is **learning-only** — it shows what a **parallel** internal contract could look like, without pulling gRPC into the Maven build yet.

---

## `.proto` anatomy (minimum you should recognize)

- **`syntax = "proto3";`** — language version; affects defaults (e.g. no required fields in proto3).
- **`package`** — logical namespace; also affects generated code layout.
- **`option java_package`** — where Java classes land after generation.
- **`service Foo { rpc Bar(In) returns (Out); }`** — one **RPC method**; `In` / `Out` are **messages**.
- **`message`** — fields numbered (`1`, `2`, …) for **wire compatibility** (never reuse field numbers for different meanings).

**Versioning:** teams often version **`package`** or **service name** (`UserLookupV2`) or path to `.proto` — same *idea* as REST `/v1` vs `/v2`, different mechanics.

---

## Unary vs streaming (they will ask)

| Style | Shape | Typical banking-ish example |
|--------|--------|------------------------------|
| **Unary** | one request → one response | Get account balance, validate party id |
| **Server streaming** | one request → many responses | Watch transactions / market ticks (with backpressure care) |
| **Client streaming** | many requests → one response | Bulk upload of instructions |
| **Bidirectional** | both ways | Long-lived sessions (chat, complex trading pipes) — less common unless you need it |

Most **internal CRUD-ish** RPCs are **unary**. Streaming is powerful but adds **flow control**, **timeouts**, and **replay** complexity.

---

## Errors, deadlines, and idempotency

- **gRPC status** — `OK`, `NOT_FOUND`, `ALREADY_EXISTS`, `INVALID_ARGUMENT`, `DEADLINE_EXCEEDED`, `UNAVAILABLE`, … — not the same as HTTP status codes, though gateways often **map** them.
- **Deadlines** — client says “do not spend more than X ms”; critical for **not** tying up thread pools when downstream is slow.
- **Idempotency** — not automatic. For money-like operations you still design **keys**, dedupe tables, or **at-least-once** consumers — same domain thinking as REST (JD 1.2 / 1.7).

---

## gRPC vs REST (defensible one-liner)

| Choose **REST (+ OpenAPI)** when… | Choose **gRPC** when… |
|-------------------------------------|-------------------------|
| Callers are **browsers**, public **partners**, or broad **ecosystem** tooling | Callers are **your own services** in one platform / mesh |
| You want **cacheable GETs**, links in emails, `curl` everywhere | You want **binary efficiency**, **streaming**, **strict contracts** |
| Human inspection of JSON matters | **Codegen + types** matter more than raw JSON |

**Pattern:** **API gateway / BFF** exposes REST to the world; **inner tier** uses gRPC between microservices.

---

## Spring / Java (what exists, without turning this repo into gRPC today)

Common options teams mention:

- **grpc-java** (core) + **Spring Boot** integration via community starters (e.g. **grpc-spring-boot-starter** from `net.devh`) — annotate a `@GrpcService` implementation.
- **spring-grpc** (Spring ecosystem) — evolving official-style story; check current docs for your Boot version before greenfield.

Interview: you do **not** need to recite Maven coordinates — you need **“we generate from `.proto`, implement the service base class, register on a TCP port (often 9090), use TLS/mTLS internally.”**

---

## Study-only contract in this repo

| Artifact | Purpose |
|----------|---------|
| [`examples/cosmos_user_lookup_v1.proto`](./examples/cosmos_user_lookup_v1.proto) | Minimal **`UserLookup.GetUser`** — compare mentally to `GET /api/v1/users/{id}`. |

---

## Interview: what they ask

| They might say | Your direction |
|----------------|----------------|
| *How do you version gRPC APIs?* | **Package / service / file** conventions; never **reuse field numbers**; additive fields OK with care; breaking changes → new package or service. |
| *How does a browser call gRPC?* | Usually **not** raw gRPC — use **gRPC-Web** + proxy, or **REST gateway** in front. |
| *Why HTTP/2 for internal RPC?* | **Multiplexing**, **efficient headers**, good fit for **many small calls** between services. |
| *What’s the biggest ops concern?* | **Load balancing** for long-lived HTTP/2 connections (L7 routers need gRPC-aware config), **timeouts**, **backpressure** on streams. |

---

## Quick self-check

1. In `.proto`, why are fields numbered (`= 1`, `= 2`)?
2. Name one case where **server streaming** beats unary.
3. Why might a bank still expose **REST** to partners even if internal services are **gRPC**?

---

## Next sub-point

**1.10 — Event-driven APIs** — see [10-event-driven-apis.md](./10-event-driven-apis.md).
