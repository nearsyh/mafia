package com.nearsyh.mafia.characters;

import com.google.common.base.Preconditions;
import com.nearsyh.mafia.common.GameAccessor;
import com.nearsyh.mafia.common.GameConstants;
import com.nearsyh.mafia.protos.CharacterIndex;
import com.nearsyh.mafia.protos.CharacterType;
import com.nearsyh.mafia.protos.Event;
import com.nearsyh.mafia.protos.EventType;
import com.nearsyh.mafia.protos.Game;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class Knight extends AbstractCharacter {

    private static final Knight INSTANCE = new Knight();

    static {
        registerEventListeners(EventType.DUEL, INSTANCE.getCharacterType(), INSTANCE::duel);
    }

    private Knight() {
        super(CharacterType.KNIGHT);
    }

    private Game duel(Game game, Event event) {

        var dueledPlayerIndex = event.getTargets(0);
        var dueledCharacter = GameAccessor.getCurrentAliveCharacter(game, dueledPlayerIndex).get();
        var knightPlayerIndex = GameAccessor.getPlayerOfType(game, getCharacterType());

        Set<CharacterIndex> newDeadCharacters = new HashSet<>();
        var correct = false;
        if (GameAccessor.isWolfOrSuccubus(game, dueledCharacter)) {
            newDeadCharacters = GameAccessor.getAllDeadCharacters(
                game,
                Set.of(GameAccessor.getCurrentAliveCharacterIndex(game, dueledPlayerIndex).get()));
            correct = true;
        } else {
            newDeadCharacters = GameAccessor.getAllDeadCharacters(
                game,
                Set.of(GameAccessor.getCurrentAliveCharacterIndex(game, knightPlayerIndex).get()));
            correct = false;
        }

        var updateGame = GameAccessor.markCharacterAsDead(game, newDeadCharacters);
        return updateGame.toBuilder()
            .setGameStatus(updateGame.getGameStatus().toBuilder()
                .setDuelIndex(dueledPlayerIndex)
                .addAllDeadCharacters(newDeadCharacters))
            .setNextEvent(updateGame.getNextEvent().toBuilder()
                .setLastEventResponse(String.format(
                    "决斗%s, 死的人是 %s",
                    correct ? "成功" : "失败",
                    newDeadCharacters.stream()
                        .map(characterIndex -> GameConstants
                            .toPlayerName(updateGame, characterIndex))
                        .collect(Collectors.joining(",")))))
            .build();
    }
}
