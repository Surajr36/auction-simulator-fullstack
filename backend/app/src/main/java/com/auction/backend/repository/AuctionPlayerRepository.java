package com.auction.backend.repository;

import com.auction.backend.domain.AuctionPlayer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuctionPlayerRepository extends JpaRepository<AuctionPlayer, Long> {

    List<AuctionPlayer> findByAuctionId(Long auctionId);
}
