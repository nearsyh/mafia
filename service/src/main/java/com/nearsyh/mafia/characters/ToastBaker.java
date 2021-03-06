package com.nearsyh.mafia.characters;

import static com.nearsyh.mafia.common.GameAccessor.NO_PLAYER;

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
        if (GameAccessor.playersWhoCanUseSkill(game, getCharacterType()).isEmpty()) {
            return nextEventBuilder.clearCandidateTargets()
                .setCurrentEventResponse("面包师傅选择一个人放面包 (不在上面, 直接下一步)")
                .addCandidateTargets(NO_PLAYER);
        }
        if (GameAccessor.notFrozenPlayers(game, getCharacterType()).isEmpty()) {
            return nextEventBuilder.clearCandidateTargets()
                .setCurrentEventResponse("面包师傅选择一个人放面包 (被冻住了)")
                .addCandidateTargets(NO_PLAYER);
        }
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
