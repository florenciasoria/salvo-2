package com.codeweb.salvo.controller;

import com.codeweb.salvo.model.GamePlayer;
import com.codeweb.salvo.model.Salvo;
import com.codeweb.salvo.repositories.GamePlayerRepository;
import com.codeweb.salvo.repositories.SalvoRepository;
import com.codeweb.salvo.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    SalvoRepository salvoRepository;

    @Autowired
    GamePlayerRepository gamePlayerRepository;


    @PostMapping("/games/players/{gamePlayerId}/salvoes")
    public ResponseEntity<Map<String, Object>> createSalvos(@PathVariable Long gamePlayerId, @RequestBody Salvo salvo, Authentication authentication
    ) {
        ResponseEntity<Map<String, Object>> response;
        Optional<GamePlayer> gamePlayer = gamePlayerRepository.findById(gamePlayerId);
        Optional<GamePlayer> opponent = gamePlayer.get().getOpponent();
        if (Util.isGuest(authentication)) {
            response = new ResponseEntity<>(Util.makeMap("error", "player is unauthorized"), HttpStatus.UNAUTHORIZED);
        } else if (gamePlayer.isEmpty()) {
            response = new ResponseEntity<>(Util.makeMap("error", "gamePlayer doesn't exist"), HttpStatus.UNAUTHORIZED);
        } else if (!authentication.getName().equals(gamePlayer.get().getPlayer().getEmail())) {
            response = new ResponseEntity<>(Util.makeMap("error", "It's not a current player"), HttpStatus.UNAUTHORIZED);
            // es null?
        } else if (opponent.isPresent()) {
            if (gamePlayer.get().getSalvoes().size() <= opponent.get().getSalvoes().size()) {
                salvo.setTurnNumber(gamePlayer.get().getSalvoes().size() + 1);
                salvo.setGamePlayer(gamePlayer.get());
                salvoRepository.save(salvo);
                // gamePlayer.get().addSalvo(salvo);--> not working
                response = new ResponseEntity<>(Util.makeMap("OK", "salvoes saved"), HttpStatus.CREATED);

            } else {
                response = new ResponseEntity<>(Util.makeMap("error", "turn is already in use"), HttpStatus.FORBIDDEN);
            }

        } else {
            response = new ResponseEntity<>(Util.makeMap("error", "opponent doesn't exist"), HttpStatus.FORBIDDEN);
        }

        return response;
    }
}