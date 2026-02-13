package com.auction.backend.graphql;

import com.auction.backend.service.WaitService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
public class WaitQueryResolver {

    private final WaitService waitService;

    public WaitQueryResolver(WaitService waitService) {
        this.waitService = waitService;
    }

    /**
     * Returns how many WAITs a team has remaining for a specific auction player.
     * Any authenticated user can query this.
     */
    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public int teamWaitCount(@Argument Long auctionPlayerId, @Argument Long teamId) {
        return (int) waitService.getTeamWaitCount(auctionPlayerId, teamId);
    }
}
