package com.auction.backend.graphql;

import com.auction.backend.domain.Role;
import com.auction.backend.domain.User;
import com.auction.backend.service.AuthService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

/**
 * GraphQL Mutation Resolver for Authentication.
 * 
 * Handles:
 * - register: Create new user account
 * - login: Authenticate user and return JWT token
 * 
 * Security:
 * - These mutations are public (no @PreAuthorize)
 * - Anyone can register/login
 */
@Controller
public class AuthMutationResolver {

    private final AuthService authService;

    public AuthMutationResolver(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Mutation: register
     * 
     * Creates new user account with hashed password.
     * 
     * Returns JWT token immediately so user doesn't need to login again.
     * 
     * Example mutation:
     * mutation {
     *   register(input: {
     *     username: "john_doe"
     *     password: "securePassword123"
     *     email: "john@example.com"
     *     role: TEAM_USER
     *     teamId: "1"
     *   }) {
     *     token
     *     user {
     *       id
     *       username
     *       role
     *     }
     *   }
     * }
     */
    @MutationMapping
    public AuthPayload register(@Argument RegisterInput input) {
        // Create user
        User user = authService.register(
                input.username(),
                input.password(),
                input.email(),
                input.role(),
                input.teamId()
        );

        // Generate JWT token for immediate login
        String token = authService.login(input.username(), input.password());

        return new AuthPayload(token, user);
    }

    /**
     * Mutation: login
     * 
     * Authenticates user and returns JWT token.
     * 
     * Example mutation:
     * mutation {
     *   login(username: "john_doe", password: "securePassword123") {
     *     token
     *     user {
     *       id
     *       username
     *       email
     *       role
     *       team { name }
     *     }
     *   }
     * }
     */
    @MutationMapping
    public AuthPayload login(@Argument String username, @Argument String password) {
        // Authenticate and get token
        String token = authService.login(username, password);

        // Load user details
        User user = authService.getCurrentUser();

        return new AuthPayload(token, user);
    }

    // Input/Output Types
    public record RegisterInput(
            String username,
            String password,
            String email,
            Role role,
            Long teamId
    ) {}

    public record AuthPayload(String token, User user) {}
}
