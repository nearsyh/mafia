package com.nearsyh.mafia.service.impl;

import com.nearsyh.mafia.protos.Game;
import com.nearsyh.mafia.service.GameService;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class InMemoryGameService implements GameService {

    private static final Map<String, Game> GAME_MAP = new HashMap<>();
    private static final LinkedList<String> GAME_IDS = new LinkedList<>();

    @Override
    public Mono<Game> getGame(String gameId) {
        return Mono.justOrEmpty(GAME_MAP.get(gameId));
    }

    @Override
    public Mono<Game> createGame(Game game) {
        String gameId = UUID.randomUUID().toString();
        game = game.toBuilder().setId(gameId).build();
        GAME_IDS.addFirst(gameId);
        GAME_MAP.put(gameId, game);
        return Mono.justOrEmpty(game);
    }

    @Override
    public Mono<Game> updateGame(Game game) {
        GAME_MAP.put(game.getId(), game);
        return Mono.justOrEmpty(game);
    }

    @Override
    public Mono<LinkedHashMap<String, Long>> getRecentGames(int count) {
        return Mono.just(
            GAME_IDS.subList(0, Math.min(count, GAME_IDS.size()))
        .stream()
        .collect(Collectors.toMap(
            Function.identity(),
            id -> GAME_MAP.get(id).getStartTimestamp(),
            (l, r) -> r,
            LinkedHashMap::new)));
    }
}
