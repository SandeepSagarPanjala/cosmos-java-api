package com.cosmos.api.service.impl;

import com.cosmos.api.dto.request.UserPatchRequest;
import com.cosmos.api.dto.request.UserRegistrationRequest;
import com.cosmos.api.dto.response.UserResponse;
import com.cosmos.api.entity.User;
import com.cosmos.api.exception.UserNotFoundException;
import com.cosmos.api.repository.UserRepository;
import com.cosmos.api.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserResponse createUser(UserRegistrationRequest request) {
        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .passwordHash(request.getPassword()) // In production, hash this!
                .displayName(request.getDisplayName())
                .build();

        User savedUser = userRepository.save(user);
        return mapToDTO(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return mapToDTO(user);
    }

    @Override
    @Transactional
    public UserResponse patchUser(UUID id, UserPatchRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        if (request.getDisplayName() != null) {
            user.setDisplayName(request.getDisplayName());
        }
        return mapToDTO(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    private UserResponse mapToDTO(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .isActive(user.getIsActive())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }
}
