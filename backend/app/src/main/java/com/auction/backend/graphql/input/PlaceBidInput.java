package com.auction.backend.graphql.input;

public class PlaceBidInput {

    private Long auctionPlayerId;
    private Long teamId;
    private Double amount;

    public Long getAuctionPlayerId() {
        return auctionPlayerId;
    }

    public void setAuctionPlayerId(Long auctionPlayerId) {
        this.auctionPlayerId = auctionPlayerId;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
