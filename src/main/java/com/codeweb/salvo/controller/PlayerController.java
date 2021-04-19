package com.codeweb.salvo.controller;

import com.codeweb.salvo.model.Player;
import com.codeweb.salvo.repositories.PlayerRepository;
import com.codeweb.salvo.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class PlayerController {

    /*
    //task 11
    private String getGameState(GamePlayer self) {






                if (self.getSalvoes().size() < opponent.get().getSalvoes().size()) {
                    return  "PLAY";
                }

                if(((self.getId() < opponent.get().getId()))){
                    return  "PLAY";
                }
                if (self.getSalvoes().size() > opponent.get().getSalvoes().size()) {
                    return  "WAIT";
                }

                if(((self.getId() > opponent.get().getId()))){
                    return  "WAIT";
                }

            }
        }

        return "UNDEFINED";
    }

     */


    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private PlayerRepository playerRepository;


    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> register(@RequestParam String email, @RequestParam String password) {
        ResponseEntity<Map<String, Object>> response;

        if (email.isEmpty() || password.isEmpty()) {
            response = new ResponseEntity<>(Util.makeMap("error", "Missing data"), HttpStatus.FORBIDDEN);
        } else if (playerRepository.findByEmail(email) != null) {
            response = new ResponseEntity<>(Util.makeMap("error", "Name already in use"), HttpStatus.FORBIDDEN);
        } else {
            playerRepository.save(new Player(email, passwordEncoder.encode(password)));
            response = new ResponseEntity<>(Util.makeMap("OK", "ok"), HttpStatus.CREATED);
        }
        return response;
    }


}
