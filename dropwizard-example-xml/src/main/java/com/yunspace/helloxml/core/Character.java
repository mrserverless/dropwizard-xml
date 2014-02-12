package com.yunspace.helloxml.core;

import javax.persistence.*;

@Entity
@Table(name = "character")
public class Character {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "playerId", table = )
    private Player playerOwner;

    public Character() {
        // Jackson deserialization
    }

    public Character(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Player getPlayerOwner() {
        return playerOwner;
    }

    public void setPlayerOwner(Player playerOwner) {
        this.playerOwner = playerOwner;
    }
}
