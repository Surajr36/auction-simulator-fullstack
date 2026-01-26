package com.auction.backend.graphql.input;

public class CreateTeamInput {

    private String name;
    private Double purse;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPurse() {
        return purse;
    }

    public void setPurse(Double purse) {
        this.purse = purse;
    }
}
