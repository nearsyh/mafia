package com.nearsyh.mafia.characters;

import com.nearsyh.mafia.protos.CharacterType;
import com.nearsyh.mafia.protos.Event;
import com.nearsyh.mafia.protos.EventType;
import com.nearsyh.mafia.protos.Game;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractCharacter implements Character {

    private static Map<CharacterType, Character> REGISTERED_CHARACTERS = new HashMap<>();
    private static Map<EventType, CharacterAction> EVENT_LISTENERS = new HashMap<>();

    private static void registerCharacter(CharacterType characterType, Character character) {
        REGISTERED_CHARACTERS.put(characterType, character);
    }

    public static Character getCharacter(CharacterType characterType) {
        return REGISTERED_CHARACTERS.get(characterType);
    }

    public static void registerEventListeners(EventType eventType,
        CharacterAction handler) {
        EVENT_LISTENERS.put(eventType, handler);
    }

    public static CharacterAction getEventListeners(EventType eventType) {
        return EVENT_LISTENERS.getOrDefault(eventType, CharacterAction.NOOP);
    }

    public static Event nextEvent(Game game, Event currentEvent) {
        return null;
    }

    private final CharacterType characterType;

    AbstractCharacter(CharacterType characterType) {
        this.characterType = characterType;
        registerCharacter(this.characterType, this);
    }

    @Override
    public CharacterType getCharacterType() {
        return characterType;
    }
}
