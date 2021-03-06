package com.nearsyh.mafia.common;

import com.nearsyh.mafia.protos.CharacterType;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class GameValidator {

    private GameValidator() {}

    private static Map<CharacterType, Set<CharacterType>> CONFLICT_TYPES = Map.of(
        CharacterType.WEREWOLF, Set.of(CharacterType.SEER, CharacterType.TOAST_BAKER),
        CharacterType.WOLF_BEAUTY, Set.of(CharacterType.SEER, CharacterType.TOAST_BAKER),
        CharacterType.SUCCUBUS, Set.of(CharacterType.SEER, CharacterType.TOAST_BAKER));

    public static boolean isCharacterListValid(List<CharacterType> characterTypeList) {
        for (int i = 0; i < characterTypeList.size(); i += 2) {
            if (!isCharacterPairValid(characterTypeList.get(i), characterTypeList.get(i + 1))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isCharacterPairValid(CharacterType left, CharacterType right) {
        return !CONFLICT_TYPES.getOrDefault(left, Set.of()).contains(right)
            && !CONFLICT_TYPES.getOrDefault(right, Set.of()).contains(left);
    }
}
