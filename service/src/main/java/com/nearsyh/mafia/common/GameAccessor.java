package com.nearsyh.mafia.common;

import com.nearsyh.mafia.protos.Character;
import com.nearsyh.mafia.protos.CharacterIndex;
import com.nearsyh.mafia.protos.CharacterType;
import com.nearsyh.mafia.protos.Game;
import com.nearsyh.mafia.protos.Player;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class GameAccessor {

    public static final int NO_PLAYER = -1;

    private GameAccessor() {
    }

    public static boolean isPlayerAlive(Game game, int playerIndex) {
        if (playerIndex < 0 || playerIndex >= game.getPlayersCount()) {
            return false;
        }
        return isPlayerAlive(game.getPlayers(playerIndex));
    }

    public static boolean isPlayerAlive(Player player) {
        return !player.getCharacterTop().getIsDead() || !player.getCharacterBot().getIsDead();
    }

    public static Optional<CharacterIndex> getCurrentAliveCharacterIndex(Game game,
        int playerIndex) {
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
        return characterType == CharacterType.WEREWOLF
            || characterType == CharacterType.WOLF_BEAUTY;
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
        for (int i = game.getPastTurnsCount() - 1; i >= 0; i--) {
            if (game.getPastTurns(i).getKillCharacterIndex().getPlayerIndex() < 0) {
                count += 1;
            } else {
                break;
            }
        }
        return count;
    }

    public static int lastGuardedPlayer(Game game) {
        return game.getPastTurnsCount() <= 0
            ? -1
            : game.getPastTurns(game.getPastTurnsCount() - 1).getGuardCharacterIndex()
                .getPlayerIndex();
    }

    public static List<Integer> allAlivePlayersIndex(Game game) {
        return game.getPlayersList().stream()
            .filter(GameAccessor::isPlayerAlive)
            .map(Player::getIndex)
            .collect(Collectors.toList());
    }

    public static boolean isPlayerActuallyKilledThisTurnForWitch(Game game, int playerIndex) {
        if (playerIndex < 0) {
            return false;
        }
        var currentTurn = game.getCurrentTurn();
        if (currentTurn.getKillCharacterIndex().getPlayerIndex() != playerIndex) {
            return false;
        }
        if (currentTurn.getGuardCharacterIndex().getPlayerIndex() != playerIndex) {
            return false;
        }
        // TODO
        if (currentTurn.getFrozenPlayerIndex() != playerIndex) {
            return false;
        }
        return true;
    }

    public static boolean isCureUsedInThisTurn(Game game) {
        return game.getCurrentTurn().getCureCharacterIndex().getPlayerIndex() >= 0;
    }

    public static boolean hasAliveCharacterInGame(Game game, Collection<CharacterType> characterTypes) {
        return game.getPlayersList().stream().anyMatch(
            player -> !(player.getCharacterTop().getIsDead()
                && characterTypes.contains(player.getCharacterTop().getCharacterType()))
                || !(player.getCharacterBot().getIsDead()
                && characterTypes.contains(player.getCharacterBot().getCharacterType())));
    }
}
