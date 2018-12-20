<template>
  <div class='game-info'>
    <h5>当前是第 {{ game.getTurnId() }} 回合.</h5>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { getSupportedCharacterTypes, createGame } from '@/lib/MafiaServiceConnector';
import { toReadableName, fromEnumName, toEnumName, fromReadableName } from '@/lib/MafiaConstants';
import { CharacterType, Game } from '@/protos/game_pb';
import * as _ from 'lodash';

@Component
export default class GameInfo extends Vue {

  @Prop()
  private game!: Game;
}
</script>

<style scoped>
</style>
