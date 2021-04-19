package com.codeweb.salvo.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String type;

    @ElementCollection
    @Column(name = "locations")
    private List<String> locations = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    public Ship() {
        this.locations = new ArrayList<>();
        this.type = "UNDEFINED";
    }

    public Ship(String type, GamePlayer gamePlayer, List<String> locations) {
        this.type = type;
        this.gamePlayer = gamePlayer;
        this.locations = locations;

    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gp) {
        this.gamePlayer = gp;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setShipLocation(List<String> locations) {
        this.locations = locations;
    }


    public Map<String, Object> makeShipDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("type", this.getType());
        dto.put("locations", this.getLocations());
        return dto;
    }
}
