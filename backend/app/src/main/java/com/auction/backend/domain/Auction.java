package com.auction.backend.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "auctions")
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuctionStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public Auction() {
        this.status = AuctionStatus.CREATED;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public AuctionStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    /* ---- Domain behavior (very minimal for now) ---- */

    public void start() {
        if (this.status != AuctionStatus.CREATED) {
            throw new IllegalStateException("Auction cannot be started");
        }
        this.status = AuctionStatus.LIVE;
    }

    public void finish() {
        if (this.status != AuctionStatus.LIVE) {
            throw new IllegalStateException("Auction cannot be finished");
        }
        this.status = AuctionStatus.FINISHED;
    }
}
