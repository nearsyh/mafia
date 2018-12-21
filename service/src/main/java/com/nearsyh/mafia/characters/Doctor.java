package com.nearsyh.mafia.characters;

import static com.nearsyh.mafia.common.GameAccessor.NO_PLAYER;

import com.nearsyh.mafia.common.GameAccessor;
import com.nearsyh.mafia.protos.CharacterType;
import com.nearsyh.mafia.protos.Event;
import com.nearsyh.mafia.protos.EventType;
import com.nearsyh.mafia.protos.Game;
import org.springframework.stereotype.Component;

@Component
public class Doctor extends AbstractCharacter {

    private static final Doctor INSTANCE = new Doctor();

    static {
        registerEventListeners(EventType.INJECT, INSTANCE.getCharacterType(), INSTANCE::inject);
        registerPreEventListeners(EventType.INJECT, INSTANCE::preInject);
    }

    private Doctor() {
        super(CharacterType.DOCTOR);
    }

    private Event.Builder preInject(Game game, Event.Builder nextEventBuilder) {
        if (GameAccessor.playersWhoCanUseSkill(game, getCharacterType()).isEmpty()) {
            return nextEventBuilder.clearCandidateTargets()
                .setCurrentEventResponse("医生请扎针 (不在上面, 直接下一步)")
                .addCandidateTargets(NO_PLAYER);
        }
        if (GameAccessor.notFrozenPlayers(game, getCharacterType()).isEmpty()) {
            return nextEventBuilder.clearCandidateTargets()
                .setCurrentEventResponse("医生请扎针 (被冻住了)")
                .addCandidateTargets(NO_PLAYER);
        }
        return nextEventBuilder
            .setCurrentEventResponse("医生请扎针")
            .clearCandidateTargets()
            .addAllCandidateTargets(GameAccessor.allAlivePlayersIndex(game))
            .addCandidateTargets(NO_PLAYER);
    }

    private Game inject(Game game, Event event) {
        var turnBuilder = game.getCurrentTurn().toBuilder();
        GameAccessor.getCurrentAliveCharacterIndex(game, event.getTargets(0))
            .ifPresent(turnBuilder::setInjectionCharacterIndex);
        return game.toBuilder()
            .setCurrentTurn(turnBuilder)
            .build();
    }
}
