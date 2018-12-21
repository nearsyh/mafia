package com.nearsyh.mafia.characters;

import static com.nearsyh.mafia.common.GameAccessor.NO_PLAYER;

import com.google.common.base.Preconditions;
import com.nearsyh.mafia.common.GameAccessor;
import com.nearsyh.mafia.protos.CharacterType;
import com.nearsyh.mafia.protos.Event;
import com.nearsyh.mafia.protos.EventType;
import com.nearsyh.mafia.protos.Game;
import org.springframework.stereotype.Component;

@Component
public class Princess extends AbstractCharacter {

    private static final Princess INSTANCE = new Princess();

    static {
        registerEventListeners(EventType.PARDON, INSTANCE.getCharacterType(), INSTANCE::pardon);
    }

    private Princess() {
        super(CharacterType.PRINCESS);
    }

    private Game pardon(Game game, Event event) {
        Preconditions
            .checkArgument(!GameAccessor.playersWhoCanUseSkill(game, getCharacterType()).isEmpty());
        Preconditions.checkArgument(game.getGameStatus().getPardonIndex() < 0);
        return game.toBuilder()
            .setGameStatus(game.getGameStatus().toBuilder().setPardonIndex(event.getTargets(0)))
            .build();
    }
}
