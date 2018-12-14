package com.nearsyh.mafia.characters;

import com.google.common.base.Preconditions;
import com.nearsyh.mafia.common.GameAccessor;
import com.nearsyh.mafia.protos.CharacterType;
import com.nearsyh.mafia.protos.Event;
import com.nearsyh.mafia.protos.EventType;
import com.nearsyh.mafia.protos.Game;

public class Witch extends AbstractCharacter implements Character {

    private static final Witch INSTANCE = new Witch();

    static {
        registerEventListeners(EventType.CURE, INSTANCE::cure);
        registerEventListeners(EventType.TOXIC, INSTANCE::toxic);
    }

    Witch() {
        super(CharacterType.WITCH);
    }

    public Game cure(Game game, Event event) {
        var gameBuilder = game.toBuilder();
        var currentTurn = game.getCurrentTurn().toBuilder();
        if (!game.getGameStatus().getIsCureUsed()) {
            GameAccessor.getCurrentAliveCharacterIndex(game, event.getTargets(0))
                .ifPresent(currentTurn::setCureCharacterIndex);
        }
        return gameBuilder
            .setCurrentTurn(currentTurn)
            .setNextEvent(AbstractCharacter.nextEvent(game, event))
            .build();
    }

    public Game toxic(Game game, Event event) {
        var gameBuilder = game.toBuilder();
        var currentTurn = game.getCurrentTurn().toBuilder();
        if (game.getCurrentTurn().getCureCharacterIndex().getPlayerIndex() < 0
            && !game.getGameStatus().getIsToxicUsed()) {
            GameAccessor.getCurrentAliveCharacterIndex(game, event.getTargets(0))
                .ifPresent(currentTurn::setToxicCharacterIndex);
        }
        return gameBuilder
            .setCurrentTurn(currentTurn)
            .setNextEvent(AbstractCharacter.nextEvent(game, event))
            .build();
    }
}
