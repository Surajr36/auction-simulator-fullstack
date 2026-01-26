package com.auction.backend.graphql;

import com.auction.backend.domain.Player;
import com.auction.backend.graphql.input.CreatePlayerInput;
import com.auction.backend.service.PlayerService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;

@Controller
public class PlayerMutationResolver {

    private final PlayerService playerService;

    public PlayerMutationResolver(PlayerService playerService) {
        this.playerService = playerService;
    }

    @MutationMapping
    public Player createPlayer(@Argument CreatePlayerInput input) {

        return playerService.createPlayer(
                input.getName(),
                input.getCategory(),
                BigDecimal.valueOf(input.getBasePrice())
        );
    }
}
