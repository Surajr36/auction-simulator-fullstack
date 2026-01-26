package com.auction.backend.graphql;

import com.auction.backend.domain.Bid;
import com.auction.backend.repository.BidRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class BidQueryResolver {

    private final BidRepository bidRepository;

    public BidQueryResolver(BidRepository bidRepository) {
        this.bidRepository = bidRepository;
    }

    @QueryMapping
    public List<Bid> bids(@Argument Long auctionPlayerId) {
        return bidRepository.findByAuctionPlayerIdOrderByCreatedAtAsc(auctionPlayerId);
    }
}
