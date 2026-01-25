package com.auction.backend.graphql;

import com.auction.backend.domain.Player;
import com.auction.backend.service.PlayerService;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class PlayerQueryResolver {

    private final PlayerService playerService;

    public PlayerQueryResolver(PlayerService playerService) {
        this.playerService = playerService;
    }

    @QueryMapping
    public List<Player> players() {
        return playerService.getAllPlayers();
    }
}
