package com.nearsyh.mafia.characters;

import com.nearsyh.mafia.common.GameAccessor;
import com.nearsyh.mafia.common.GameConstants;
import com.nearsyh.mafia.common.GameConstructor;
import com.nearsyh.mafia.protos.CharacterIndex;
import com.nearsyh.mafia.protos.CharacterType;
import com.nearsyh.mafia.protos.Event;
import com.nearsyh.mafia.protos.EventType;
import com.nearsyh.mafia.protos.Game;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class VirtualCharacter extends AbstractCharacter implements Character {

    private static final VirtualCharacter INSTANCE = new VirtualCharacter();

    VirtualCharacter() {
        super(CharacterType.CHARACTER_TYPE_UNSPECIFIED);
    }

    static {
        registerPreEventListeners(EventType.SUNSET, INSTANCE::preSunset);
        registerEventListeners(EventType.SUNSET, INSTANCE.getCharacterType(), INSTANCE::sunset);

        registerPreEventListeners(EventType.SUNRISE, INSTANCE::preSunrise);
        registerEventListeners(EventType.SUNRISE, INSTANCE.getCharacterType(), INSTANCE::sunrise);

        registerPreEventListeners(EventType.ANNOUNCE_DEATH, INSTANCE::preAnnounceDeath);
        registerEventListeners(EventType.ANNOUNCE_DEATH, INSTANCE.getCharacterType(),
            INSTANCE::announceDeath);

        registerPreEventListeners(EventType.SPEECH, INSTANCE::preSpeech);
        registerEventListeners(EventType.SPEECH, INSTANCE.getCharacterType(), INSTANCE::speech);

        registerPreEventListeners(EventType.VOTE, INSTANCE::preVote);
        registerEventListeners(EventType.VOTE, INSTANCE.getCharacterType(), INSTANCE::vote);
    }

    /* Sunset */
    private Event.Builder preSunset(Game game, Event.Builder nextEventBuilder) {
        return nextEventBuilder.setCurrentEventResponse("天黑请闭眼");
    }

    private Game sunset(Game game, Event event) {
        return game.toBuilder()
            .addPastTurns(game.getCurrentTurn())
            .setTurnId(game.getTurnId() + 1)
            .setLastTurn(game.getCurrentTurn())
            .setCurrentTurn(GameConstructor.constructInitialTurnStatus())
            .build();
    }

    /* Sunrise */
    private Event.Builder preSunrise(Game game, Event.Builder nextEventBuilder) {
        return nextEventBuilder.setCurrentEventResponse("天亮了");
    }

    private Game sunrise(Game game, Event event) {
        Set<CharacterIndex> deadCharacters = new HashSet<CharacterIndex>();

        // 被杀的角色
        var killedCharacterIndex = game.getCurrentTurn().getKillCharacterIndex();
        if (killedCharacterIndex.getPlayerIndex() >= 0
            && GameAccessor
            .isPlayerActuallyKilledThisTurn(game, killedCharacterIndex.getPlayerIndex())) {
            deadCharacters.add(killedCharacterIndex);
        }
        // 被毒死的角色
        var toxicCharacterIndex = game.getCurrentTurn().getToxicCharacterIndex();
        // TODO: 被毒能被针救吗?
        if (toxicCharacterIndex.getPlayerIndex() >= 0
            && !GameAccessor.isPlayerSavedThisTurn(game, toxicCharacterIndex.getPlayerIndex())) {
            deadCharacters.add(toxicCharacterIndex);
        }

        // 被针扎死的角色
        var injectCharacterIndex = game.getCurrentTurn().getInjectionCharacterIndex();
        if (injectCharacterIndex.getPlayerIndex() >= 0) {
            if (!killedCharacterIndex.equals(injectCharacterIndex) && !toxicCharacterIndex
                .equals(injectCharacterIndex)) {
                game = GameAccessor
                    .increasePlayerInjectionCount(game, injectCharacterIndex.getPlayerIndex());
                if (GameAccessor
                    .getPlayerInjectionCount(game, injectCharacterIndex.getPlayerIndex()) >= 2) {
                    deadCharacters.add(injectCharacterIndex);
                }
            }
        }
        deadCharacters = GameAccessor.getAllDeadCharacters(game, deadCharacters);
        // 更新 Character 的 Dead 字段
        game = GameAccessor.markCharacterAsDead(game, deadCharacters);
        var gameStatusBuilder = game.getGameStatus().toBuilder();
        gameStatusBuilder.addAllDeadCharacters(deadCharacters);
        var currentTurnBuilder = game.getCurrentTurn().toBuilder();
        currentTurnBuilder.clearDeadCharacters().addAllDeadCharacters(deadCharacters);

        return game.toBuilder()
            .setCurrentTurn(currentTurnBuilder)
            .setGameStatus(gameStatusBuilder)
            .build();
    }

    /* Announce */
    private Event.Builder preAnnounceDeath(Game game, Event.Builder nextEventBuilder) {
        return nextEventBuilder.setCurrentEventResponse(
            game.getCurrentTurn().getDeadCharactersCount() <= 0
                ? "今晚是个平安夜"
                : String.format("今晚死的人是 %s", game.getCurrentTurn().getDeadCharactersList()
                    .stream()
                    .map(characterIndex -> GameConstants.toPlayerName(game, characterIndex))
                    .collect(Collectors.joining(","))));
    }

    private Game announceDeath(Game game, Event event) {
        return game;
    }

    /* 发言 */
    private Event.Builder preSpeech(Game game, Event.Builder nextEventBuilder) {
        var ascOrder = (game.getTurnId() % 2 == 0);
        var lastDeadCharacter = game.getCurrentTurn().getDeadCharactersCount() == 0
            ? null
            : game.getCurrentTurn()
                .getDeadCharacters(game.getCurrentTurn().getDeadCharactersCount() - 1);
        String message = "";
        if (lastDeadCharacter == null) {
            message += String.format(
                "(先说遗言, 如果有的话) 从 %s 号玩家开始发言.", ascOrder ? 1 : game.getPlayersCount());

        } else {
            message += String.format(
                "(先说遗言, 如果有的话) 从 %s 号玩家%s边开始发言.",
                lastDeadCharacter.getPlayerIndex() + 1,
                ascOrder ? "右" : "左");
        }
        if (game.getCurrentTurn().getMutedPlayerIndex() >= 0) {
            message += String.format("(%s 号玩家禁言, 轮到 TA 再告诉 TA)",
                game.getCurrentTurn().getMutedPlayerIndex() + 1);
        }
        return nextEventBuilder.setCurrentEventResponse(message);
    }

    private Game speech(Game game, Event event) {
        return game;
    }

    /* 投票 */
    private Event.Builder preVote(Game game, Event.Builder nextEventBuilder) {
        return nextEventBuilder.setCurrentEventResponse(String.format("开始投票."))
            .addAllCandidateTargets(GameAccessor.allAlivePlayersIndex(game));
    }

    private Game vote(Game game, Event event) {
        var votedCharacterIndices = event.getTargetsList().stream()
            .map(playerIndex -> GameAccessor
                .getCurrentAliveCharacterIndex(game, playerIndex))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toSet());
        var newDeadCharacterIndices = GameAccessor
            .getAllDeadCharacters(game, votedCharacterIndices);

        // 更新 Character 的 Dead 字段
        var updatedGame = GameAccessor.markCharacterAsDead(game, newDeadCharacterIndices);

        var currentTurnBuilder = updatedGame.getCurrentTurn().toBuilder()
            .clearVotedCharacters()
            .addAllVotedCharacters(votedCharacterIndices)
            .addAllDeadCharacters(newDeadCharacterIndices);

        return updatedGame.toBuilder()
            .setCurrentTurn(currentTurnBuilder)
            .setNextEvent(updatedGame.getNextEvent().toBuilder()
                .setLastEventResponse(String.format(
                    "投完票死的人是 %s", newDeadCharacterIndices.stream()
                        .map(characterIndex -> GameConstants.toPlayerName(game, characterIndex))
                        .collect(Collectors.joining(",")))))
            .setGameStatus(
                updatedGame.getGameStatus().toBuilder()
                    .addAllDeadCharacters(newDeadCharacterIndices))
            .build();
    }
}
