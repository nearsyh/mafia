package com.nearsyh.mafia.common;

import static com.nearsyh.mafia.common.GameAccessor.BOT_CHARACTER;

import com.google.common.collect.ImmutableMap;
import com.nearsyh.mafia.protos.CharacterIndex;
import com.nearsyh.mafia.protos.CharacterType;
import com.nearsyh.mafia.protos.Game;
import java.util.Map;

public final class GameConstants {

    private GameConstants() {}

    private static final Map<CharacterType, String> READABLE_NAME_MAP = ImmutableMap.<CharacterType, String>builder()
        .put(CharacterType.WEREWOLF, "狼")
        .put(CharacterType.NORMAL_VILLAGER, "普通村民")
        .put(CharacterType.SEER, "预言家")
        .put(CharacterType.DOCTOR, "医生")
        .put(CharacterType.WITCH, "女巫")
        .put(CharacterType.GUARDIAN, "守卫")
        .put(CharacterType.PENGUIN, "企鹅")
        .put(CharacterType.TOAST_BAKER, "面包师傅")
        .put(CharacterType.WOLF_BEAUTY, "狼美人")
        .put(CharacterType.WOLF_CHILD, "野孩子")
        .put(CharacterType.SUCCUBUS, "魅狼")
        .put(CharacterType.MUTER, "禁言长老")
        .put(CharacterType.PRINCESS, "公主")
        .put(CharacterType.KNIGHT, "骑士")
        .put(CharacterType.FOX, "狐狸")
        .build();

    public static String toReadableName(CharacterType characterType) {
        return READABLE_NAME_MAP.get(characterType);
    }

    public static String toPlayerName(Game game, CharacterIndex characterIndex) {
        var player = game.getPlayers(characterIndex.getPlayerIndex());
        var characterType =
            characterIndex.getCharacterIndex() == BOT_CHARACTER ? player.getCharacterBot()
                .getCharacterType() : player.getCharacterTop().getCharacterType();
        return String.format("%s号玩家(%s)", player.getIndex() + 1, toReadableName(characterType));
    }
}
