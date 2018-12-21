package com.nearsyh.mafia.characters;

import static com.nearsyh.mafia.common.GameAccessor.NO_PLAYER;

import com.nearsyh.mafia.common.GameAccessor;
import com.nearsyh.mafia.protos.CharacterType;
import com.nearsyh.mafia.protos.Event;
import com.nearsyh.mafia.protos.EventType;
import com.nearsyh.mafia.protos.Game;
import org.springframework.stereotype.Component;

@Component
public class Fox extends AbstractCharacter {

    private static final Fox INSTANCE = new Fox();

    static {
        registerEventListeners(EventType.FOX_VERIFY, INSTANCE.getCharacterType(),
            INSTANCE::foxVerify);
        registerPreEventListeners(EventType.FOX_VERIFY, INSTANCE::preFoxVerify);
    }

    private Fox() {
        super(CharacterType.FOX);
    }

    private Event.Builder preFoxVerify(Game game, Event.Builder nextEventBuilder) {
        var message = "这个手势是有狼, 这个手势是没有. 狐狸你今晚发动技能的情况是这个 (%s)";
        nextEventBuilder = nextEventBuilder.clearCandidateTargets()
            .addCandidateTargets(NO_PLAYER);
        var hasFox = !GameAccessor.playersWhoCanUseSkill(game, getCharacterType()).isEmpty();
        if (!hasFox) {
            return nextEventBuilder
                .setCurrentEventResponse(String.format(message, "不在上面, 直接下一步"));
        }
        var hasNotFrozenFox = !GameAccessor.notFrozenPlayers(game, getCharacterType()).isEmpty();
        if (!hasNotFrozenFox) {
            return nextEventBuilder
                .setCurrentEventResponse(String.format(message, "被冻住了, 直接下一步"));
        }

        var hasBadGuys = GameAccessor
            .doFollowingPlayersSeemBad(game,
                GameAccessor.getPlayerOfType(game, getCharacterType()));
        return nextEventBuilder
            .setCurrentEventResponse(String.format(message, hasBadGuys ? "有狼" : "没狼"));
    }

    private Game foxVerify(Game game, Event event) {
        return game;
    }
}
