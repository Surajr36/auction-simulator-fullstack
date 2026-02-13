package com.auction.backend.domain;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * WaitRequest Entity - Tracks WAIT usage during auctions.
 *
 * Two types:
 * 1. Team WAIT (adminWait = false, team != null)
 *    - Extends timer by 30 seconds
 *    - Limited to 2 per team per player
 *
 * 2. Admin WAIT (adminWait = true, team = null)
 *    - Pauses timer indefinitely
 *    - No limit
 *
 * Why a separate entity?
 * - Audit trail: Track who used WAIT and when
 * - Count tracking: Query count per team per player
 * - History: Can review WAIT patterns after auction
 */
@Entity
@Table(name = "wait_requests")
public class WaitRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "auction_player_id", nullable = false)
    private AuctionPlayer auctionPlayer;

    /**
     * Team that used the WAIT.
     * NULL for admin WAITs (admin has no team context).
     */
    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    /**
     * true = Admin pause (freezes timer)
     * false = Team WAIT (extends timer by 30s)
     */
    @Column(nullable = false)
    private boolean adminWait;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected WaitRequest() {
        // JPA
    }

    /**
     * Factory method for Team WAIT.
     */
    public static WaitRequest teamWait(AuctionPlayer auctionPlayer, Team team) {
        WaitRequest wr = new WaitRequest();
        wr.auctionPlayer = auctionPlayer;
        wr.team = team;
        wr.adminWait = false;
        wr.createdAt = Instant.now();
        return wr;
    }

    /**
     * Factory method for Admin WAIT/Pause.
     */
    public static WaitRequest adminWait(AuctionPlayer auctionPlayer) {
        WaitRequest wr = new WaitRequest();
        wr.auctionPlayer = auctionPlayer;
        wr.team = null;
        wr.adminWait = true;
        wr.createdAt = Instant.now();
        return wr;
    }

    // Getters

    public Long getId() {
        return id;
    }

    public AuctionPlayer getAuctionPlayer() {
        return auctionPlayer;
    }

    public Team getTeam() {
        return team;
    }

    public boolean isAdminWait() {
        return adminWait;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
