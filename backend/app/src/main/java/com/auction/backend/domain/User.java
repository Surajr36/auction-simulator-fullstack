package com.auction.backend.domain;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * User entity representing system users (Admin or Team Users).
 * 
 * Key Design Decisions:
 * - Password stored as BCrypt hash (never plain text)
 * - Role-based access control (ADMIN, TEAM_USER)
 * - Optional relationship with Team (admins don't belong to teams)
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password; // BCrypt hashed

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team; // Null for admins

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected User() {
        // JPA requires no-arg constructor
    }

    public User(String username, String password, String email, Role role, Team team) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.team = team;
        this.createdAt = Instant.now();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public Team getTeam() {
        return team;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    // Domain behavior
    public void setTeam(Team team) {
        this.team = team;
    }
}
