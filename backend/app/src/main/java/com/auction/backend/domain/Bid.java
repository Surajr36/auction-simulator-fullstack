package com.auction.backend.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "bids")
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private AuctionPlayer auctionPlayer;

    @ManyToOne(optional = false)
    private Team team;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected Bid() {
        // JPA
    }

    public Bid(AuctionPlayer auctionPlayer, Team team, BigDecimal amount) {
        this.auctionPlayer = auctionPlayer;
        this.team = team;
        this.amount = amount;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public AuctionPlayer getAuctionPlayer() {
        return auctionPlayer;
    }

    public Team getTeam() {
        return team;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
