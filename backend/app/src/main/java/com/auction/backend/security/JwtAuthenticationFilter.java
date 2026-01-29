package com.auction.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter - Entry point for JWT validation.
 * 
 * This filter intercepts EVERY request before it reaches the controllers.
 * 
 * Flow:
 * 1. Extract JWT from Authorization header (format: "Bearer <token>")
 * 2. Validate token signature and expiry
 * 3. Extract username from token
 * 4. Load user from database
 * 5. Create Authentication object
 * 6. Store in SecurityContext (thread-local storage)
 * 
 * Why OncePerRequestFilter?
 * - Guarantees filter runs exactly once per request
 * - Avoids multiple executions in async/forward scenarios
 * 
 * SecurityContext Explained:
 * - Thread-local storage for authenticated user
 * - Each request runs in its own thread
 * - Controllers/services access via SecurityContextHolder.getContext()
 * - Cleared automatically after request completes
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtTokenProvider tokenProvider,
            UserDetailsService userDetailsService
    ) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Main filter method - called for every HTTP request.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            // Step 1: Extract JWT from request
            String jwt = extractJwtFromRequest(request);

            // Step 2: Validate and process if token exists
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                
                // Step 3: Extract username from validated token
                String username = tokenProvider.getUsernameFromToken(jwt);

                // Step 4: Load user details from database
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Step 5: Create authentication object
                // This represents the authenticated user with their authorities
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,           // Principal (the authenticated user)
                                null,                  // Credentials (not needed after auth)
                                userDetails.getAuthorities()  // Roles/permissions
                        );

                // Add request details (IP, session ID, etc.)
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Step 6: Store in SecurityContext
                // Now any @PreAuthorize checks will use this authentication
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            // Log but don't block request - let Spring Security handle unauthorized
            logger.error("Could not set user authentication in security context", ex);
        }

        // Continue filter chain (move to next filter/controller)
        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from Authorization header.
     * 
     * Expected format: "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
     * 
     * @return JWT token string or null if not present/malformed
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remove "Bearer " prefix
        }
        
        return null;
    }
}
