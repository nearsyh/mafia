package com.nearsyh.mafia.handlers;

import com.nearsyh.mafia.characters.AbstractCharacter;
import com.nearsyh.mafia.common.GameAccessor;
import com.nearsyh.mafia.common.GameConstructor;
import com.nearsyh.mafia.protos.CharacterType;
import com.nearsyh.mafia.protos.Event;
import com.nearsyh.mafia.protos.Game;
import com.nearsyh.mafia.service.GameService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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
    public Mono<Game> createGame(@RequestBody Map<String, Integer> characterCounts) {
        return gameService.createGame(GameConstructor.constructGame(characterCounts));
    }

    @PostMapping("/games/{game_id}")
    public Mono<Game> updateGame(
        @PathVariable("game_id") String gameId, @RequestBody Event event) {
        return gameService.getGame(gameId)
            .map(game -> AbstractCharacter.handle(game, event))
            .flatMap(gameService::updateGame);
    }

    @PostMapping("/games/{game_id}/swap")
    public Mono<Game> swap(
        @PathVariable("game_id") String gameId, @RequestParam("player_index") int playerIndex,
        @RequestBody Map<String, Integer> any) {
        return gameService.getGame(gameId)
            .map(game -> GameAccessor.swapPlayer(game, playerIndex))
            .flatMap(gameService::updateGame);
    }

    @GetMapping("/games/support_characters")
    public Mono<List<String>> allSupportedCharacters() {
        return Mono.just(AbstractCharacter.allSupportedCharacterTypes().stream()
            .filter(characterType ->
                characterType != CharacterType.CHARACTER_TYPE_UNSPECIFIED
             && characterType != CharacterType.UNRECOGNIZED)
            .map(CharacterType::name)
            .collect(Collectors.toList()));
    }

}
