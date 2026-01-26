package com.auction.backend.graphql;

import com.auction.backend.domain.Team;
import com.auction.backend.service.TeamService;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class TeamQueryResolver {

    private final TeamService teamService;

    public TeamQueryResolver(TeamService teamService) {
        this.teamService = teamService;
    }

    @QueryMapping
    public List<Team> teams() {
        return teamService.getAllTeams();
    }
}
