package com.auction.backend.security;

import com.auction.backend.domain.User;
import com.auction.backend.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom UserDetailsService implementation.
 * 
 * Purpose: Bridge between Spring Security and our User entity.
 * 
 * Spring Security uses UserDetails interface, but our User is a custom entity.
 * This service converts our User → UserDetails that Spring Security understands.
 * 
 * Called by:
 * 1. JwtAuthenticationFilter (after validating token)
 * 2. AuthenticationManager (during login)
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Load user by username from database.
     * 
     * This method is called by Spring Security to:
     * 1. Authenticate user during login
     * 2. Load user details after JWT validation
     * 
     * @param username Username to search for
     * @return UserDetails object Spring Security can work with
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                getAuthorities(user)
        );
    }

    /**
     * Convert our Role enum to Spring Security authorities.
     * 
     * Spring Security uses GrantedAuthority interface for roles/permissions.
     * We convert our simple Role enum to this format.
     * 
     * Note: Spring Security expects "ROLE_" prefix for @PreAuthorize("hasRole('ADMIN')")
     */
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
    }
}
