package com.nearsyh.mafia.characters;

import static com.nearsyh.mafia.common.GameAccessor.NO_PLAYER;

import com.google.common.base.Preconditions;
import com.nearsyh.mafia.common.GameAccessor;
import com.nearsyh.mafia.protos.CharacterType;
import com.nearsyh.mafia.protos.Event;
import com.nearsyh.mafia.protos.EventType;
import com.nearsyh.mafia.protos.Game;
import java.util.HashSet;

public final class Werewolf extends AbstractCharacter implements Character {

    private static final Werewolf INSTANCE = new Werewolf();
    static {
        registerEventListeners(EventType.KILL, INSTANCE.getCharacterType(), INSTANCE::kill);
        registerPreEventListeners(EventType.KILL, INSTANCE::preKill);
    }

    private Werewolf() {
        super(CharacterType.WEREWOLF);
    }

    private Event.Builder preKill(Game game, Event.Builder nextEventBuilder) {
        var candidatePlayers = new HashSet<>(GameAccessor.allAlivePlayersIndex(game));
        var noKillNightsCount = GameAccessor.noKillNightsCount(game);
        var message = "狼人睁眼杀人.";
        if (noKillNightsCount < 3) {
            candidatePlayers.add(NO_PLAYER);
            message += " (今晚可以不杀)";
        } else {
            message += " (3晚没杀人了, 必须杀)";
        }
        return nextEventBuilder.clearCandidateTargets()
            .setCurrentEventResponse(message)
            .addAllCandidateTargets(candidatePlayers);
    }

    private Game kill(Game game, Event event) {
        Preconditions.checkArgument(event.getEventType() == EventType.KILL);
        var currentTurn = game.getCurrentTurn().toBuilder();
        var noKillNightsCount = GameAccessor.noKillNightsCount(game);
        var playerIndexToKill = event.getTargets(0);
        Preconditions.checkArgument(noKillNightsCount <= 3, "好几晚没杀人了");
        if (noKillNightsCount >= 3) {
            Preconditions.checkArgument(playerIndexToKill >= 0, "好几晚没杀人了");
        }
        GameAccessor.getCurrentAliveCharacterIndex(game, event.getTargets(0))
            .ifPresent(currentTurn::setKillCharacterIndex);
        return game.toBuilder()
            .setCurrentTurn(currentTurn)
            .build();
    }
}
