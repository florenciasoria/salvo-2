package com.codeweb.salvo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Game {

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private final Set<GamePlayer> gamePlayers = new HashSet<>();
    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private final Set<Score> scores = new HashSet<>();
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private LocalDateTime date;

    public Game() {
    }

    public Game(LocalDateTime date) {
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @JsonIgnore
    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    @JsonIgnore
    public Set<Score> getScores() {
        return scores;
    }


    public Map<String, Object> makeGameDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.getId());
        dto.put("created", this.getDate());
        dto.put("gamePlayers", gamePlayers.stream().map(GamePlayer::makeGamePlayerDTO).collect(Collectors.toList()));
        dto.put("scores", scores.stream().map(score -> score.makeScoreDTO()).collect(Collectors.toList()));
        return dto;
    }

}
