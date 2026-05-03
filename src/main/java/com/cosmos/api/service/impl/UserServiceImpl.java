package com.cosmos.api.service.impl;

import com.cosmos.api.dto.request.UserPatchRequest;
import com.cosmos.api.dto.request.UserRegistrationRequest;
import com.cosmos.api.dto.response.PagedUsersResponse;
import com.cosmos.api.dto.response.UserResponse;
import com.cosmos.api.entity.User;
import com.cosmos.api.exception.UserNotFoundException;
import com.cosmos.api.repository.UserRepository;
import com.cosmos.api.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("createdAt", "email", "username");

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
    public PagedUsersResponse searchUsers(String emailContains, int page, int size, String sortBy, String sortDirection) {
        int safeSize = size < 1 ? DEFAULT_PAGE_SIZE : Math.min(size, MAX_PAGE_SIZE);
        int safePage = Math.max(page, 0);

        String property = (sortBy != null && ALLOWED_SORT_FIELDS.contains(sortBy)) ? sortBy : "createdAt";
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(direction, property));

        Page<User> entityPage;
        if (emailContains != null && !emailContains.isBlank()) {
            entityPage = userRepository.findByEmailContainingIgnoreCase(emailContains.trim(), pageable);
        } else {
            entityPage = userRepository.findAll(pageable);
        }

        Page<UserResponse> dtoPage = entityPage.map(this::mapToDTO);
        return PagedUsersResponse.from(dtoPage);
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
