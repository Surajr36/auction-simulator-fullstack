package com.auction.backend.graphql.input;

import com.auction.backend.domain.PlayerCategory;

public class CreatePlayerInput {

    private String name;
    private PlayerCategory category;
    private Double basePrice;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PlayerCategory getCategory() {
        return category;
    }

    public void setCategory(PlayerCategory category) {
        this.category = category;
    }

    public Double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }
}
