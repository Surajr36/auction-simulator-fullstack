package com.auction.backend.graphql;

import com.auction.backend.domain.Bid;
import com.auction.backend.graphql.input.PlaceBidInput;
import com.auction.backend.service.BidService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;

@Controller
public class BidMutationResolver {

    private final BidService bidService;

    public BidMutationResolver(BidService bidService) {
        this.bidService = bidService;
    }

    @MutationMapping
    @PreAuthorize("hasRole('TEAM_USER')")
    public Bid placeBid(@Argument PlaceBidInput input) {
        return bidService.placeBid(
                input.getAuctionPlayerId(),
                input.getTeamId(),
                BigDecimal.valueOf(input.getAmount())
        );
    }
}
