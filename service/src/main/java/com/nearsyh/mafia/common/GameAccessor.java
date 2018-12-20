package com.nearsyh.mafia.common;

import com.nearsyh.mafia.protos.Character;
import com.nearsyh.mafia.protos.CharacterIndex;
import com.nearsyh.mafia.protos.CharacterType;
import com.nearsyh.mafia.protos.Game;
import com.nearsyh.mafia.protos.Player;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class GameAccessor {

    public static final int NO_PLAYER = -1;
    public static final CharacterIndex NO_CHARACTER = CharacterIndex.newBuilder()
        .setPlayerIndex(NO_PLAYER).build();
    public static final int TOP_CHARACTER = 0;
    public static final int BOT_CHARACTER = 1;

    private GameAccessor() {
    }

    public static boolean isCharacterTypeOnSurface(Game game, CharacterType characterType) {
        return game.getPlayersList().stream().anyMatch(
            player -> {
                if (!player.getCharacterTop().getIsDead()) {
                    return player.getCharacterTop().getCharacterType() == characterType;
                } else if (!player.getCharacterBot().getIsDead()) {
                    return player.getCharacterBot().getCharacterType() == characterType;
                }
                return false;
            });
    }

    public static boolean hasWolvesOnSurface(Game game) {
        return game.getPlayersList().stream().anyMatch(
            player -> {
                if (!player.getCharacterTop().getIsDead()) {
                    return isWolf(game, player.getCharacterTop());
                } else if (!player.getCharacterBot().getIsDead()) {
                    return isWolf(game, player.getCharacterBot());
                }
                return false;
            });
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

    public static Character getCharacter(Game game, CharacterIndex characterIndex) {
        var player = game.getPlayers(characterIndex.getPlayerIndex());
        return characterIndex.getCharacterIndex() == 0
            ? player.getCharacterTop()
            : player.getCharacterBot();
    }

    public static Optional<Character> getCurrentAliveCharacter(Game game,
        int playerIndex) {
        if (playerIndex < 0 || playerIndex >= game.getPlayersCount()) {
            return Optional.empty();
        }
        var player = game.getPlayers(playerIndex);
        var character = !player.getCharacterTop().getIsDead() ? player.getCharacterTop()
            : !player.getCharacterBot().getIsDead() ? player.getCharacterBot()
                : null;
        return Optional.ofNullable(character);
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

    private static boolean isWolf(Game game, Character character) {
        var characterType = character.getCharacterType();
        return characterType == CharacterType.WEREWOLF
            || characterType == CharacterType.WOLF_BEAUTY
            || (characterType == CharacterType.WOLF_CHILD && !isPlayerAlive(game,
            game.getGameStatus().getIdolIndex()));
    }

    private static boolean isWolfOnSurface(Game game, Player player) {
        if (!player.getCharacterTop().getIsDead()) {
            return isWolf(game, player.getCharacterTop());
        } else if (!player.getCharacterBot().getIsDead()) {
            return isWolf(game, player.getCharacterBot());
        }
        return false;
    }

    private static boolean isWolfOrSuccubus(Game game, Character character) {
        return character.getCharacterType() == CharacterType.SUCCUBUS || isWolf(game, character);
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
                    return !isWolfOrSuccubus(game, player.getCharacterBot());
                case WOLF_CHILD:
                    if (isPlayerAlive(game, game.getGameStatus().getIdolIndex())) {
                        return false;
                    }
                    return !isWolfOrSuccubus(game, player.getCharacterBot());
                default:
                    return false;
            }
        } else if (!player.getCharacterBot().getIsDead()) {
            return isWolf(game, player.getCharacterBot());
        }
        return false;
    }

    public static int noKillNightsCount(Game game) {
        int count = 0;
        if (game.getTurnId() == 1) {
            return 0;
        }
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
        if (currentTurn.getGuardCharacterIndex().getPlayerIndex() == playerIndex) {
            return false;
        }
        // TODO
        if (currentTurn.getFrozenPlayerIndex() == playerIndex) {
            return false;
        }
        return true;
    }

    public static boolean isCureUsedInThisTurn(Game game) {
        return game.getCurrentTurn().getCureCharacterIndex().getPlayerIndex() >= 0;
    }

    public static boolean hasAliveCharacterInGame(Game game,
        Collection<CharacterType> characterTypes) {
        return game.getPlayersList().stream().anyMatch(
            player -> !(player.getCharacterTop().getIsDead()
                && characterTypes.contains(player.getCharacterTop().getCharacterType()))
                || !(player.getCharacterBot().getIsDead()
                && characterTypes.contains(player.getCharacterBot().getCharacterType())));
    }

    public static int getPlayerOfType(Game game, CharacterType characterType) {
        return game.getPlayersList().stream()
            .filter(player -> doesPlayerHasThisCharacter(game, player.getIndex(), characterType))
            .findFirst()
            .map(Player::getIndex)
            .orElse(NO_PLAYER);
    }

    public static boolean isPlayerActuallyKilledThisTurn(Game game, int playerIndex) {
        var currentTurn = game.getCurrentTurn();
        if (!isPlayerActuallyKilledThisTurnForWitch(game, playerIndex)) {
            return false;
        }
        return currentTurn.getCureCharacterIndex().getPlayerIndex() != playerIndex
            && currentTurn.getInjectionCharacterIndex().getPlayerIndex() != playerIndex;
    }

    public static boolean isPlayerSavedThisTurn(Game game, int playerIndex) {
        if (playerIndex < 0) {
            return false;
        }
        var currentTurn = game.getCurrentTurn();
        return currentTurn.getCureCharacterIndex().getPlayerIndex() == playerIndex
            || currentTurn.getInjectionCharacterIndex().getPlayerIndex() == playerIndex
            || currentTurn.getFrozenPlayerIndex() == playerIndex;
    }

    public static int getPlayerInjectionCount(Game game, int playerIndex) {
        return getCurrentAliveCharacter(game, playerIndex)
            .map(Character::getInjectionCount)
            .orElse(0);
    }

    public static Game increasePlayerInjectionCount(Game game, int playerIndex) {
        if (playerIndex < 0) {
            return game;
        }
        var player = game.getPlayers(playerIndex);
        var playerBuilder = player.toBuilder();
        if (!player.getCharacterTop().getIsDead()) {
            playerBuilder.setCharacterTop(player.getCharacterTop().toBuilder()
                .setInjectionCount(player.getCharacterTop().getInjectionCount() + 1));
        } else if (!player.getCharacterBot().getIsDead()) {
            playerBuilder.setCharacterBot(player.getCharacterBot().toBuilder()
                .setInjectionCount(player.getCharacterBot().getInjectionCount() + 1));
        }
        return game.toBuilder()
            .setPlayers(playerIndex, playerBuilder)
            .build();
    }

    public static boolean doesPlayerHasThisCharacter(Game game, int playerIndex,
        CharacterType characterType) {
        if (playerIndex < 0) {
            return false;
        }
        var player = game.getPlayers(playerIndex);
        return player.getCharacterTop().getCharacterType().equals(characterType)
            || player.getCharacterBot().getCharacterType().equals(characterType);
    }

    public static Game markCharacterAsDead(Game game, Collection<CharacterIndex> characterIndices) {
        var gameStatusBuilder = game.getGameStatus().toBuilder();
        var builder = game.toBuilder();
        var deadMap = characterIndices.stream()
            .collect(Collectors.groupingBy(CharacterIndex::getPlayerIndex));
        deadMap.forEach((playerIndex, characters) -> {
            var playerBuilder = game.getPlayers(playerIndex).toBuilder();
            characters.forEach(characterIndex -> {
                if (characterIndex.getCharacterIndex() == BOT_CHARACTER) {
                    playerBuilder.setCharacterBot(
                        game.getPlayers(playerIndex).getCharacterBot().toBuilder().setIsDead(true));
                }
                if (characterIndex.getCharacterIndex() == TOP_CHARACTER) {
                    playerBuilder.setCharacterTop(
                        game.getPlayers(playerIndex).getCharacterTop().toBuilder().setIsDead(true));
                }
            });
            builder.setPlayers(playerIndex, playerBuilder);
        });
        var tmpGame = builder.build();
        gameStatusBuilder.clearOnSurfaceWolves()
            .addAllOnSurfaceWolves(tmpGame.getPlayersList().stream()
            .filter(player -> isWolfOnSurface(tmpGame, player))
            .map(Player::getIndex)
            .collect(Collectors.toList()));
        return builder.setGameStatus(gameStatusBuilder).build();
    }

    public static Set<CharacterIndex> getAllDeadCharacters(Game game,
        Set<CharacterIndex> originalDeadCharacters) {
        var gameStatus = game.getGameStatus();
        var deadCharacters = new HashSet<>(originalDeadCharacters);
        // 被连带弄死的人
        int currentSize = 0;
        while (deadCharacters.size() != currentSize) {
            currentSize = deadCharacters.size();
            var newDeadCharacters = new HashSet<CharacterIndex>();
            for (CharacterIndex characterIndex : deadCharacters) {
                var deadCharacter = GameAccessor.getCharacter(game, characterIndex);
                // 狼美人
                if (deadCharacter.getCharacterType() == CharacterType.WOLF_BEAUTY) {
                    newDeadCharacters.addAll(gameStatus.getAffectedCharactersList());
                }

                // 死透了
                var deadPlayerIndex = characterIndex.getPlayerIndex();
                if (characterIndex.getCharacterIndex() == BOT_CHARACTER) {
                    // 魅狼死了
                    if (GameAccessor.doesPlayerHasThisCharacter(game, deadPlayerIndex,
                        CharacterType.SUCCUBUS)) {
                        var characterIndexBuilder = CharacterIndex.newBuilder()
                            .setPlayerIndex(gameStatus.getLoverIndex());
                        newDeadCharacters
                            .add(characterIndexBuilder.setCharacterIndex(BOT_CHARACTER).build());
                        newDeadCharacters
                            .add(characterIndexBuilder.setCharacterIndex(TOP_CHARACTER).build());
                    }
                    // 被连着的人死了
                    if (deadPlayerIndex == gameStatus.getLoverIndex()) {
                        var succubusPlayerIndex = GameAccessor
                            .getPlayerOfType(game, CharacterType.SUCCUBUS);
                        var characterIndexBuilder = CharacterIndex.newBuilder()
                            .setPlayerIndex(succubusPlayerIndex);
                        newDeadCharacters
                            .add(characterIndexBuilder.setCharacterIndex(BOT_CHARACTER).build());
                        newDeadCharacters
                            .add(characterIndexBuilder.setCharacterIndex(TOP_CHARACTER).build());
                    }
                }

                newDeadCharacters.stream()
                    .filter(c -> !gameStatus.getDeadCharactersList().contains(c))
                    .forEach(deadCharacters::add);
            }
        }
        return deadCharacters;
    }
}
