package com.auction.backend.domain;

import java.math.BigDecimal;

import jakarta.persistence.*;

@Entity
@Table(name="players")
public class Player {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private PlayerCategory category;

    @Column(nullable=false, precision=10, scale=2)
    private BigDecimal basePrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private PlayerStatus status;

    protected Player(){

    }

    public Player(String name, PlayerCategory category, BigDecimal basePrice){
        this.name=name;
        this.category=category;
        this.basePrice=basePrice;
        this.status=PlayerStatus.AVAILABLE;
    }
    
    // Getters only for now (immutability-lite)
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public PlayerCategory getCategory() {
        return category;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public PlayerStatus getStatus() {
        return status;
    }

    public void markSold() {
        this.status = PlayerStatus.SOLD;
    }

    public void markUnsold() {
        this.status = PlayerStatus.UNSOLD;
    }
}
