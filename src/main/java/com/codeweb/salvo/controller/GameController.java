package com.codeweb.salvo.controller;

import com.codeweb.salvo.model.Game;
import com.codeweb.salvo.model.GamePlayer;
import com.codeweb.salvo.model.Player;
import com.codeweb.salvo.repositories.GamePlayerRepository;
import com.codeweb.salvo.repositories.GameRepository;
import com.codeweb.salvo.repositories.PlayerRepository;
import com.codeweb.salvo.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class GameController {

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private GamePlayerRepository gamePlayerRepository;


    @RequestMapping("/games")
    public Map<String, Object> getAllGames(Authentication authentication) {
        Map<String, Object> map = new HashMap<>();
        if (!Util.isGuest(authentication)) {
            map.put("player", playerRepository.findByEmail(authentication.getName()));

        } else {
            map.put("player", "Guest");
        }
        map.put("games", gameRepository.findAll().stream().map(Game::makeGameDTO).collect(Collectors.toList()));
        return map;
    }

    // ANDA
    @PostMapping("/games")
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication) {
        ResponseEntity<Map<String, Object>> response;
        if (Util.isGuest(authentication)) {
            response = new ResponseEntity<>(Util.makeMap("error", "player is unauthorized"), HttpStatus.UNAUTHORIZED);
        } else {
            Game game = gameRepository.save(new Game(LocalDateTime.now()));
            Player player = playerRepository.findByEmail(authentication.getName());
            GamePlayer gamePlayer = gamePlayerRepository.save(new GamePlayer(game, playerRepository.save(player)));
            response = new ResponseEntity<>(Util.makeMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);
        }
        return response;
    }

    @PostMapping("/game/{gameId}/players")
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable Long gameId, Authentication authentication) {
        ResponseEntity<Map<String, Object>> response;
        Optional<Game> game = gameRepository.findById(gameId);
        if (Util.isGuest(authentication)) {
            response = new ResponseEntity<>(Util.makeMap("error", "player is unauthorized"), HttpStatus.UNAUTHORIZED);
        } else if (game.isEmpty()) {
            response = new ResponseEntity<>(Util.makeMap("error", "No such game"), HttpStatus.FORBIDDEN);
        } else if (game.get().getGamePlayers().size() > 1) {
            response = new ResponseEntity<>(Util.makeMap("error", "Game is full"), HttpStatus.FORBIDDEN);
        } else {
            Player player = playerRepository.findByEmail(authentication.getName());
            GamePlayer gamePlayer = gamePlayerRepository.save(new GamePlayer(game.get(), player));
            response = new ResponseEntity<>(Util.makeMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);
        }
        return response;
    }


}
