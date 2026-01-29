package com.auction.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT Token Provider - Core component for JWT token lifecycle.
 * 
 * Responsibilities:
 * 1. Generate JWT tokens with user claims
 * 2. Validate token signature and expiry
 * 3. Extract user information from tokens
 * 
 * Security Notes:
 * - Uses HS256 algorithm (HMAC with SHA-256)
 * - Secret key must be at least 256 bits (32 characters)
 * - Tokens are signed, not encrypted (payload is readable)
 * - Signature prevents tampering
 */
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    /**
     * Generate JWT token from authenticated user.
     * 
     * Token structure:
     * Header: {alg: HS256, typ: JWT}
     * Payload: {sub: username, role: ROLE, iat: issued_at, exp: expiry}
     * Signature: HMACSHA256(header + payload, secret)
     */
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .subject(userDetails.getUsername())     // 'sub' claim - who is this token for?
                .issuedAt(now)                          // 'iat' claim - when was it issued?
                .expiration(expiryDate)                 // 'exp' claim - when does it expire?
                .signWith(key)                          // Sign with secret key (HS256 auto-detected)
                .compact();                             // Convert to compact string
    }

    /**
     * Extract username from JWT token.
     * Used after validation to load user from database.
     */
    public String getUsernameFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    /**
     * Validate JWT token.
     * 
     * Validation checks:
     * 1. Signature matches (token not tampered)
     * 2. Token not expired
     * 3. Claims structure valid
     * 
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);  // Throws exception if invalid
            
            return true;
        } catch (SecurityException ex) {
            // Invalid signature
            System.err.println("Invalid JWT signature: " + ex.getMessage());
        } catch (MalformedJwtException ex) {
            // Invalid token structure
            System.err.println("Invalid JWT token: " + ex.getMessage());
        } catch (ExpiredJwtException ex) {
            // Token expired
            System.err.println("Expired JWT token: " + ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            // Unsupported token
            System.err.println("Unsupported JWT token: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            // Empty or null token
            System.err.println("JWT claims string is empty: " + ex.getMessage());
        }
        
        return false;
    }
}
