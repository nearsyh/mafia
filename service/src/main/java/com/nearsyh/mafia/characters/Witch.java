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
public class Witch extends AbstractCharacter implements Character {

    private static final Witch INSTANCE = new Witch();

    static {
        registerEventListeners(EventType.CURE, INSTANCE.getCharacterType(), INSTANCE::cure);
        registerPreEventListeners(EventType.CURE, INSTANCE::preCure);
        registerEventListeners(EventType.TOXIC, INSTANCE.getCharacterType(), INSTANCE::toxic);
        registerPreEventListeners(EventType.TOXIC, INSTANCE::preToxic);
    }

    Witch() {
        super(CharacterType.WITCH);
    }

    public Event.Builder preCure(Game game, Event.Builder nextEventBuilder) {
        var isOnTop = GameAccessor.isCharacterTypeOnSurface(game, CharacterType.WITCH);
        if (!isOnTop) {
            return nextEventBuilder.clearCandidateTargets()
                .setCurrentEventResponse("今晚死的人是 TA, 你有一瓶解药要救吗? (不在上面, 直接下一步)")
                .addCandidateTargets(NO_PLAYER);
        }

        var killedPlayerIndex = game.getCurrentTurn().getKillCharacterIndex().getPlayerIndex();
        var isPlayerKilledForWitch =
            GameAccessor.isPlayerActuallyKilledThisTurnForWitch(game, killedPlayerIndex);
        var message = String.format(
            "今晚死的人是 TA (%s), 你有一瓶解药要救吗?",
            isPlayerKilledForWitch ? (killedPlayerIndex + 1) : "没人死");
        var hasWitch = !GameAccessor.notFrozenPlayers(game, CharacterType.WITCH).isEmpty();
        if (!hasWitch) {
            return nextEventBuilder.clearCandidateTargets()
                .setCurrentEventResponse(message + " (被冻住了)")
                .addCandidateTargets(NO_PLAYER);
        }
        var candidatePlayers = new HashSet<Integer>();
        candidatePlayers.add(NO_PLAYER);
        if (game.getGameStatus().getIsCureUsed()) {
            message += " (用过了)";
        } else if (isPlayerKilledForWitch) {
            candidatePlayers.add(killedPlayerIndex);
        }
        return nextEventBuilder.clearCandidateTargets()
            .setCurrentEventResponse(message)
            .addAllCandidateTargets(candidatePlayers);
    }

    public Game cure(Game game, Event event) {
        var gameBuilder = game.toBuilder();
        var currentTurn = game.getCurrentTurn().toBuilder();
        var gameStatus = game.getGameStatus().toBuilder();
        if (!game.getGameStatus().getIsCureUsed()) {
            GameAccessor.getCurrentAliveCharacterIndex(game, event.getTargets(0))
                .ifPresent(index -> {
                    currentTurn.setCureCharacterIndex(index);
                    gameStatus.setIsCureUsed(true);
                });
        }
        return gameBuilder
            .setGameStatus(gameStatus)
            .setCurrentTurn(currentTurn)
            .build();
    }

    public Event.Builder preToxic(Game game, Event.Builder nextEventBuilder) {
        var isOnTop = GameAccessor.isCharacterTypeOnSurface(game, CharacterType.WITCH);
        if (!isOnTop) {
            return nextEventBuilder.clearCandidateTargets()
                .setCurrentEventResponse("你有一瓶毒药要用吗? (不在当前第一张牌, 直接下一步)")
                .addCandidateTargets(NO_PLAYER);
        }

        var message = "你有一瓶毒药要用吗?";
        var hasWitch = !GameAccessor.notFrozenPlayers(game, CharacterType.WITCH).isEmpty();
        if (!hasWitch) {
            return nextEventBuilder.clearCandidateTargets()
                .setCurrentEventResponse(message + " (被冻住了)")
                .addCandidateTargets(NO_PLAYER);
        }

        var candidatePlayers = new HashSet<Integer>();
        candidatePlayers.add(NO_PLAYER);
        if (game.getGameStatus().getIsToxicUsed()) {
            message += " (用过了)";
        } else if (GameAccessor.isCureUsedInThisTurn(game)) {
            message += " (这回合用过解药不能再用毒药了)";
        } else {
            candidatePlayers.addAll(GameAccessor.allAlivePlayersIndex(game));
        }
        return nextEventBuilder.clearCandidateTargets()
            .setCurrentEventResponse(message)
            .addAllCandidateTargets(candidatePlayers);
    }

    public Game toxic(Game game, Event event) {
        var gameBuilder = game.toBuilder();
        var currentTurn = game.getCurrentTurn().toBuilder();
        var gameStatus = game.getGameStatus().toBuilder();
        if (game.getCurrentTurn().getCureCharacterIndex().getPlayerIndex() < 0
            && !game.getGameStatus().getIsToxicUsed()) {
            GameAccessor.getCurrentAliveCharacterIndex(game, event.getTargets(0))
                .ifPresent(index -> {
                    currentTurn.setToxicCharacterIndex(index);
                    gameStatus.setIsToxicUsed(true);
                });
        }
        return gameBuilder
            .setCurrentTurn(currentTurn)
            .setGameStatus(gameStatus)
            .build();
    }
}
