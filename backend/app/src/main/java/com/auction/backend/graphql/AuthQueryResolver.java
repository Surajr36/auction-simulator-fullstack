package com.auction.backend.graphql;

import com.auction.backend.domain.User;
import com.auction.backend.service.AuthService;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

/**
 * GraphQL Query Resolver for Authentication.
 * 
 * Handles:
 * - me: Get current authenticated user
 * 
 * Security:
 * - Requires authentication (any authenticated user can query their own details)
 */
@Controller
public class AuthQueryResolver {

    private final AuthService authService;

    public AuthQueryResolver(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Query: me
     * 
     * Returns current authenticated user's details.
     * 
     * Frontend usage: Check if user is logged in, display user info
     * 
     * Example query:
     * query {
     *   me {
     *     id
     *     username
     *     email
     *     role
     *     team { name }
     *   }
     * }
     */
    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public User me() {
        return authService.getCurrentUser();
    }
}
