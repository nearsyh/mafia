package com.nearsyh.mafia.service;

import com.nearsyh.mafia.protos.Game;
import reactor.core.publisher.Mono;

public interface GameService {

    Mono<Game> getGame(String gameId);

    Mono<Game> createGame(Game game);

    Mono<Game> updateGame(Game game);
}
