package com.nearsyh.mafia.handlers;

import com.nearsyh.mafia.characters.AbstractCharacter;
import com.nearsyh.mafia.protos.Event;
import com.nearsyh.mafia.protos.Game;
import com.nearsyh.mafia.service.GameService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class GameManagementHandler {

    private final GameService gameService;

    public GameManagementHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/games/{game_id}")
    public Mono<Game> getGame(@PathVariable("game_id") String gameId) {
        return gameService.getGame(gameId);
    }

    @PutMapping("/games")
    public Mono<Game> createGame() {
        return gameService.createGame();
    }

    @PostMapping("/games/{game_id}")
    public Mono<Game> updateGame(
        @PathVariable("game_id") String gameId, @RequestBody Event event) {
        return gameService.getGame(gameId)
            .map(game -> AbstractCharacter.getEventListeners(event.getEventType())
                .handle(game, event))
            .flatMap(gameService::updateGame);
    }

}
