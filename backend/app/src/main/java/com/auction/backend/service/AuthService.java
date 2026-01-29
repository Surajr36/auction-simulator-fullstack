package com.auction.backend.service;

import com.auction.backend.domain.Role;
import com.auction.backend.domain.Team;
import com.auction.backend.domain.User;
import com.auction.backend.exception.DomainException;
import com.auction.backend.repository.TeamRepository;
import com.auction.backend.repository.UserRepository;
import com.auction.backend.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authentication Service - Business logic for user authentication.
 * 
 * Responsibilities:
 * 1. User registration with password hashing
 * 2. User login with credential validation
 * 3. JWT token generation
 * 4. Integration with Spring Security
 * 
 * Transaction Management:
 * - @Transactional ensures database consistency
 * - Rollback on exceptions
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(
            UserRepository userRepository,
            TeamRepository teamRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Register new user.
     * 
     * Validation:
     * - Username must be unique
     * - Email must be unique
     * - Password must be hashed (never store plain text)
     * - Team required for TEAM_USER, optional for ADMIN
     * 
     * Security Note: Password hashing is one-way.
     * Even if database is compromised, passwords remain secure.
     * 
     * @param username Unique username
     * @param password Plain text password (will be hashed)
     * @param email Unique email
     * @param role ADMIN or TEAM_USER
     * @param teamId Required for TEAM_USER
     * @return Created user (password already hashed)
     */
    @Transactional
    public User register(String username, String password, String email, Role role, Long teamId) {
        
        // Validation: Check uniqueness
        if (userRepository.existsByUsername(username)) {
            throw new DomainException("Username already exists");
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new DomainException("Email already exists");
        }

        // Validation: Team required for TEAM_USER
        Team team = null;
        if (role == Role.TEAM_USER) {
            if (teamId == null) {
                throw new DomainException("Team is required for TEAM_USER role");
            }
            team = teamRepository.findById(teamId)
                    .orElseThrow(() -> new DomainException("Team not found"));
        }

        // Hash password using BCrypt
        // Result format: $2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6
        // $2a$ = algorithm, $10$ = rounds, next 22 chars = salt, rest = hash
        String hashedPassword = passwordEncoder.encode(password);

        // Create and save user
        User user = new User(username, hashedPassword, email, role, team);
        return userRepository.save(user);
    }

    /**
     * Login user and generate JWT token.
     * 
     * Flow:
     * 1. Authenticate credentials using Spring Security
     * 2. If valid, generate JWT token
     * 3. Return token to client
     * 
     * Authentication Process:
     * - AuthenticationManager loads user via UserDetailsService
     * - Compares hashed password using BCrypt
     * - If match, returns Authentication object
     * - If fail, throws BadCredentialsException
     * 
     * Security Note: We return generic "Invalid credentials" message
     * to avoid revealing whether username or password was wrong
     * (prevents username enumeration attacks).
     * 
     * @param username Username to authenticate
     * @param password Plain text password
     * @return JWT token string
     */
    @Transactional(readOnly = true)
    public String login(String username, String password) {
        try {
            // Create authentication request
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // Set authentication in SecurityContext (for this request)
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate and return JWT token
            return jwtTokenProvider.generateToken(authentication);
            
        } catch (Exception e) {
            // Generic error message (don't reveal if username exists)
            throw new DomainException("Invalid username or password");
        }
    }

    /**
     * Get current authenticated user.
     * 
     * Used by GraphQL resolver for "me" query.
     * Extracts username from SecurityContext and loads from database.
     * 
     * @return Current user
     */
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new DomainException("Not authenticated");
        }

        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
