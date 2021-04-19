package com.codeweb.salvo.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Salvo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;
    private int turnNumber;

    @ElementCollection
    @Column(name = "salvoLocations")
    private List<String> salvoLocations = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    public Salvo() {
        salvoLocations = new ArrayList<>();
    }

    public Salvo(int turnNumber, GamePlayer gamePlayer, List<String> salvoLocations) {
        this.gamePlayer = gamePlayer;
        this.turnNumber = turnNumber;
        this.salvoLocations = salvoLocations;
    }

    public Long getId() {
        return id;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public void setTurnNumber(int turnNumber) {
        this.turnNumber = turnNumber;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public List<String> getSalvoLocations() {
        return salvoLocations;
    }

    public void setSalvoLocations(List<String> salvoLocations) {
        this.salvoLocations = salvoLocations;
    }

    public Map<String, Object> makeSalvoDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("turn", this.turnNumber);
        dto.put("player", getGamePlayer().getPlayer().getId());
        dto.put("locations", this.salvoLocations);
        return dto;
    }


}
