package com.nearsyh.mafia.characters;

import com.nearsyh.mafia.common.GameAccessor;
import com.nearsyh.mafia.protos.CharacterType;
import com.nearsyh.mafia.protos.Event;
import com.nearsyh.mafia.protos.EventType;
import com.nearsyh.mafia.protos.Game;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class Succubus extends AbstractCharacter {

    private static final Succubus INSTANCE = new Succubus();

    static {
        registerEventListeners(EventType.CHOOSE_LOVER, INSTANCE.getCharacterType(),
            INSTANCE::chooseLover);
        registerPreEventListeners(EventType.CHOOSE_LOVER, INSTANCE::preChooseLover);
    }

    private Succubus() {
        super(CharacterType.SUCCUBUS);
    }

    private Event.Builder preChooseLover(Game game, Event.Builder nextEventBuilder) {
        return nextEventBuilder.setCurrentEventResponse("魅狼选择一个情侣")
            .clearCandidateTargets()
            .addAllCandidateTargets(
                GameAccessor.allAlivePlayersIndex(game)
                    .stream().filter(
                    index -> index != GameAccessor.getPlayerOfType(game, getCharacterType()))
                    .collect(Collectors.toList()));
    }

    private Game chooseLover(Game game, Event event) {
        return game.toBuilder()
            .setGameStatus(game.getGameStatus().toBuilder()
                .setLoverIndex(event.getTargets(0)))
            .setNextEvent(game.getNextEvent().toBuilder()
                .setLastEventResponse("请被我碰到手的人睁开双眼确认对方"))
            .build();
    }
}
