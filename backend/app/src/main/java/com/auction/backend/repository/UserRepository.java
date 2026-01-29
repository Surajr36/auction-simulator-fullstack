package com.auction.backend.repository;

import com.auction.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by username for authentication.
     * Used during login to validate credentials.
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Check if username already exists (for registration validation).
     */
    boolean existsByUsername(String username);
    
    /**
     * Check if email already exists (for registration validation).
     */
    boolean existsByEmail(String email);
}
