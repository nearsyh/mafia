package com.nearsyh.mafia.common;

import com.nearsyh.mafia.protos.Character;
import com.nearsyh.mafia.protos.CharacterIndex;
import com.nearsyh.mafia.protos.CharacterType;
import com.nearsyh.mafia.protos.Game;
import com.nearsyh.mafia.protos.Player;
import java.util.Optional;

public final class GameAccessor {

    private GameAccessor() {}

    public static boolean isPlayerAlive(Game game, int playerIndex) {
        if (playerIndex < 0 || playerIndex >= game.getPlayersCount()) {
            return false;
        }
        return isPlayerAlive(game.getPlayers(playerIndex));
    }

    public static boolean isPlayerAlive(Player player) {
        return !player.getCharacterTop().getIsDead() || !player.getCharacterBot().getIsDead();
    }

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

    private static boolean isWolf(Character character) {
        var characterType = character.getCharacterType();
        return characterType == CharacterType.WEREWOLF || characterType == CharacterType.WOLF_BEAUTY;
    }

    private static boolean isWolfOrSuccubus(Character character) {
        return character.getCharacterType() == CharacterType.SUCCUBUS || isWolf(character);
    }

    public static boolean doesPlayerSeemBad(Game game, int playerIndex) {
        if (playerIndex < 0 || playerIndex >= game.getPlayersCount()) {
            return false;
        }
        return doesPlayerSeemBad(game, game.getPlayers(playerIndex));
    }

    private static boolean doesPlayerSeemBad(Game game, Player player) {
        if (!player.getCharacterTop().getIsDead()) {
            switch (player.getCharacterTop().getCharacterType()) {
                case WEREWOLF:
                case WOLF_BEAUTY:
                    return !isWolfOrSuccubus(player.getCharacterBot());
                case WOLF_CHILD:
                    if (isPlayerAlive(game, game.getGameStatus().getIdolIndex())) {
                        return false;
                    }
                    return !isWolfOrSuccubus(player.getCharacterBot());
                default:
                    return false;
            }
        } else if (!player.getCharacterBot().getIsDead()) {
            switch (player.getCharacterTop().getCharacterType()) {
                case WEREWOLF:
                case WOLF_BEAUTY:
                    return true;
                case WOLF_CHILD:
                    return !isPlayerAlive(game, game.getGameStatus().getIdolIndex());
                default:
                    return false;
            }
        }
        return false;
    }

    public static int noKillNightsCount(Game game) {
        int count = 0;
        for (int i = game.getPastTurnsCount() - 1; i >= 0; i --) {
            if (game.getPastTurns(i).getKillCharacterIndex().getPlayerIndex() < 0) {
                count += 1;
            } else {
                break;
            }
        }
        return count;
    }

}
