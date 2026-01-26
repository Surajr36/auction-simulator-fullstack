package com.auction.backend.graphql;

import com.auction.backend.domain.AuctionPlayer;
import com.auction.backend.graphql.input.AddPlayerToAuctionInput;
import com.auction.backend.service.AuctionPlayerService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;

@Controller
public class AuctionPlayerMutationResolver {

    private final AuctionPlayerService auctionPlayerService;

    public AuctionPlayerMutationResolver(AuctionPlayerService auctionPlayerService) {
        this.auctionPlayerService = auctionPlayerService;
    }

    @MutationMapping
    public AuctionPlayer addPlayerToAuction(
            @Argument AddPlayerToAuctionInput input
    ) {
        return auctionPlayerService.addPlayerToAuction(
                input.getAuctionId(),
                input.getPlayerId(),
                BigDecimal.valueOf(input.getBasePrice())
        );
    }

    @MutationMapping
    public AuctionPlayer startAuctionPlayer(@Argument Long auctionPlayerId) {
        return auctionPlayerService.startAuctionPlayer(auctionPlayerId);
    }
}
