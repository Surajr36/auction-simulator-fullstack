package com.auction.backend.repository;

import com.auction.backend.domain.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BidRepository extends JpaRepository<Bid, Long> {

    List<Bid> findByAuctionPlayerIdOrderByCreatedAtAsc(Long auctionPlayerId);
}
