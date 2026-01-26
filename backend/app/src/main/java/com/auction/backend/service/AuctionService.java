package com.auction.backend.service;

import com.auction.backend.domain.Auction;
import com.auction.backend.domain.AuctionStatus;
import com.auction.backend.exception.DomainException;
import com.auction.backend.repository.AuctionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuctionService {

    private final AuctionRepository auctionRepository;

    public AuctionService(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }

    /**
     * Create a new auction.
     * An auction always starts in CREATED state.
     */
    @Transactional
    public Auction createAuction() {
        Auction auction = new Auction();
        return auctionRepository.save(auction);
    }

    /**
     * Start an auction.
     * Only CREATED auctions can be started.
     */
    @Transactional
    public Auction startAuction(Long auctionId) {

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new DomainException("Auction not found"));

        if (auction.getStatus() != AuctionStatus.CREATED) {
            throw new DomainException("Only CREATED auctions can be started");
        }

        auction.start();
        return auction;
    }
}