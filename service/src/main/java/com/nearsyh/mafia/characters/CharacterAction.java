package com.nearsyh.mafia.characters;

import com.nearsyh.mafia.protos.Event;
import com.nearsyh.mafia.protos.Game;

public interface CharacterAction {
    Game handle(Game game, Event event);

    CharacterAction NOOP = (game, any) -> game;
}
