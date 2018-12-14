package com.nearsyh.mafia.characters;

import com.nearsyh.mafia.common.GameAccessor;
import com.nearsyh.mafia.protos.CharacterType;
import com.nearsyh.mafia.protos.Event;
import com.nearsyh.mafia.protos.EventType;
import com.nearsyh.mafia.protos.Game;

public class Guardian extends AbstractCharacter implements Character {

    private static final Guardian INSTANCE = new Guardian();

    static {
        registerEventListeners(EventType.GUARD, INSTANCE::guard);
    }

    Guardian() {
        super(CharacterType.GUARDIAN);
    }

    public Game guard(Game game, Event event) {
        var gameBuilder = game.toBuilder();
        var currentTurn = game.getCurrentTurn().toBuilder();
        if (GameAccessor.lastGuardedPlayer(game) != event.getTargets(0)) {
            GameAccessor.getCurrentAliveCharacterIndex(game, event.getTargets(0))
                .ifPresent(currentTurn::setGuardCharacterIndex);
        }
        return gameBuilder
            .setCurrentTurn(currentTurn)
            .setNextEvent(AbstractCharacter.nextEvent(game, event))
            .build();
    }
}
