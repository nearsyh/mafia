package com.nearsyh.mafia.characters;

import static com.nearsyh.mafia.common.GameAccessor.NO_PLAYER;

import com.google.common.base.Preconditions;
import com.nearsyh.mafia.common.GameAccessor;
import com.nearsyh.mafia.protos.CharacterType;
import com.nearsyh.mafia.protos.Event;
import com.nearsyh.mafia.protos.EventType;
import com.nearsyh.mafia.protos.Game;
import java.util.HashSet;
import org.springframework.stereotype.Component;

@Component
public final class Seer extends AbstractCharacter implements Character {

    private static final Seer INSTANCE = new Seer();

    static {
        registerEventListeners(EventType.VERIFY, INSTANCE.getCharacterType(), INSTANCE::see);
        registerPreEventListeners(EventType.VERIFY, INSTANCE::preSee);
    }

    Seer() {
        super(CharacterType.SEER);
    }

    private Event.Builder preSee(Game game, Event.Builder nextEventBuilder) {
        var hasSeer = !GameAccessor.notFrozenPlayers(game, CharacterType.SEER).isEmpty();
        var candidatePlayers = hasSeer
            ? new HashSet<>(GameAccessor.allAlivePlayersIndex(game))
            : new HashSet<Integer>();
        candidatePlayers.add(NO_PLAYER);
        return nextEventBuilder.clearCandidateTargets()
            .setCurrentEventResponse(hasSeer
                ? "预言家请睁眼选择一个人查验身份 (可以不查)"
                : "预言家请睁眼选择一个人查验身份 (被冻住了, 不可以查)")
            .addAllCandidateTargets(candidatePlayers);
    }

    private Game see(Game game, Event event) {
        Preconditions.checkArgument(event.getEventType() == EventType.VERIFY);
        var currentTurn = game.getCurrentTurn().toBuilder();
        GameAccessor.getCurrentAliveCharacterIndex(game, event.getTargets(0))
            .ifPresent(currentTurn::setVerifyCharacterIndex);
        var isPlayerBad = GameAccessor.doesPlayerSeemBad(game, event.getTargets(0));
        return game.toBuilder()
            .setCurrentTurn(currentTurn)
            .setNextEvent(game.getNextEvent().toBuilder()
                .setLastEventResponse(String.format(
                    "这个手势是好人, 这个手势是坏人, TA 是这个 (%s)", isPlayerBad ? "坏人" : "好人")))
            .build();
    }

}
