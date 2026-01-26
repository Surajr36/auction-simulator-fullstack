package com.auction.backend.graphql;

import com.auction.backend.domain.Team;
import com.auction.backend.graphql.input.CreateTeamInput;
import com.auction.backend.service.TeamService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;

@Controller
public class TeamMutationResolver {

    private final TeamService teamService;

    public TeamMutationResolver(TeamService teamService) {
        this.teamService = teamService;
    }

    @MutationMapping
    public Team createTeam(@Argument CreateTeamInput input) {
        return teamService.createTeam(
                input.getName(),
                BigDecimal.valueOf(input.getPurse())
        );
    }
}
