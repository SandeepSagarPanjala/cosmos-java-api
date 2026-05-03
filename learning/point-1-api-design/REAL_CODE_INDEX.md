# Point 1 — quick map to Cosmos API source

| Area | Path |
|------|------|
| Version path constants (JD 1.8) | `src/main/java/com/cosmos/api/web/ApiPaths.java` |
| User REST endpoints | `src/main/java/com/cosmos/api/controller/UserController.java` |
| Transfer command API | `src/main/java/com/cosmos/api/controller/TransferController.java` |
| HTTP method semantics demo | `src/main/java/com/cosmos/api/sample/HttpMethodSemanticsDemoController.java` |
| Registration request DTO | `src/main/java/com/cosmos/api/dto/request/UserRegistrationRequest.java` |
| Patch request DTO | `src/main/java/com/cosmos/api/dto/request/UserPatchRequest.java` |
| Transfer command DTO | `src/main/java/com/cosmos/api/dto/request/TransferCommandRequest.java` |
| Transfer response DTO | `src/main/java/com/cosmos/api/dto/response/TransferResponse.java` |
| User response DTO | `src/main/java/com/cosmos/api/dto/response/UserResponse.java` |
| Paged users response | `src/main/java/com/cosmos/api/dto/response/PagedUsersResponse.java` |
| Service contract | `src/main/java/com/cosmos/api/service/IUserService.java` |
| Service implementation | `src/main/java/com/cosmos/api/service/impl/UserServiceImpl.java` |
| Transfer service | `src/main/java/com/cosmos/api/service/ITransferService.java` |
| Transfer service impl | `src/main/java/com/cosmos/api/service/impl/TransferServiceImpl.java` |
| JPA entity | `src/main/java/com/cosmos/api/entity/User.java` |
| Transfer entity | `src/main/java/com/cosmos/api/entity/Transfer.java` |
| Repository | `src/main/java/com/cosmos/api/repository/UserRepository.java` |
| Transfer repository | `src/main/java/com/cosmos/api/repository/TransferRepository.java` |
| Global errors | `src/main/java/com/cosmos/api/exception/GlobalExceptionHandler.java` |
| Problem `type` URIs (RFC 7807) | `src/main/java/com/cosmos/api/exception/ProblemTypeUri.java` |
| User not found (404) | `src/main/java/com/cosmos/api/exception/UserNotFoundException.java` |
| App entry | `src/main/java/com/cosmos/api/CosmosJavaApiApplication.java` |
| Config | `src/main/resources/application.properties` |
| Study-only gRPC `.proto` (JD 1.9, not in Maven build) | `learning/point-1-api-design/examples/cosmos_user_lookup_v1.proto` |
| Study-only domain event JSON (JD 1.10) | `learning/point-1-api-design/examples/transfer_completed_event_v1.json` |
