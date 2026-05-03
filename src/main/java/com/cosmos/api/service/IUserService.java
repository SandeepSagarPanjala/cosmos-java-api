package com.cosmos.api.service;

import com.cosmos.api.dto.request.UserPatchRequest;
import com.cosmos.api.dto.request.UserRegistrationRequest;
import com.cosmos.api.dto.response.PagedUsersResponse;
import com.cosmos.api.dto.response.UserResponse;

import java.util.UUID;

public interface IUserService {
    UserResponse createUser(UserRegistrationRequest request);
    UserResponse getUserById(UUID id);

    UserResponse patchUser(UUID id, UserPatchRequest request);

    /**
     * Paginated collection with optional email substring search (JD 1.6).
     */
    PagedUsersResponse searchUsers(String emailContains, int page, int size, String sortBy, String sortDirection);
    void deleteUser(UUID id);
}
