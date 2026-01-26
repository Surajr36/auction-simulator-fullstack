package com.auction.backend.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auction.backend.domain.Auction;
import com.auction.backend.domain.AuctionPlayer;
import com.auction.backend.domain.AuctionPlayerStatus;
import com.auction.backend.domain.AuctionStatus;
import com.auction.backend.domain.Player;
import com.auction.backend.exception.DomainException;
import com.auction.backend.repository.AuctionPlayerRepository;
import com.auction.backend.repository.AuctionRepository;
import com.auction.backend.repository.PlayerRepository;

@Service
public class AuctionPlayerService {
    private final AuctionRepository auctionRepository;
    private final PlayerRepository playerRepository;
    private final AuctionPlayerRepository auctionPlayerRepository;

    public AuctionPlayerService(
            AuctionRepository auctionRepository,
            PlayerRepository playerRepository,
            AuctionPlayerRepository auctionPlayerRepository
    ) {
        this.auctionRepository = auctionRepository;
        this.playerRepository = playerRepository;
        this.auctionPlayerRepository = auctionPlayerRepository;
    }

    /**
     * Add a player to an auction (schedule them).
     */
    @Transactional
    public AuctionPlayer addPlayerToAuction(
            Long auctionId,
            Long playerId,
            BigDecimal basePrice
    ) {

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new DomainException("Auction not found"));

        if (auction.getStatus() != AuctionStatus.LIVE) {
            throw new DomainException("Players can only be added to a LIVE auction");
        }

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new DomainException("Player not found"));

        if (basePrice == null || basePrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("Base price must be greater than zero");
        }

        AuctionPlayer auctionPlayer =
                new AuctionPlayer(auction, player, basePrice);

        return auctionPlayerRepository.save(auctionPlayer);
    }

    /**
     * Start bidding for a specific auction player.
     * Enforces: only ONE LIVE AuctionPlayer per auction.
     */
    @Transactional
    public AuctionPlayer startAuctionPlayer(Long auctionPlayerId) {

        AuctionPlayer auctionPlayer = auctionPlayerRepository.findById(auctionPlayerId)
                .orElseThrow(() -> new DomainException("AuctionPlayer not found"));

        Auction auction = auctionPlayer.getAuction();

        if (auction.getStatus() != AuctionStatus.LIVE) {
            throw new DomainException("Auction is not LIVE");
        }

        // Enforce: only one LIVE AuctionPlayer per auction
        List<AuctionPlayer> auctionPlayers =
                auctionPlayerRepository.findByAuctionId(auction.getId());

        boolean anotherLiveExists = auctionPlayers.stream()
                .anyMatch(ap ->
                        ap.getStatus() == AuctionPlayerStatus.LIVE
                                && !ap.getId().equals(auctionPlayerId)
                );

        if (anotherLiveExists) {
            throw new DomainException("Another player is already being auctioned");
        }

        auctionPlayer.start();
        return auctionPlayer;
    }
}
