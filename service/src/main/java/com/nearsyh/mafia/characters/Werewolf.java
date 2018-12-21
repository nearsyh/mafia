package com.nearsyh.mafia.characters;

import static com.nearsyh.mafia.common.GameAccessor.NO_PLAYER;

import com.google.common.base.Preconditions;
import com.nearsyh.mafia.common.GameAccessor;
import com.nearsyh.mafia.protos.CharacterIndex;
import com.nearsyh.mafia.protos.CharacterType;
import com.nearsyh.mafia.protos.Event;
import com.nearsyh.mafia.protos.EventType;
import com.nearsyh.mafia.protos.Game;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public final class Werewolf extends AbstractCharacter implements Character {

    private static final Werewolf INSTANCE = new Werewolf();

    static {
        registerEventListeners(EventType.KILL, INSTANCE.getCharacterType(), INSTANCE::kill);
        registerPreEventListeners(EventType.KILL, INSTANCE::preKill);

        registerEventListeners(EventType.CONFESS, INSTANCE.getCharacterType(), INSTANCE::confess);
    }

    private Werewolf() {
        super(CharacterType.WEREWOLF);
    }

    private Event.Builder preKill(Game game, Event.Builder nextEventBuilder) {
        var hasWolves = !GameAccessor.wolvesWhoCanKill(game).isEmpty();
        var hasNotFrozenWolves = !GameAccessor.notFrozenWolves(game).isEmpty();
        var candidatePlayers = new HashSet<>(GameAccessor.allAlivePlayersIndex(game));
        var noKillNightsCount = GameAccessor.noKillNightsCount(game);
        var hasToast = game.getCurrentTurn().getToastPlayerIndex() >= 0;
        var message = "狼人睁眼杀人.";
        if (GameAccessor.hasAliveCharacterInGame(game, Set.of(CharacterType.TOAST_BAKER))) {
            message += String.format(
                " 面包在这位玩家头上(%s)", hasToast
                    ? game.getCurrentTurn().getToastPlayerIndex() + 1 : "没有面包");
        }
        if (hasWolves && hasNotFrozenWolves) {
            if (hasToast) {
                candidatePlayers.clear();
                candidatePlayers.addAll(GameAccessor.getToastCoveredPlayerIndices(game));
                message += " (有面包, 必须杀人)";
            } else if (noKillNightsCount < 3) {
                candidatePlayers.add(NO_PLAYER);
                message += String.format(" (%s晚没杀人, 今晚可以不杀)", noKillNightsCount);
            } else {
                message += " (3晚没杀人了, 必须杀)";
            }
        } else if (hasWolves) {
            candidatePlayers.add(NO_PLAYER);
            message += " (唯一的普通狼被冻住了, 今晚可以不杀)";
        } else {
            candidatePlayers.add(NO_PLAYER);
            message += " (没有普通狼在上面, 今晚可以不杀)";
        }
        if (candidatePlayers.contains(NO_PLAYER) && hasToast) {
            message += " (有面包, 魅狼睁眼的话必须杀人)";
        }
        return nextEventBuilder.clearCandidateTargets()
            .setCurrentEventResponse(message)
            .addAllCandidateTargets(candidatePlayers);
    }

    private Game kill(Game game, Event event) {
        Preconditions.checkArgument(event.getEventType() == EventType.KILL);
        var currentTurn = game.getCurrentTurn().toBuilder();
        var isOnTop = GameAccessor.hasWolvesOnSurface(game);
        if (!isOnTop && event.getTargets(0) == -1) {
            currentTurn.setKillCharacterIndex(
                CharacterIndex.newBuilder().setPlayerIndex(Integer.MAX_VALUE));
        } else {
            GameAccessor.getCurrentAliveCharacterIndex(game, event.getTargets(0))
                .ifPresent(currentTurn::setKillCharacterIndex);
        }
        return game.toBuilder()
            .setCurrentTurn(currentTurn)
            .build();
    }

    private Game confess(Game game, Event event) {
        Preconditions.checkArgument(event.getEventType() == EventType.CONFESS);
        Preconditions.checkArgument(event.getTargetsCount() == 1);
        var confessPlayerIndex = event.getTargets(0);
        Preconditions.checkArgument(confessPlayerIndex >= 0);

        var character = GameAccessor.getCurrentAliveCharacterIndex(game, confessPlayerIndex)
            .get();
        var newDeadCharacters = GameAccessor.getAllDeadCharacters(game, Set.of(character));

        game = GameAccessor.markCharacterAsDead(game, newDeadCharacters);

        return game.toBuilder()
            .setCurrentTurn(
                game.getCurrentTurn().toBuilder().addAllDeadCharacters(newDeadCharacters))
            .setGameStatus(game.getGameStatus().toBuilder().addAllDeadCharacters(newDeadCharacters))
            .build();
    }
}
