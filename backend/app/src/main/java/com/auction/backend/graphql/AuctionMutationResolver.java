package com.auction.backend.graphql;

import com.auction.backend.domain.Auction;
import com.auction.backend.service.AuctionService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class AuctionMutationResolver {

    private final AuctionService auctionService;

    public AuctionMutationResolver(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @MutationMapping
    public Auction createAuction() {
        return auctionService.createAuction();
    }

    @MutationMapping
    public Auction startAuction(@Argument Long auctionId) {
        return auctionService.startAuction(auctionId);
    }
}
