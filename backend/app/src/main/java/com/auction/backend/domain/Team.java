package com.auction.backend.domain;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.*;

@Entity
@Table(name="teams",uniqueConstraints={@UniqueConstraint(columnNames="name")})
public class Team {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String name;

    @Column(nullable=false, precision=10,scale=2)
    private BigDecimal purse;

    @Column(nullable=false)
    private int maxSquadSize;

    @Column(nullable=false, updatable=false)
    private Instant createdAt;

    protected Team(){}

    public Team(String name, BigDecimal purse,int maxSquadSize){
        this.name = name;
        this.purse = purse;
        this.maxSquadSize = maxSquadSize;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPurse() {
        return purse;
    }

    public int getMaxSquadSize() {
        return maxSquadSize;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
