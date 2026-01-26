package com.auction.backend.service;

import com.auction.backend.domain.*;
import com.auction.backend.exception.DomainException;
import com.auction.backend.repository.AuctionPlayerRepository;
import com.auction.backend.repository.BidRepository;
import com.auction.backend.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class BidService {

    private final AuctionPlayerRepository auctionPlayerRepository;
    private final TeamRepository teamRepository;
    private final BidRepository bidRepository;

    public BidService(
            AuctionPlayerRepository auctionPlayerRepository,
            TeamRepository teamRepository,
            BidRepository bidRepository
    ) {
        this.auctionPlayerRepository = auctionPlayerRepository;
        this.teamRepository = teamRepository;
        this.bidRepository = bidRepository;
    }

    @Transactional
    public Bid placeBid(Long auctionPlayerId, Long teamId, BigDecimal amount) {

        AuctionPlayer auctionPlayer = auctionPlayerRepository.findById(auctionPlayerId)
                .orElseThrow(() -> new DomainException("AuctionPlayer not found"));

        if (auctionPlayer.getStatus() != AuctionPlayerStatus.LIVE) {
            throw new DomainException("Bidding is not open for this player");
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new DomainException("Team not found"));

        BigDecimal currentPrice = auctionPlayer.getCurrentPrice();

        if (amount.compareTo(currentPrice) <= 0) {
            throw new DomainException("Bid must be higher than current price");
        }

        validateIncrement(currentPrice, amount);

        if (team.getPurse().compareTo(amount) < 0) {
            throw new DomainException("Insufficient purse for this bid");
        }

        // Create bid (immutable event)
        Bid bid = new Bid(auctionPlayer, team, amount);
        bidRepository.save(bid);

        // Update auction player state
        auctionPlayer.updateCurrentBid(team, amount);
        auctionPlayerRepository.save(auctionPlayer);

        return bid;
    }

    private void validateIncrement(BigDecimal currentPrice, BigDecimal amount) {

        BigDecimal increment = amount.subtract(currentPrice);

        BigDecimal expectedIncrement =
                currentPrice.compareTo(BigDecimal.valueOf(5)) < 0
                        ? BigDecimal.valueOf(0.2)
                        : BigDecimal.valueOf(0.5);

        if (increment.compareTo(expectedIncrement) < 0) {
            throw new DomainException(
                    "Minimum increment is " + expectedIncrement
            );
        }
    }
}
