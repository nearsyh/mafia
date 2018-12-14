package com.nearsyh.mafia.common;

import com.nearsyh.mafia.protos.CharacterIndex;
import com.nearsyh.mafia.protos.Game;
import java.util.Optional;

public final class GameAccessor {

    private GameAccessor() {}

    public static Optional<CharacterIndex> getCurrentAliveCharacterIndex(Game game, int playerIndex) {
        if (playerIndex < 0 || playerIndex >= game.getPlayersCount()) {
            return Optional.empty();
        }
        var player = game.getPlayers(playerIndex);
        var characterIndexBuilder = CharacterIndex.newBuilder().setPlayerIndex(playerIndex);
        characterIndexBuilder = !player.getCharacterTop().getIsDead()
            ? characterIndexBuilder.setCharacterIndex(0)
            : !player.getCharacterBot().getIsDead()
                ? characterIndexBuilder.setCharacterIndex(1)
                : null;
        return Optional.ofNullable(characterIndexBuilder).map(CharacterIndex.Builder::build);
    }

}
