# JD Point 1 — API Design

Sub-points below match the study plan (1.1–1.12). Each has a note under this folder and pointers into runnable code.

| Sub | Topic | Note | Status |
|-----|--------|------|--------|
| **1.1** | REST: resources & URLs | [01-resources-and-urls.md](./01-resources-and-urls.md) | Done |
| **1.2** | HTTP methods & safety / idempotency | [02-http-methods-and-idempotency.md](./02-http-methods-and-idempotency.md) | Done |
| **1.3** | Status codes & headers | [03-status-codes-and-headers.md](./03-status-codes-and-headers.md) | Done |
| **1.4** | DTOs & validation | [04-dtos-and-validation.md](./04-dtos-and-validation.md) | Done |
| **1.5** | Errors as data (Problem Details) | [05-errors-as-data-problem-details.md](./05-errors-as-data-problem-details.md) | Done |
| **1.6** | List / search / pagination | [06-list-search-pagination.md](./06-list-search-pagination.md) | Done |
| **1.7** | Actions & domain operations | [07-actions-and-domain-operations.md](./07-actions-and-domain-operations.md) | Done |
| **1.8** | Versioning & compatibility (design) | [08-versioning-and-compatibility.md](./08-versioning-and-compatibility.md) | Done |
| **1.9** | gRPC | [09-grpc-when-and-how.md](./09-grpc-when-and-how.md) | Done |
| **1.10** | Event-driven APIs | *TBD* | Planned |
| **1.11** | OpenAPI / contract-first | *TBD* | Planned |
| **1.12** | AsyncAPI | *TBD* | Planned |

**Primary REST surface today:** `UserController` — [`src/main/java/com/cosmos/api/controller/UserController.java`](../../src/main/java/com/cosmos/api/controller/UserController.java)
