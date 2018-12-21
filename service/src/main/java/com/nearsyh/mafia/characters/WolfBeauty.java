package com.nearsyh.mafia.characters;

import static com.nearsyh.mafia.common.GameAccessor.NO_PLAYER;

import com.google.common.base.Preconditions;
import com.nearsyh.mafia.common.GameAccessor;
import com.nearsyh.mafia.protos.CharacterType;
import com.nearsyh.mafia.protos.Event;
import com.nearsyh.mafia.protos.EventType;
import com.nearsyh.mafia.protos.Game;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class WolfBeauty extends AbstractCharacter {

    private static final WolfBeauty INSTANCE = new WolfBeauty();

    static {
        registerEventListeners(EventType.AFFECT, INSTANCE.getCharacterType(), INSTANCE::affect);
        registerPreEventListeners(EventType.AFFECT, INSTANCE::preAffect);
    }

    private WolfBeauty() {
        super(CharacterType.WOLF_BEAUTY);
    }

    private Event.Builder preAffect(Game game, Event.Builder nextEventBuilder) {
        var message = "选择一个人魅惑";

        var isOnTop = GameAccessor.isCharacterTypeOnSurface(game, CharacterType.WOLF_BEAUTY);
        if (!isOnTop) {
            return nextEventBuilder.setCurrentEventResponse(message + " (不在上面, 直接下一步)")
                .clearCandidateTargets()
                .addCandidateTargets(NO_PLAYER);
        }

        var doesAffectLastNight = GameAccessor.doesAffectLastNight(game);
        var candidatePlayers = doesAffectLastNight
            ? new HashSet<>(GameAccessor.allAlivePlayersIndex(game))
            : Set.of(NO_PLAYER);
        if (doesAffectLastNight) {
            message += " (上回合魅惑了, 这一回合不能魅惑)";
        }
        return nextEventBuilder.clearCandidateTargets()
            .setCurrentEventResponse(message)
            .addAllCandidateTargets(candidatePlayers);
    }

    private Game affect(Game game, Event event) {
        if (event.getTargets(0) < 0) {
            return game;
        }
        var characterIndexOpt = GameAccessor
            .getCurrentAliveCharacterIndex(game, event.getTargets(0));
        Preconditions.checkArgument(characterIndexOpt.isPresent());
        return game.toBuilder()
            .setCurrentTurn(game.getCurrentTurn().toBuilder()
                .setAffectedCharacter(characterIndexOpt.get()))
            .build();
    }
}
