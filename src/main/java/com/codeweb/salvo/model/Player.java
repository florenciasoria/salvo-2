package com.codeweb.salvo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Entity
public class Player {

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private final Set<GamePlayer> gamePlayers = new HashSet<>();
    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private final Set<Score> scores = new HashSet<>();
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String email;
    private String password;

    public Player() {
    }

    public Player(String userName, String password) {
        this.email = userName;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    @JsonIgnore
    public List<Player> getPlayers() {
        return gamePlayers.stream().map(sub -> sub.getPlayer()).collect(toList());
    }

    // filter: filtrar
    public Score getScore(Game game) {
        return scores.stream().filter(score -> score.getGame().getId() == game.getId()).findFirst().orElse(null);
    }

    public Map<String, Object> makePlayerDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", getId());
        dto.put("email", getEmail());
        return dto;
    }
}
