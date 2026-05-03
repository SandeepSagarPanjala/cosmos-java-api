package com.cosmos.api.service;

import com.cosmos.api.dto.request.UserPatchRequest;
import com.cosmos.api.dto.request.UserRegistrationRequest;
import com.cosmos.api.dto.response.UserResponse;

import java.util.List;
import java.util.UUID;

public interface IUserService {
    UserResponse createUser(UserRegistrationRequest request);
    UserResponse getUserById(UUID id);

    UserResponse patchUser(UUID id, UserPatchRequest request);

    List<UserResponse> getAllUsers();
    void deleteUser(UUID id);
}
