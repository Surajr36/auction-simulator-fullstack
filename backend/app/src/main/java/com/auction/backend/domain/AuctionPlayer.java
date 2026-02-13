package com.auction.backend.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "auction_players")
public class AuctionPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Auction auction;

    @ManyToOne(optional = false)
    private Player player;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal currentPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuctionPlayerStatus status;

    @ManyToOne
    private Team currentHighestBidTeam;

    @Column
    private Instant timerStartAt;

    @Column
    private Instant timerEndAt;

    /**
     * Admin pause fields.
     * adminPaused: true when admin has frozen the timer.
     * adminPausedAt: when the pause started (used to calc remaining time).
     * timerRemainingOnPause: milliseconds of timer left when paused.
     */
    @Column(nullable = false)
    private boolean adminPaused = false;

    @Column
    private Instant adminPausedAt;

    @Column
    private Long timerRemainingOnPause;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected AuctionPlayer() {
        // JPA
    }

    public AuctionPlayer(Auction auction, Player player, BigDecimal basePrice) {
        this.auction = auction;
        this.player = player;
        this.basePrice = basePrice;
        this.currentPrice = basePrice;
        this.status = AuctionPlayerStatus.NOT_STARTED;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Auction getAuction() {
        return auction;
    }

    public Player getPlayer() {
        return player;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public AuctionPlayerStatus getStatus() {
        return status;
    }

    public Team getCurrentHighestBidTeam() {
        return currentHighestBidTeam;
    }

    public Instant getTimerStartAt() {
        return timerStartAt;
    }

    public Instant getTimerEndAt() {
        return timerEndAt;
    }

    public boolean isAdminPaused() {
        return adminPaused;
    }

    public Instant getAdminPausedAt() {
        return adminPausedAt;
    }

    public Long getTimerRemainingOnPause() {
        return timerRemainingOnPause;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    /* ---- Domain behavior (minimal, intentional) ---- */

    public void start() {
        if (status != AuctionPlayerStatus.NOT_STARTED) {
            throw new IllegalStateException("AuctionPlayer cannot be started");
        }
        
        Instant now = Instant.now();
        this.timerStartAt = now;
        this.timerEndAt = now.plusSeconds(120);  // 2 minutes initial timer
        this.status = AuctionPlayerStatus.LIVE;
    }

    public void markSold(Team winningTeam, BigDecimal finalPrice) {
        if (status != AuctionPlayerStatus.LIVE) {
            throw new IllegalStateException("Only LIVE AuctionPlayer can be sold");
        }
        this.currentHighestBidTeam = winningTeam;
        this.currentPrice = finalPrice;
        this.status = AuctionPlayerStatus.SOLD;
    }

    public void markUnsold() {
        if (status != AuctionPlayerStatus.LIVE) {
            throw new IllegalStateException("Only LIVE AuctionPlayer can be unsold");
        }
        this.status = AuctionPlayerStatus.UNSOLD;
    }

    public void updateCurrentBid(Team team, BigDecimal amount) {
    if (status != AuctionPlayerStatus.LIVE) {
        throw new IllegalStateException("Cannot place bid when auction is not LIVE");
    }

    this.currentHighestBidTeam = team;
    this.currentPrice = amount;
}

    /**
     * Reset timer to 30 seconds from now.
     * Called after every valid bid.
     */
    public void resetTimer() {
        if (status != AuctionPlayerStatus.LIVE) {
            throw new IllegalStateException("Can only reset timer for LIVE players");
        }
        
        this.timerEndAt = Instant.now().plusSeconds(30);  // 30 seconds
    }

    /**
     * Check if timer has expired.
     * Used by scheduled task and bid validation.
     */
    public boolean isTimerExpired() {
        if (timerEndAt == null) {
            return false;
        }
        return Instant.now().isAfter(timerEndAt);
    }

    /* ---- Admin Pause / Resume ---- */

    /**
     * Admin pauses the timer.
     * Calculates remaining milliseconds and freezes the countdown.
     */
    public void adminPause() {
        if (status != AuctionPlayerStatus.LIVE) {
            throw new IllegalStateException("Can only pause LIVE players");
        }
        if (adminPaused) {
            throw new IllegalStateException("Already paused");
        }

        Instant now = Instant.now();
        long remainingMs = java.time.Duration.between(now, timerEndAt).toMillis();
        if (remainingMs < 0) remainingMs = 0;

        this.adminPaused = true;
        this.adminPausedAt = now;
        this.timerRemainingOnPause = remainingMs;
        // timerEndAt is NOT changed — it becomes stale while paused.
    }

    /**
     * Admin resumes the timer.
     * Restores timerEndAt based on the remaining time that was saved on pause.
     */
    public void adminResume() {
        if (!adminPaused) {
            throw new IllegalStateException("Not currently paused");
        }

        Instant now = Instant.now();
        this.timerEndAt = now.plusMillis(timerRemainingOnPause);
        this.adminPaused = false;
        this.adminPausedAt = null;
        this.timerRemainingOnPause = null;
    }

    /**
     * Extend timer by the given number of seconds.
     * Used by Team WAIT (+30 seconds).
     */
    public void extendTimer(long seconds) {
        if (status != AuctionPlayerStatus.LIVE) {
            throw new IllegalStateException("Can only extend timer for LIVE players");
        }
        if (adminPaused) {
            // If paused, add to the saved remaining time instead
            this.timerRemainingOnPause += (seconds * 1000);
        } else {
            this.timerEndAt = this.timerEndAt.plusSeconds(seconds);
        }
    }

}
