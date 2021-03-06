package com.nearsyh.mafia.common;

import static com.nearsyh.mafia.common.GameAccessor.NO_CHARACTER;
import static com.nearsyh.mafia.common.GameAccessor.NO_PLAYER;

import com.nearsyh.mafia.protos.Character;
import com.nearsyh.mafia.protos.CharacterType;
import com.nearsyh.mafia.protos.Event;
import com.nearsyh.mafia.protos.EventType;
import com.nearsyh.mafia.protos.Game;
import com.nearsyh.mafia.protos.GameStatus;
import com.nearsyh.mafia.protos.Player;
import com.nearsyh.mafia.protos.TurnStatus;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public final class GameConstructor {

    private GameConstructor() {
    }

    public static Game constructGame(Map<String, Integer> characterCounts) {
        var characterList = new ArrayList<CharacterType>();
        characterCounts.forEach((rawCharacterType, count) -> {
            for (int i = 0; i < count; i++) {
                characterList.add(CharacterType.valueOf(rawCharacterType));
            }
        });
        while (true) {
            Collections.shuffle(characterList);
            if (GameValidator.isCharacterListValid(characterList)) {
                break;
            }
        }
        var players = new ArrayList<Player>();
        for (int i = 0; i < characterList.size(); i += 2) {
            players.add(Player.newBuilder()
                .setCharacterTop(Character.newBuilder().setCharacterType(characterList.get(i)))
                .setCharacterBot(Character.newBuilder().setCharacterType(characterList.get(i + 1)))
                .setIndex(i / 2)
                .build());
        }

        return Game.newBuilder()
            .addAllPlayers(players)
            .setCurrentTurn(constructInitialTurnStatus())
            .setGameStatus(constructGameStatus())
            .setNextEvent(Event.newBuilder()
                .setCurrentEventResponse("天黑请闭眼")
                .setEventType(EventType.SUNSET)
                .setTimestamp(Instant.now().toEpochMilli())
                .build())
            .setStartTimestamp(Instant.now().toEpochMilli())
            .build();
    }

    public static TurnStatus constructInitialTurnStatus() {
        return TurnStatus.newBuilder()
            .setFrozenPlayerIndex(NO_PLAYER)
            .setToastPlayerIndex(NO_PLAYER)
            .setInjectionCharacterIndex(NO_CHARACTER)
            .setGuardCharacterIndex(NO_CHARACTER)
            .setKillCharacterIndex(NO_CHARACTER)
            .clearHasAffection()
            .setAffectedCharacter(NO_CHARACTER)
            .setCureCharacterIndex(NO_CHARACTER)
            .setToxicCharacterIndex(NO_CHARACTER)
            .setVerifyCharacterIndex(NO_CHARACTER)
            .setMutedPlayerIndex(NO_PLAYER)
            .clearDeadCharacters()
            .clearVotedCharacters()
            .build();
    }

    public static GameStatus constructGameStatus() {
        return GameStatus.newBuilder()
            .setIdolIndex(NO_PLAYER)
            .setLoverIndex(NO_PLAYER)
            .setIsCureUsed(false)
            .setIsToxicUsed(false)
            .setPardonIndex(NO_PLAYER)
            .setDuelIndex(NO_PLAYER)
            .build();
    }
}
