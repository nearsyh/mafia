package com.nearsyh.mafia.service.impl;

import com.nearsyh.mafia.protos.Game;
import com.nearsyh.mafia.service.GameService;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class InMemoryGameService implements GameService {

    private static final Map<String, Game> GAME_MAP = new HashMap<>();

    @Override
    public Mono<Game> getGame(String gameId) {
        return Mono.justOrEmpty(GAME_MAP.get(gameId));
    }

    @Override
    public Mono<Game> createGame(Game game) {
        String gameId = UUID.randomUUID().toString();
        game = game.toBuilder().setId(gameId).build();
        GAME_MAP.put(gameId, game);
        return Mono.justOrEmpty(game);
    }

    @Override
    public Mono<Game> updateGame(Game game) {
        return Mono.justOrEmpty(GAME_MAP.put(game.getId(), game));
    }
}
