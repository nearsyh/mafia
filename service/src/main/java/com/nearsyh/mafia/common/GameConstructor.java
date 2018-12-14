package com.nearsyh.mafia.common;

import com.nearsyh.mafia.protos.Character;
import com.nearsyh.mafia.protos.CharacterType;
import com.nearsyh.mafia.protos.Game;
import com.nearsyh.mafia.protos.Player;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public final class GameConstructor {

    private GameConstructor() {}

    public static Game constructGame(Map<String, Integer> characterCounts) {
        var characterList = new ArrayList<CharacterType>();
        characterCounts.forEach((rawCharacterType, count) -> {
            for (int i = 0; i < count; i ++) {
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
            .build();
    }
}
