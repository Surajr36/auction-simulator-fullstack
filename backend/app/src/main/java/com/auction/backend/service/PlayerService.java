package com.auction.backend.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auction.backend.domain.Player;
import com.auction.backend.domain.PlayerCategory;
import com.auction.backend.exception.DomainException;
import com.auction.backend.repository.PlayerRepository;


@Service
public class PlayerService {
    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository){
        this.playerRepository=playerRepository;
    }

    @Transactional
    public Player createPlayer(String name, PlayerCategory category, BigDecimal basePrice)
    {
        if (name == null || name.trim().isEmpty()) {
            throw new DomainException("Player name must not be empty");
        }
        if (category == null) {
            throw new DomainException("Player category must be specified");
        }
        if (basePrice == null || basePrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("Base price must be greater than zero");
        }

        Player player = new Player(name.trim(), category, basePrice);
        return playerRepository.save(player);
    }

    @Transactional(readOnly = true)
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }
}
