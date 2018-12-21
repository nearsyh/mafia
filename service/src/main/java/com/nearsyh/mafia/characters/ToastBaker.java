package com.nearsyh.mafia.characters;

import com.nearsyh.mafia.common.GameAccessor;
import com.nearsyh.mafia.protos.CharacterType;
import com.nearsyh.mafia.protos.Event;
import com.nearsyh.mafia.protos.EventType;
import com.nearsyh.mafia.protos.Game;
import org.springframework.stereotype.Component;

@Component
public class ToastBaker extends AbstractCharacter {

    private static final ToastBaker INSTANCE = new ToastBaker();

    static {
        registerEventListeners(EventType.TOAST, INSTANCE.getCharacterType(), INSTANCE::toast);
        registerPreEventListeners(EventType.TOAST, INSTANCE::preToast);
    }

    private ToastBaker() {
        super(CharacterType.TOAST_BAKER);
    }

    private Event.Builder preToast(Game game, Event.Builder nextEventBuilder) {
        return nextEventBuilder
            .clearCandidateTargets()
            .addAllCandidateTargets(GameAccessor.allAlivePlayersIndex(game))
            .setCurrentEventResponse("面包师傅选择一个人放面包");
    }

    private Game toast(Game game, Event event) {
        return game.toBuilder()
            .setCurrentTurn(game.getCurrentTurn().toBuilder()
                .setToastPlayerIndex(event.getTargets(0)))
            .build();
    }
}
