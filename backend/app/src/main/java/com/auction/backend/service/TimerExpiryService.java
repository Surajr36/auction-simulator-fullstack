package com.auction.backend.service;

import com.auction.backend.domain.AuctionPlayer;
import com.auction.backend.domain.AuctionPlayerStatus;
import com.auction.backend.repository.AuctionPlayerRepository;
import com.auction.backend.repository.BidRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Scheduled Task Service - Timer Expiry Management
 * 
 * Runs every 5 seconds to check for expired timers.
 * Automatically marks players as SOLD (if bids exist) or UNSOLD (if no bids).
 * 
 * Why Scheduled Task?
 * - Players cannot stay LIVE indefinitely
 * - Manual intervention not scalable
 * - Automatic state transition based on time
 * 
 * Configuration:
 * - fixedDelay = 5000 (5 seconds between executions)
 * - @EnableScheduling required in main application class
 */
@Service
public class TimerExpiryService {

    private final AuctionPlayerRepository auctionPlayerRepository;
    private final BidRepository bidRepository;

    public TimerExpiryService(
            AuctionPlayerRepository auctionPlayerRepository,
            BidRepository bidRepository
    ) {
        this.auctionPlayerRepository = auctionPlayerRepository;
        this.bidRepository = bidRepository;
    }

    /**
     * Scheduled task: Check for expired timers every 5 seconds.
     * 
     * Process:
     * 1. Find all LIVE auction players with timerEndAt < NOW
     * 2. For each expired player:
     *    - If bids exist → Mark SOLD to highest bidder
     *    - If no bids → Mark UNSOLD
     * 3. Save changes to database
     * 
     * Transaction:
     * - @Transactional ensures atomicity
     * - If error occurs, all changes rolled back
     * - Prevents partial state updates
     */
    @Scheduled(fixedDelay = 5000)  // Every 5 seconds after previous execution completes
    @Transactional
    public void checkExpiredTimers() {
        Instant now = Instant.now();
        
        // Find all LIVE players with expired timers
        List<AuctionPlayer> expiredPlayers = auctionPlayerRepository
                .findByStatusAndTimerEndAtBefore(AuctionPlayerStatus.LIVE, now);
        
        for (AuctionPlayer player : expiredPlayers) {
            // Check if any bids exist
            long bidCount = bidRepository.countByAuctionPlayerId(player.getId());
            
            if (bidCount > 0) {
                // Has bids → Mark SOLD to current highest bidder
                player.markSold(
                    player.getCurrentHighestBidTeam(),
                    player.getCurrentPrice()
                );
                System.out.println("[TIMER EXPIRED] Player " + player.getId() + 
                    " SOLD to " + player.getCurrentHighestBidTeam().getName() + 
                    " for ₹" + player.getCurrentPrice() + " cr");
            } else {
                // No bids → Mark UNSOLD
                player.markUnsold();
                System.out.println("[TIMER EXPIRED] Player " + player.getId() + " marked UNSOLD");
            }
            
            auctionPlayerRepository.save(player);
        }
        
        // Log execution (useful for debugging)
        if (!expiredPlayers.isEmpty()) {
            System.out.println("[TIMER CHECK] Processed " + expiredPlayers.size() + " expired player(s)");
        }
    }
}
