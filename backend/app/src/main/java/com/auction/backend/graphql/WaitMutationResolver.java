package com.auction.backend.graphql;

import com.auction.backend.domain.AuctionPlayer;
import com.auction.backend.domain.WaitRequest;
import com.auction.backend.graphql.input.UseWaitInput;
import com.auction.backend.service.WaitService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
public class WaitMutationResolver {

    private final WaitService waitService;

    public WaitMutationResolver(WaitService waitService) {
        this.waitService = waitService;
    }

    /**
     * Team uses a WAIT — extends timer by 30 seconds.
     * Requires TEAM_USER role (teams call WAIT on their own behalf).
     */
    @MutationMapping
    @PreAuthorize("hasRole('TEAM_USER')")
    public WaitRequest useWait(@Argument UseWaitInput input) {
        return waitService.useTeamWait(
                input.getAuctionPlayerId(),
                input.getTeamId()
        );
    }

    /**
     * Admin pauses the timer.
     * Requires ADMIN role.
     */
    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public AuctionPlayer adminPause(@Argument Long auctionPlayerId) {
        return waitService.adminPause(auctionPlayerId);
    }

    /**
     * Admin resumes the timer.
     * Requires ADMIN role.
     */
    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public AuctionPlayer adminResume(@Argument Long auctionPlayerId) {
        return waitService.adminResume(auctionPlayerId);
    }
}
