<template>
  <div class='game-view'>
    <GameInfo :game='game'></GameInfo>
    <Table :characterReadableNames='characterReadableNames'
           :characterCurrentEffectiveIndex='characterCurrentEffectiveIndex'
           :candidatePlayersIndex='candidatePlayersIndex'
           :selected='selectedIndex'
           v-on:select='select'></Table>
    <p v-if='lastTurnMessage'>{{ lastTurnMessage }}</p>
    <p v-if='lastTurnMessage'><b>等完成上述步骤后</b></p>
    <p v-if='nextTurnMessage'>{{ nextTurnMessage }}</p>
    <button type="button" class="btn btn-success btn-block"
      @click='nextStep'
      :disabled='!ready'>
      {{ ready ? ("下一步" + (candidatePlayersIndex.includes(-1) ? " (可以不选)" : "")) : "需要选一个角色才能下一步" }}
    </button>

    <button type="button" class="btn btn-danger btn-block"
      @click='confess'
      :disabled='!canConfess'>
      {{ isInConfess ? (selectedIndex < 0 ? "选择自爆的狼人或再次点击取消" : "确认") : (canConfess ? "狼人自爆" : "现在狼不能自爆") }}
    </button>

    <button type="button" class="btn btn-danger btn-block"
      @click='pardon'
      :disabled='!canPardon'>
      {{ isInPardon ? (selectedIndex < 0 ? "选择赦免的人或再次点击取消" : "确认") : (canPardon ? "赦免" : "现在公主不能赦免") }}
    </button>

    <button type="button" class="btn btn-danger btn-block"
      @click='duel'
      :disabled='!canDuel'>
      {{ isInDuel ? (selectedIndex < 0 ? "选择决斗的对象或再次点击取消" : "确认") : (canConfess ? "决斗" : "现在骑士不能决斗") }}
    </button>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { getGame, updateGame } from '@/lib/MafiaServiceConnector';
import { toReadableName, fromEnumName, toEnumName, fromReadableName, NO_PLAYER } from '@/lib/MafiaConstants';
import { canPardon, canDuel } from '@/lib/MafiaHelper';
import { CharacterType, Game, Player, Event, EventType } from '@/protos/game_pb';
import * as _ from 'lodash';

import Table from '@/components/Table.vue';
import GameInfo from '@/components/GameInfo.vue';

@Component({
  components: {
    Table,
    GameInfo,
  },
})
export default class GameView extends Vue {

  private gameId: string = '';
  private game: Game = new Game();
  private characterReadableNames: Array<[string, string]> = [];
  private selectedIndex: number = NO_PLAYER;

  private isInConfess: boolean = false;
  private isInPardon: boolean = false;
  private isInDuel: boolean = false;

  private async created() {
    this.gameId = this.$route.query.id as string;
    if (this.gameId === undefined || this.gameId.length <= 0) {
      this.$router.push('/game/start');
    }
    this.game = await getGame(this.gameId);
    this.characterReadableNames = this.game.getPlayersList().map(
      (player): [string, string] =>
        [toReadableName(player.getCharacterTop()!.getCharacterType()),
         toReadableName(player.getCharacterBot()!.getCharacterType())]);
  }

  private get characterCurrentEffectiveIndex() {
    return this.game.getPlayersList().map((player) => this.getPlayerCurrentCharacterIndex(player));
  }

  private get candidatePlayersIndex()  {
    return this.isInConfess
      ? this.game.getGameStatus()!.getOnSurfaceWolvesList()
      : (this.game.getNextEvent()
          ? this.game.getNextEvent()!.getCandidateTargetsList()
          : []);
  }

  private get lastTurnMessage() {
    return this.game.getNextEvent()
      ? this.game.getNextEvent()!.getLastEventResponse()
      : undefined;
  }

  private get nextTurnMessage() {
    return this.game.getNextEvent()
      ? this.game.getNextEvent()!.getCurrentEventResponse()
      : undefined;
  }

  private getPlayerCurrentCharacterIndex(player: Player) {
    return !player.getCharacterTop()!.getIsDead()
      ? 0 : !player.getCharacterBot()!.getIsDead() ? 1 : 2;
  }

  private select(index: number) {
    if (this.selectedIndex === index) {
      this.selectedIndex = NO_PLAYER;
    } else {
      this.selectedIndex = index;
    }
  }

  private get ready() {
    return !this.isInConfess && (this.candidatePlayersIndex.length === 0
         || this.candidatePlayersIndex.includes(this.selectedIndex));
  }

  private async nextStep() {
    await this.sendEventAndClearSelectedIndex(
      this.game.getNextEvent()!.getEventType());
  }

  private async confess() {
    if (!this.isInConfess) {
      this.preConfess();
    } else {
      await this.confirmConfess();
    }
  }

  private get canConfess() {
    return this.game.getNextEvent()
        && (this.game.getNextEvent()!.getEventType() === EventType.SPEECH
            || this.game.getNextEvent()!.getEventType() === EventType.VOTE)
        && this.game.getGameStatus()
        && this.game.getGameStatus()!.getOnSurfaceWolvesList().length > 0;
  }

  private preConfess() {
    this.isInConfess = true;
  }

  private async confirmConfess() {
    if (this.selectedIndex !== NO_PLAYER) {
      await this.sendEventAndClearSelectedIndex(EventType.CONFESS);
    }
    this.isInConfess = false;
  }

  private async pardon() {
    if (!this.isInPardon) {
      this.prePardon();
    } else {
      await this.confirmPardon();
    }
  }

  private get canPardon() {
    return canPardon(this.game);
  }

  private prePardon() {
    this.isInPardon = true;
  }

  private async confirmPardon() {
    if (this.selectedIndex !== NO_PLAYER) {
      await this.sendEventAndClearSelectedIndex(EventType.PARDON);
    }
    this.isInPardon = false;
  }

  private async duel() {
    if (!this.isInDuel) {
      this.preDuel();
    } else {
      await this.confirmDuel();
    }
  }

  private get canDuel() {
    return canDuel(this.game);
  }

  private preDuel() {
    this.isInDuel = true;
  }

  private async confirmDuel() {
    if (this.selectedIndex !== NO_PLAYER) {
      await this.sendEventAndClearSelectedIndex(EventType.DUEL);
    }
    this.isInDuel = false;
  }

  private async sendEventAndClearSelectedIndex(eventType: EventType) {
    const event = new Event();
    event.setEventType(eventType);
    event.setTimestamp(new Date().getTime());
    event.setTurnId(this.game.getTurnId());
    event.setTargetsList([this.selectedIndex]);
    this.selectedIndex = NO_PLAYER;
    this.game = await updateGame(this.gameId, event);
  }
}
</script>

<style scoped>
.btn {
  border-radius: 0px;
}
</style>
