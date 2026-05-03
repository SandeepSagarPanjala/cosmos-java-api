package com.cosmos.api.repository;

import com.cosmos.api.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    /** Case-insensitive substring match on email (JD 1.6 search). */
    Page<User> findByEmailContainingIgnoreCase(String email, Pageable pageable);
}
