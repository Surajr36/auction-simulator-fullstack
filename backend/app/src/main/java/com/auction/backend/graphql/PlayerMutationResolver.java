package com.auction.backend.graphql;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import com.auction.backend.domain.Player;
import com.auction.backend.domain.PlayerCategory;
import com.auction.backend.service.PlayerService;

@Controller
public class PlayerMutationResolver {
    private final PlayerService playerService;

    public PlayerMutationResolver(PlayerService playerService){
        this.playerService=playerService;
    }

    @MutationMapping
    public Player createPlayer(@Argument Map<String, Object> input){
        String name=(String) input.get("name");
        String category = (String) input.get("category");
        Double basePrice = (Double) input.get("basePrice");

        return playerService.createPlayer(
                name,
                PlayerCategory.valueOf(category),
                BigDecimal.valueOf(basePrice)
        );
    }
    
}
