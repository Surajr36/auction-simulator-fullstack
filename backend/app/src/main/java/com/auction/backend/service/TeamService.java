package com.auction.backend.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auction.backend.domain.Team;
import com.auction.backend.exception.DomainException;
import com.auction.backend.repository.TeamRepository;

@Service
public class TeamService {
    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Transactional
    public Team createTeam(String name, BigDecimal purse) {

        if (name == null || name.trim().isEmpty()) {
            throw new DomainException("Team name must not be empty");
        }

        if (purse == null || purse.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("Purse must be greater than zero");
        }

        if (teamRepository.findByName(name.trim()).isPresent()) {
            throw new DomainException("Team with this name already exists");
        }

        return teamRepository.save(
                new Team(name.trim(), purse, 25)
        );
    }

    @Transactional(readOnly = true)
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }
}
