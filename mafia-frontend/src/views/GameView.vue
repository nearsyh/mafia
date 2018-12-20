<template>
  <div class='game-view'>
    <GameInfo :game='game'></GameInfo>
    <Table :characterReadableNames='characterReadableNames'
           :characterCurrentEffectiveIndex='characterCurrentEffectiveIndex'
           :candidatePlayersIndex='candidatePlayersIndex'
           v-on:select='select'></Table>
    <p v-if='lastTurnMessage'>{{ lastTurnMessage }}</p>
    <p v-if='nextTurnMessage'>{{ nextTurnMessage }}</p>
    <button type="button" class="btn btn-success btn-block"
      @click='nextStep'
      :disabled='!ready'>
      下一步
    </button>

    <button type="button" class="btn btn-danger btn-block"
      @click='confess'
      :disabled='!canConfess'>
      {{ isInConfess ? (selectedIndex < 0 ? "选择自爆的狼人" : "确认") : "狼人自爆" }}
    </button>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { getGame, updateGame } from '@/lib/MafiaServiceConnector';
import { toReadableName, fromEnumName, toEnumName, fromReadableName, NO_PLAYER } from '@/lib/MafiaConstants';
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
    this.selectedIndex = index;
  }

  private get ready() {
    return this.candidatePlayersIndex.length === 0
        || this.candidatePlayersIndex.includes(this.selectedIndex);
  }

  private async nextStep() {
    const event = new Event();
    event.setEventType(this.game.getNextEvent()!.getEventType());
    event.setTimestamp(new Date().getTime());
    event.setTurnId(this.game.getTurnId());
    event.setTargetsList([this.selectedIndex]);
    this.selectedIndex = NO_PLAYER;
    this.game = await updateGame(this.gameId, event);
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
    const event = new Event();
    event.setEventType(EventType.CONFESS);
    event.setTimestamp(new Date().getTime());
    event.setTurnId(this.game.getTurnId());
    event.setTargetsList([this.selectedIndex]);
    this.selectedIndex = NO_PLAYER;
    this.game = await updateGame(this.gameId, event);
    this.isInConfess = false;
  }
}
</script>

<style scoped>
.btn {
  border-radius: 0px;
}
</style>
