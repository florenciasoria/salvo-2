package com.codeweb.salvo.controller;

import com.codeweb.salvo.model.GamePlayer;
import com.codeweb.salvo.model.Ship;
import com.codeweb.salvo.repositories.GamePlayerRepository;
import com.codeweb.salvo.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class ShipController {

    @Autowired
    private GamePlayerRepository gamePlayerRepository;


    @PostMapping("/games/players/{gamePlayerId}/ships")
    public ResponseEntity<Map<String, Object>> createShips(@PathVariable Long gamePlayerId, @RequestBody Set<Ship> ships, Authentication authentication) {
        ResponseEntity<Map<String, Object>> response;
        Optional<GamePlayer> gamePlayer = gamePlayerRepository.findById(gamePlayerId);
        if (Util.isGuest(authentication)) {
            response = new ResponseEntity<>(Util.makeMap("error", "player is unauthorized"), HttpStatus.UNAUTHORIZED);
        } else if (gamePlayer.isEmpty()) {
            response = new ResponseEntity<>(Util.makeMap("error", "gamePlayer doesn't exist"), HttpStatus.UNAUTHORIZED);
        } else if (!authentication.getName().equals(gamePlayer.get().getPlayer().getEmail())) {
            response = new ResponseEntity<>(Util.makeMap("error", "It's not a current player"), HttpStatus.UNAUTHORIZED);
        } else if (gamePlayer.get().getShips().size() > 0) {
            response = new ResponseEntity<>(Util.makeMap("error", "shipsLocations are full"), HttpStatus.FORBIDDEN);
        } else {
            if (ships.size() > 0) {
                gamePlayer.get().addShip(ships);
                gamePlayerRepository.save(gamePlayer.get());
                response = new ResponseEntity<>(Util.makeMap("OK", "ships saved"), HttpStatus.CREATED);
            } else {
                response = new ResponseEntity<>(Util.makeMap("error", "ships are empty"), HttpStatus.FORBIDDEN);
            }
        }
        return response;
    }
}
