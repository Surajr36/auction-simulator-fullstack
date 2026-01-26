package com.auction.backend.graphql;

import com.auction.backend.domain.Auction;
import com.auction.backend.repository.AuctionRepository;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class AuctionQueryResolver {

    private final AuctionRepository auctionRepository;

    public AuctionQueryResolver(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }

    @QueryMapping
    public List<Auction> auctions() {
        return auctionRepository.findAll();
    }
}
