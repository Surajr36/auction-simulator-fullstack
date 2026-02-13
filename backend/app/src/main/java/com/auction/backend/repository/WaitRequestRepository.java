package com.auction.backend.repository;

import com.auction.backend.domain.WaitRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WaitRequestRepository extends JpaRepository<WaitRequest, Long> {

    /**
     * Count team WAITs for a specific team on a specific auction player.
     * Used to enforce the 2-WAIT limit per team per player.
     * Only counts non-admin WAITs (adminWait = false).
     */
    long countByAuctionPlayerIdAndTeamIdAndAdminWaitFalse(Long auctionPlayerId, Long teamId);

    /**
     * Find all WAIT requests for a given auction player.
     * Useful for audit and display.
     */
    List<WaitRequest> findByAuctionPlayerIdOrderByCreatedAtAsc(Long auctionPlayerId);
}
