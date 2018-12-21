package com.nearsyh.mafia.service;

import com.nearsyh.mafia.protos.Game;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import reactor.core.publisher.Mono;

public interface GameService {

    Mono<Game> getGame(String gameId);

    Mono<Game> createGame(Game game);

    Mono<Game> updateGame(Game game);

    Mono<LinkedHashMap<String, Long>> getRecentGames(int count);
}
