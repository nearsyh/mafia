package com.nearsyh.mafia.characters;

import com.nearsyh.mafia.protos.Event;
import com.nearsyh.mafia.protos.Game;

public interface CharacterPreAction {

    Event.Builder preHandle(Game game, Event.Builder nextEventBuilder);

    CharacterPreAction NOOP = (game, any) -> any;

}
