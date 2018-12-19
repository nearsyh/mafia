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
public class Guardian extends AbstractCharacter implements Character {

    private static final Guardian INSTANCE = new Guardian();

    static {
        registerEventListeners(EventType.GUARD, INSTANCE.getCharacterType(), INSTANCE::guard);
        registerPreEventListeners(EventType.GUARD, INSTANCE::preGuard);
    }

    private Guardian() {
        super(CharacterType.GUARDIAN);
    }

    private Event.Builder preGuard(Game game, Event.Builder nextEventBuilder) {
        var lastGuardedPlayer = GameAccessor.lastGuardedPlayer(game);
        var candidatePlayers = new HashSet<>(GameAccessor.allAlivePlayersIndex(game));
        candidatePlayers.remove(lastGuardedPlayer);
        candidatePlayers.add(NO_PLAYER);
        return nextEventBuilder.clearCandidateTargets()
            .setCurrentEventResponse("守卫请睁眼选择一个人守护 (可以不守)")
            .addAllCandidateTargets(candidatePlayers);
    }

    private Game guard(Game game, Event event) {
        var gameBuilder = game.toBuilder();
        var currentTurn = game.getCurrentTurn().toBuilder();
        if (GameAccessor.lastGuardedPlayer(game) != event.getTargets(0)) {
            GameAccessor.getCurrentAliveCharacterIndex(game, event.getTargets(0))
                .ifPresent(currentTurn::setGuardCharacterIndex);
        }
        return gameBuilder
            .setCurrentTurn(currentTurn)
            .build();
    }
}
