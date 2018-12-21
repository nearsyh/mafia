package com.nearsyh.mafia.characters;

import com.nearsyh.mafia.common.GameAccessor;
import com.nearsyh.mafia.protos.CharacterType;
import com.nearsyh.mafia.protos.Event;
import com.nearsyh.mafia.protos.EventType;
import com.nearsyh.mafia.protos.Game;
import org.springframework.stereotype.Component;

@Component
public class Penguin extends AbstractCharacter {

    private static final Penguin INSTANCE = new Penguin();

    static {
        registerEventListeners(EventType.GUARD, INSTANCE.getCharacterType(), INSTANCE::freeze);
        registerPreEventListeners(EventType.GUARD, INSTANCE::preFreeze);
    }

    private Penguin() {
        super(CharacterType.PENGUIN);
    }

    private Event.Builder preFreeze(Game game, Event.Builder nextEventBuilder) {
        return nextEventBuilder.setCurrentEventResponse("企鹅选择一个人释放急冻光线")
            .clearCandidateTargets()
            .addAllCandidateTargets(GameAccessor.allAlivePlayersIndex(game))
            .addCandidateTargets(GameAccessor.NO_PLAYER);
    }

    private Game freeze(Game game, Event event) {
        return game.toBuilder()
            .setCurrentTurn(game.getCurrentTurn().toBuilder()
                .setFrozenPlayerIndex(event.getTargets(0)))
            .build();
    }
}
