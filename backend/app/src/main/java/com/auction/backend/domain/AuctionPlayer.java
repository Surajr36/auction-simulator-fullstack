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

    /* ---- Domain behavior (minimal, intentional) ---- */

    public void start() {
        if (status != AuctionPlayerStatus.NOT_STARTED) {
            throw new IllegalStateException("AuctionPlayer cannot be started");
        }
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

}
