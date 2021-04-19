package com.codeweb.salvo.model;

import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Entity
public class GamePlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private LocalDateTime joinDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Ship> ships;

    @OrderBy
    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    private Set<Salvo> salvoes;
    // el set es desordenado

    public GamePlayer() {
        this.joinDate = LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires"));
        this.ships = new HashSet<>();
        this.salvoes = new HashSet<>();
    }

    public GamePlayer(Game game, Player player) {
        this.joinDate = LocalDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires"));
        this.game = game;
        this.player = player;
        this.ships = new HashSet<>();
        this.salvoes = new HashSet<>();
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDateTime joinDate) {
        this.joinDate = joinDate;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @JsonIgnore
    public Set<Ship> getShips() {
        return ships;
    }

    public void setShips(Set<Ship> ships) {
        this.ships = ships;
    }

    public Set<Salvo> getSalvoes() {
        return salvoes;
    }

    public void setSalvoes(Set<Salvo> salvoes) {
        this.salvoes = salvoes;
    }

    public Score getScore() {
        return player.getScore(this.game);
    }

    public Optional<GamePlayer> getOpponent() {
        return this.getGame().getGamePlayers().stream().filter(gamePlayer -> gamePlayer.getId() != this.getId()).findFirst();
        // or else new gp
    }

    public void addShip(Set<Ship> ships) {
        ships.forEach(ship -> {
            ship.setGamePlayer(this);
            this.ships.add(ship);
        });
    }

    public Map<String, Object> makeGamePlayerDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.getId());
        dto.put("player", player.makePlayerDTO());
        return dto;
    }


}
