package com.codeweb.salvo.controller;

import com.codeweb.salvo.model.*;
import com.codeweb.salvo.repositories.GamePlayerRepository;
import com.codeweb.salvo.repositories.PlayerRepository;
import com.codeweb.salvo.repositories.ScoreRepository;
import com.codeweb.salvo.repositories.ShipRepository;
import com.codeweb.salvo.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api")
public class AppController {

    final static double GANO = 1.0;
    final static double EMPATO = 0.5;
    final static double PERDIO = 0.0;


    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private ShipRepository shipRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    @RequestMapping("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> getGameViewByGamePlayerID(@PathVariable Long gamePlayerId, Authentication authentication) {
        ResponseEntity<Map<String, Object>> response;
        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).orElse(null);
        Player player = playerRepository.findByEmail(authentication.getName());
        if (Util.isGuest(authentication)) {
            response = new ResponseEntity<>(Util.makeMap("error", "player is not allowed"), HttpStatus.UNAUTHORIZED);
        } else if (player == null) {
            response = new ResponseEntity<>(Util.makeMap("error", "player doesn't exist"), HttpStatus.UNAUTHORIZED);
        } else if (gamePlayer == null) {
            response = new ResponseEntity<>(Util.makeMap("error", "gamePlayer doesn't exist"), HttpStatus.NOT_ACCEPTABLE);
        } else if (gamePlayer.getPlayer().getId() != player.getId()) {
            return new ResponseEntity<>(Util.makeMap("error", "it,s not a current player"), HttpStatus.CONFLICT);
        } else {
            Map<String, Object> dto = new LinkedHashMap<>();
            Map<String, Object> hits = new LinkedHashMap<>();
            Optional<GamePlayer> opponent = gamePlayer.getOpponent();
            if (opponent.isPresent()) {
                hits.put("self", getHitsAndSinks(gamePlayer, opponent.get()));
                hits.put("opponent", getHitsAndSinks(opponent.get(), gamePlayer));
            } else {
                hits.put("self", new ArrayList<>());
                hits.put("opponent", new ArrayList<>());
            }
            dto.put("id", gamePlayer.getGame().getId());
            dto.put("created", gamePlayer.getGame().getDate());
            dto.put("gameState", getGameState(gamePlayer));

            dto.put("gamePlayers", gamePlayer.getGame().getGamePlayers()
                    .stream()
                    .map(gamePlayer1 -> gamePlayer1.makeGamePlayerDTO())
                    .collect(Collectors.toList()));
            dto.put("ships", gamePlayer.getShips()
                    .stream()
                    .map(ship -> ship.makeShipDTO())
                    .collect(Collectors.toList()));
            dto.put("salvoes", gamePlayer.getGame().getGamePlayers()
                    .stream()
                    .flatMap(gamePlayer1 -> gamePlayer1.getSalvoes()
                            .stream()
                            .map(salvo -> salvo.makeSalvoDTO()))
                    .collect(Collectors.toList()));
            dto.put("hits", hits);

            response = new ResponseEntity<>(dto, HttpStatus.OK);
        }
        return response;
    }

    //task10:
    private List<Map> getHitsAndSinks(GamePlayer self, GamePlayer opponent) {

        List<Map> hits = new ArrayList<>();

        //total damage
        int carrierHits = 0;
        int battleshipHits = 0;
        int submarineHits = 0;
        int destroyerHits = 0;
        int patrolboatHits = 0;

        //locations
        List<String> carrierLocations = findShipLocations(self, "carrier");
        List<String> battleshipLocations = findShipLocations(self, "battleship");
        List<String> submarineLocations = findShipLocations(self, "submarine");
        List<String> destroyerLocations = findShipLocations(self, "destroyer");
        List<String> patrolboatLocations = findShipLocations(self, "patrolboat");

        //cambiamos el foreach porque tiraba cualquier turno, porque set es desordenado
        // este for te ordena los turnos de un SET
        //  for (int i = 1; i <= self.getSalvoes().size(); i++) {
        //    int turn = i;
        //  Salvo salvo = self.getSalvoes().stream().filter(salvo1 -> salvo1.getTurnNumber() == turn).findFirst().orElse(null);
        // CAMBIAMOS EL SET DE SALVO A LIST

        for (Salvo salvo : opponent.getSalvoes()) {

            Map<String, Object> damagesPerTurn = new LinkedHashMap<>();
            Map<String, Object> hitsPerTurn = new LinkedHashMap<>();

            ArrayList<String> hitCellList = new ArrayList<>();

            // missed
            int missed = salvo.getSalvoLocations().size();

            //hits per turn
            int carrierTurn = 0;
            int battleshipTurn = 0;
            int submarineTurn = 0;
            int destroyerTurn = 0;
            int patrolboatTurn = 0;

            for (String location : salvo.getSalvoLocations()) {
                if (carrierLocations.contains(location)) {
                    carrierHits++;
                    carrierTurn++;
                    hitCellList.add(location);
                    missed--;
                }
                if (battleshipLocations.contains(location)) {
                    battleshipHits++;
                    battleshipTurn++;
                    hitCellList.add(location);
                    missed--;
                }
                if (submarineLocations.contains(location)) {
                    submarineHits++;
                    submarineTurn++;
                    hitCellList.add(location);
                    missed--;
                }
                if (destroyerLocations.contains(location)) {
                    destroyerHits++;
                    destroyerTurn++;
                    hitCellList.add(location);
                    missed--;
                }
                if ((patrolboatLocations.contains(location))) {
                    patrolboatHits++;
                    patrolboatTurn++;
                    hitCellList.add(location);
                    missed--;
                }
            }
            //damage per turn
            damagesPerTurn.put("carrierHits", carrierTurn);
            damagesPerTurn.put("battleshipHits", battleshipTurn);
            damagesPerTurn.put("submarineHits", submarineTurn);
            damagesPerTurn.put("destroyerHits", destroyerTurn);
            damagesPerTurn.put("patrolboatHits", patrolboatTurn);
            // total damage
            damagesPerTurn.put("carrier", carrierHits);
            damagesPerTurn.put("battleship", battleshipHits);
            damagesPerTurn.put("submarine", submarineHits);
            damagesPerTurn.put("destroyer", destroyerHits);
            damagesPerTurn.put("patrolboat", patrolboatHits);
            // DTO dentro de hits
            hitsPerTurn.put("turn", salvo.getTurnNumber());
            hitsPerTurn.put("missed", missed);
            hitsPerTurn.put("damages", damagesPerTurn);
            hitsPerTurn.put("hitLocations", hitCellList);
            //DTO hits: Lista
            hits.add(hitsPerTurn);

        }

        return hits;
    }

    private List<String> findShipLocations(GamePlayer gamePlayer, String type) {
        Optional<Ship> response;
        response = gamePlayer.getShips().stream().filter(ship -> ship.getType().equals(type)).findFirst();
        if (response.isEmpty()) {
            return new ArrayList<String>();
        }
        return response.get().getLocations();
    }


    private String getGameState(GamePlayer self) {

        if (self.getShips().size() == 0)
            return "PLACESHIPS";

        if (self.getGame().getGamePlayers().size() == 1)
            return "WAITINGFOROPP";

        if (self.getGame().getGamePlayers().size() == 2) {

            Optional<GamePlayer> opponent = self.getOpponent();

            if (self.getSalvoes().size() == opponent.get().getSalvoes().size()) {

                if (self.getSalvoes().size() > opponent.get().getSalvoes().size()) {
                    return "WAIT";
                }

                if (((self.getId() > opponent.get().getId()))) {
                    return "WAIT";
                }
                if (self.getSalvoes().size() < opponent.get().getSalvoes().size()) {
                    return "PLAY";
                }

                if (((self.getId() < opponent.get().getId()))) {
                    return "PLAY";
                } else if (self.getSalvoes().size() == opponent.get().getSalvoes().size()) {

                    boolean selfSunk = getIfAllSunk(self, opponent.get());
                    boolean opponentSunk = getIfAllSunk(opponent.get(), self);

                    if (selfSunk && opponentSunk) {
                        scoreRepository.save(new Score(self.getGame(), self.getPlayer(), 0.5, LocalDateTime.now()));
                        return "TIE";
                    }

                    if (self.getSalvoes().size() == opponent.get().getSalvoes().size()) {
                        if (!selfSunk && opponentSunk) {
                            scoreRepository.save(new Score(self.getGame(), self.getPlayer(), 1.0, LocalDateTime.now()));
                            return "WON";
                        }

                        if (selfSunk && !opponentSunk) {
                            scoreRepository.save(new Score(self.getGame(), self.getPlayer(), 0.0, LocalDateTime.now()));
                            return "LOST";
                        }
                    }

                }
            }

        }
        return "PLAY";
    }

    // task 11
    private Boolean getIfAllSunk(GamePlayer self, GamePlayer opponent) {

        if (!opponent.getShips().isEmpty() && !self.getSalvoes().isEmpty()) {
            return opponent.getSalvoes().stream().flatMap(salvo -> salvo.getSalvoLocations().stream()).collect(Collectors.toList()).containsAll(self.getShips().stream()
                    .flatMap(ship -> ship.getLocations().stream()).collect(Collectors.toList()));
        }
        return false;
    }


}


