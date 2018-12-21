package com.nearsyh.mafia.characters;

import com.nearsyh.mafia.common.GameAccessor;
import com.nearsyh.mafia.protos.CharacterType;
import com.nearsyh.mafia.protos.Event;
import com.nearsyh.mafia.protos.EventType;
import com.nearsyh.mafia.protos.Game;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractCharacter implements Character {

    private static Map<CharacterType, Character> REGISTERED_CHARACTERS = new HashMap<>();
    private static Map<EventType, Set<CharacterType>> EVENT_LISTENER_TYPES = new HashMap<>();
    private static Map<EventType, CharacterAction> EVENT_LISTENERS = new HashMap<>();
    private static Map<EventType, CharacterPreAction> PRE_EVENT_LISTENER = new HashMap<>();

    private static void registerCharacter(CharacterType characterType, Character character) {
        REGISTERED_CHARACTERS.put(characterType, character);
    }

    public static Character getCharacter(CharacterType characterType) {
        return REGISTERED_CHARACTERS.get(characterType);
    }

    public static Set<CharacterType> allSupportedCharacterTypes() {
        return REGISTERED_CHARACTERS.keySet();
    }

    static void registerEventListeners(EventType eventType,
        CharacterType characterType,
        CharacterAction handler) {
        EVENT_LISTENER_TYPES.computeIfAbsent(eventType, any -> new HashSet<>()).add(characterType);
        EVENT_LISTENERS.put(eventType, handler);
    }

    static void registerPreEventListeners(EventType eventType,
        CharacterPreAction handler) {
        PRE_EVENT_LISTENER.put(eventType, handler);
    }

    public static Game handle(Game game, Event event) {
        game = game.toBuilder().clearNextEvent().build();
        var updateGame = getEventListeners(event.getEventType())
            .handle(game, event);
        return updateGame.toBuilder()
            .setNextEvent(nextEvent(updateGame, event))
            .build();
    }

    private static CharacterAction getEventListeners(EventType eventType) {
        return EVENT_LISTENERS.getOrDefault(eventType, CharacterAction.NOOP);
    }

    private static CharacterPreAction getPreEventListener(EventType nextEventType) {
        return PRE_EVENT_LISTENER.getOrDefault(nextEventType, CharacterPreAction.NOOP);
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

    private static List<EventType> PRECEDENCE = List.of(
        EventType.SUNSET,
        EventType.CHOOSE_IDOL,
        EventType.CHOOSE_LOVER,
        EventType.FREEZE,
        EventType.TOAST,
        EventType.INJECT,
        EventType.GUARD,
        EventType.KILL,
        EventType.AFFECT,
        EventType.CURE,
        EventType.TOXIC,
        EventType.VERIFY,
        EventType.FOX_VERIFY,
        EventType.MUTE,
        EventType.SUNRISE,
        EventType.ANNOUNCE_DEATH,
        EventType.SPEECH,
        EventType.VOTE,
        EventType.SUNSET
    );
    private static int START_OF_DAY = PRECEDENCE.indexOf(EventType.SUNRISE);

    private static EventType nextEventType(Game game, Event currentEvent) {
        var currentEventType = currentEvent.getEventType();
        var currentPrecedence = PRECEDENCE.indexOf(currentEventType);
        if (currentPrecedence >= 0) {
            // 白天就按部就班
            if (currentPrecedence >= START_OF_DAY) {
                return PRECEDENCE.get(currentPrecedence + 1);
            }
            // 晚上就要根据活着的角色决定.
            for (int i = currentPrecedence + 1; i <= START_OF_DAY; i++) {
                var nextEventTypeCandidate = PRECEDENCE.get(i);
                if (nextEventTypeCandidate == EventType.CHOOSE_IDOL
                    || nextEventTypeCandidate == EventType.CHOOSE_LOVER) {
                    if (game.getTurnId() > 1) {
                        continue;
                    }
                }
                if (nextEventTypeCandidate == EventType.SUNRISE) {
                    return nextEventTypeCandidate;
                } else if (nextEventTypeCandidate == EventType.KILL) {
                    return nextEventTypeCandidate;
                } else {
                    var characterTypes = EVENT_LISTENER_TYPES.get(nextEventTypeCandidate);
                    if (characterTypes != null
                        && GameAccessor.hasAliveCharacterInGame(game, characterTypes)) {
                        return nextEventTypeCandidate;
                    }
                }
            }
            return PRECEDENCE.get(currentPrecedence + 1);
        } else {
            switch (currentEventType) {
                case DUEL:
                case PARDON:
                    return EventType.SPEECH;
                case CONFESS:
                    return EventType.SUNSET;
            }
        }
        return EventType.SUNSET;
    }

    private static Event.Builder nextEvent(Game game, Event currentEvent) {
        var eventBuilder = game.getNextEvent().toBuilder();
        var nextEventType = nextEventType(game, currentEvent);
        eventBuilder.setEventType(nextEventType);
        return getPreEventListener(nextEventType).preHandle(game, eventBuilder);
    }
}
