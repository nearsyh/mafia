package com.nearsyh.mafia.characters;

import com.nearsyh.mafia.common.GameAccessor;
import com.nearsyh.mafia.protos.CharacterType;
import com.nearsyh.mafia.protos.Event;
import com.nearsyh.mafia.protos.EventType;
import com.nearsyh.mafia.protos.Game;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class WolfChild extends AbstractCharacter {

    private static final WolfChild INSTANCE = new WolfChild();

    static {
        registerEventListeners(EventType.CHOOSE_IDOL, INSTANCE.getCharacterType(),
            INSTANCE::chooseIdol);
        registerPreEventListeners(EventType.CHOOSE_IDOL, INSTANCE::preChooseIdol);
    }

    private WolfChild() {
        super(CharacterType.WOLF_CHILD);
    }

    private Event.Builder preChooseIdol(Game game, Event.Builder nextEventBuilder) {
        return nextEventBuilder
            .setCurrentEventResponse("野孩子选择一个偶像")
            .clearCandidateTargets()
            .addAllCandidateTargets(
                GameAccessor.allAlivePlayersIndex(game)
                    .stream().filter(
                    index -> index != GameAccessor.getPlayerOfType(game, getCharacterType()))
                    .collect(Collectors.toList()));
    }

    private Game chooseIdol(Game game, Event event) {
        return game.toBuilder()
            .setGameStatus(game.getGameStatus().toBuilder()
                .setIdolIndex(event.getTargets(0)))
            .setNextEvent(game.getNextEvent().toBuilder())
            .build();
    }
}
