package com.auction.backend.repository;

import com.auction.backend.domain.AuctionPlayer;
import com.auction.backend.domain.AuctionPlayerStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface AuctionPlayerRepository extends JpaRepository<AuctionPlayer, Long> {

    List<AuctionPlayer> findByAuctionId(Long auctionId);
    
    /**
     * Find LIVE auction players whose timer has expired.
     * Used by scheduled task for auto-expiry.
     */
    List<AuctionPlayer> findByStatusAndTimerEndAtBefore(AuctionPlayerStatus status, Instant time);
}
