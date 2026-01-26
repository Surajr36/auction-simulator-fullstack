package com.auction.backend.graphql;

import com.auction.backend.domain.AuctionPlayer;
import com.auction.backend.repository.AuctionPlayerRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class AuctionPlayerQueryResolver {

    private final AuctionPlayerRepository auctionPlayerRepository;

    public AuctionPlayerQueryResolver(AuctionPlayerRepository auctionPlayerRepository) {
        this.auctionPlayerRepository = auctionPlayerRepository;
    }

    @QueryMapping
    public List<AuctionPlayer> auctionPlayers(@Argument Long auctionId) {
        return auctionPlayerRepository.findByAuctionId(auctionId);
    }
}
