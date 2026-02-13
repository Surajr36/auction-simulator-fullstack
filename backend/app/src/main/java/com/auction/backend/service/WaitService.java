package com.auction.backend.service;

import com.auction.backend.domain.*;
import com.auction.backend.exception.DomainException;
import com.auction.backend.repository.AuctionPlayerRepository;
import com.auction.backend.repository.TeamRepository;
import com.auction.backend.repository.WaitRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * WaitService — Handles Team WAIT and Admin Pause/Resume.
 *
 * Team WAIT:
 *   - Each team gets 2 WAITs per auction player
 *   - Each WAIT extends the timer by 30 seconds
 *   - Player must be LIVE
 *
 * Admin Pause:
 *   - Freezes the timer indefinitely
 *   - Bids are still accepted (handled by BidService)
 *   - Timer resumes on adminResume()
 *
 * Why @Transactional?
 *   - Both the WaitRequest insert and AuctionPlayer update must succeed together
 *   - If anything fails, everything rolls back
 */
@Service
public class WaitService {

    private static final int MAX_TEAM_WAITS_PER_PLAYER = 2;
    private static final long WAIT_EXTEND_SECONDS = 30;

    private final AuctionPlayerRepository auctionPlayerRepository;
    private final TeamRepository teamRepository;
    private final WaitRequestRepository waitRequestRepository;

    public WaitService(
            AuctionPlayerRepository auctionPlayerRepository,
            TeamRepository teamRepository,
            WaitRequestRepository waitRequestRepository
    ) {
        this.auctionPlayerRepository = auctionPlayerRepository;
        this.teamRepository = teamRepository;
        this.waitRequestRepository = waitRequestRepository;
    }

    /**
     * Team uses a WAIT on a LIVE auction player.
     *
     * Validation chain:
     * 1. AuctionPlayer must exist and be LIVE
     * 2. Not admin-paused (no point extending while paused)
     * 3. Team must exist
     * 4. Team has < 2 WAITs used on this player
     *
     * Side effects:
     * - Timer extended by 30 seconds
     * - WaitRequest record created for audit
     */
    @Transactional
    public WaitRequest useTeamWait(Long auctionPlayerId, Long teamId) {

        AuctionPlayer auctionPlayer = auctionPlayerRepository.findById(auctionPlayerId)
                .orElseThrow(() -> new DomainException("AuctionPlayer not found"));

        if (auctionPlayer.getStatus() != AuctionPlayerStatus.LIVE) {
            throw new DomainException("Player is not LIVE");
        }

        if (auctionPlayer.isAdminPaused()) {
            throw new DomainException("Cannot use WAIT while player is admin-paused");
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new DomainException("Team not found"));

        // Check WAIT limit
        long usedWaits = waitRequestRepository
                .countByAuctionPlayerIdAndTeamIdAndAdminWaitFalse(auctionPlayerId, teamId);

        if (usedWaits >= MAX_TEAM_WAITS_PER_PLAYER) {
            throw new DomainException(
                    "Team has already used all " + MAX_TEAM_WAITS_PER_PLAYER + " WAITs for this player"
            );
        }

        // Extend timer
        auctionPlayer.extendTimer(WAIT_EXTEND_SECONDS);
        auctionPlayerRepository.save(auctionPlayer);

        // Record the WAIT request
        WaitRequest waitRequest = WaitRequest.teamWait(auctionPlayer, team);
        waitRequestRepository.save(waitRequest);

        System.out.println("[WAIT] Team " + team.getName() +
                " used WAIT on player " + auctionPlayerId +
                " (" + (usedWaits + 1) + "/" + MAX_TEAM_WAITS_PER_PLAYER + ")");

        return waitRequest;
    }

    /**
     * Admin pauses the timer for a LIVE auction player.
     *
     * Creates a WaitRequest record and calls adminPause() on the entity.
     */
    @Transactional
    public AuctionPlayer adminPause(Long auctionPlayerId) {

        AuctionPlayer auctionPlayer = auctionPlayerRepository.findById(auctionPlayerId)
                .orElseThrow(() -> new DomainException("AuctionPlayer not found"));

        // Domain method handles all validation (LIVE check, already paused check)
        auctionPlayer.adminPause();
        auctionPlayerRepository.save(auctionPlayer);

        // Record the admin WAIT for audit
        WaitRequest waitRequest = WaitRequest.adminWait(auctionPlayer);
        waitRequestRepository.save(waitRequest);

        System.out.println("[ADMIN PAUSE] Player " + auctionPlayerId + " timer frozen");

        return auctionPlayer;
    }

    /**
     * Admin resumes the timer for a paused auction player.
     *
     * Restores timerEndAt from the saved remaining time.
     */
    @Transactional
    public AuctionPlayer adminResume(Long auctionPlayerId) {

        AuctionPlayer auctionPlayer = auctionPlayerRepository.findById(auctionPlayerId)
                .orElseThrow(() -> new DomainException("AuctionPlayer not found"));

        // Domain method handles validation (not paused check)
        auctionPlayer.adminResume();
        auctionPlayerRepository.save(auctionPlayer);

        System.out.println("[ADMIN RESUME] Player " + auctionPlayerId + " timer resumed");

        return auctionPlayer;
    }

    /**
     * Get how many WAITs a team has remaining for a specific auction player.
     */
    public long getTeamWaitCount(Long auctionPlayerId, Long teamId) {
        long used = waitRequestRepository
                .countByAuctionPlayerIdAndTeamIdAndAdminWaitFalse(auctionPlayerId, teamId);
        return MAX_TEAM_WAITS_PER_PLAYER - used;
    }
}
