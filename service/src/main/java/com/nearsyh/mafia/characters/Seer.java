package com.nearsyh.mafia.characters;

import com.google.common.base.Preconditions;
import com.nearsyh.mafia.common.GameAccessor;
import com.nearsyh.mafia.protos.CharacterType;
import com.nearsyh.mafia.protos.Event;
import com.nearsyh.mafia.protos.EventType;
import com.nearsyh.mafia.protos.Game;

public final class Seer extends AbstractCharacter implements Character {

    private static final Seer INSTANCE = new Seer();

    static {
        registerEventListeners(EventType.VERIFY, INSTANCE::see);
    }

    Seer() {
        super(CharacterType.SEER);
    }

    private Game see(Game game, Event event) {
        Preconditions.checkArgument(event.getEventType() == EventType.VERIFY);
        var currentTurn = game.getCurrentTurn().toBuilder();
        GameAccessor.getCurrentAliveCharacterIndex(game, event.getTargets(0))
            .ifPresent(currentTurn::setVerifyCharacterIndex);
        var isPlayerBad = GameAccessor.doesPlayerSeemBad(game, event.getTargets(0));
        return game.toBuilder()
            .setCurrentTurn(currentTurn)
            .setNextEvent(AbstractCharacter.nextEvent(game, event)
                .setLastEventResponse(
                    String.format("这个手势是好人, 这个手势是坏人, 它是这个 (%s)",
                        isPlayerBad ? "坏人" : "好人")))
            .build();
    }

}
