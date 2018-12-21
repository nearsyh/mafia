package com.nearsyh.mafia.characters;

import static com.nearsyh.mafia.common.GameAccessor.NO_PLAYER;

import com.nearsyh.mafia.common.GameAccessor;
import com.nearsyh.mafia.protos.CharacterType;
import com.nearsyh.mafia.protos.Event;
import com.nearsyh.mafia.protos.EventType;
import com.nearsyh.mafia.protos.Game;
import java.util.HashSet;
import org.springframework.stereotype.Component;

@Component
public class Muter extends AbstractCharacter {

    private static final Muter INSTANCE = new Muter();

    static {
        registerEventListeners(EventType.MUTE, INSTANCE.getCharacterType(), INSTANCE::mute);
        registerPreEventListeners(EventType.MUTE, INSTANCE::preMute);
    }

    private Muter() {
        super(CharacterType.MUTER);
    }

    private Event.Builder preMute(Game game, Event.Builder nextEventBuilder) {
        if (GameAccessor.playersWhoCanUseSkill(game, getCharacterType()).isEmpty()) {
            return nextEventBuilder.clearCandidateTargets()
                .setCurrentEventResponse("禁言长老选择一个人禁言 (不在上面, 直接下一步)")
                .addCandidateTargets(NO_PLAYER);
        }
        if (GameAccessor.notFrozenPlayers(game, getCharacterType()).isEmpty()) {
            return nextEventBuilder.clearCandidateTargets()
                .setCurrentEventResponse("禁言长老选择一个人禁言 (被冻住了)")
                .addCandidateTargets(NO_PLAYER);
        }
        var candidatePlayers = new HashSet<>(GameAccessor.allAlivePlayersIndex(game));
        candidatePlayers.add(NO_PLAYER);
        return nextEventBuilder.clearCandidateTargets()
            .setCurrentEventResponse("禁言长老选择一个人禁言 (可以不禁言)")
            .addAllCandidateTargets(candidatePlayers);
    }

    private Game mute(Game game, Event event) {
        return game.toBuilder()
            .setCurrentTurn(game.getCurrentTurn().toBuilder()
                .setMutedPlayerIndex(event.getTargets(0)))
            .build();
    }
}
