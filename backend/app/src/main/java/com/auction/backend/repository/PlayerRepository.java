package com.auction.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auction.backend.domain.Player;

public interface PlayerRepository extends JpaRepository<Player,Long> {
    
}
