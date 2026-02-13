package com.auction.backend.graphql.input;

public class UseWaitInput {

    private Long auctionPlayerId;
    private Long teamId;

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
}
