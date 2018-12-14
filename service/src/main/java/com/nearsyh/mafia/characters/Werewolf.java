package com.nearsyh.mafia.characters;

import com.google.common.base.Preconditions;
import com.nearsyh.mafia.common.GameAccessor;
import com.nearsyh.mafia.protos.CharacterType;
import com.nearsyh.mafia.protos.Event;
import com.nearsyh.mafia.protos.EventType;
import com.nearsyh.mafia.protos.Game;

public final class Werewolf extends AbstractCharacter implements Character {

    private static final Werewolf INSTANCE = new Werewolf();
    static {
        registerEventListeners(EventType.KILL, INSTANCE::kill);
    }

    private Werewolf() {
        super(CharacterType.WEREWOLF);
    }

    private Game kill(Game game, Event event) {
        Preconditions.checkArgument(event.getEventType() == EventType.KILL);
        var currentTurn = game.getCurrentTurn().toBuilder();
        GameAccessor.getCurrentAliveCharacterIndex(game, event.getTargets(0))
            .ifPresent(currentTurn::setKillCharacterIndex);
        return game.toBuilder()
            .setCurrentTurn(currentTurn)
            .setNextEvent(AbstractCharacter.nextEvent(game, event))
            .build();
    }
}
