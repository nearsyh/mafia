<template>
  <div class='start-view'>
    <div class='PlayersNumberSetter input-group'>
      <div class='input-group-prepend'>
        <span class='input-group-text'>设置玩家数量</span>
      </div>
      <input v-model.number='playersNumber' type='number' class='form-control'
        v-on:change='updatePlayersNumber'>
    </div>

    <div v-if='playersNumber > 0'>
      <div v-for='(usedCharacter, index) of usedCharacters' :key='index' class='input-group'>
        <select class="custom-select" v-model='usedCharacters[index]'>
          <option v-if='usedCharacter.length > 0' selected :value='usedCharacter'>{{ usedCharacter }}</option>
          <option v-for='(supportedCharacter, index) of supportedCharacters'
            :value='supportedCharacter' :key='index'
            v-if='usedCharacters.indexOf(supportedCharacter) < 0'>
            {{ supportedCharacter }}
          </option>
        </select>
        <input v-model.number='charactersCount[index]' type='number' class='form-control'
          :class='{disabled: index === 0}'>
        <button style='float:left' class="btn btn-danger"
          :disabled='index === 0'
          v-on:click='deleteCharacterType(index)'>
          删除
        </button>
      </div>
      <button type="button" class="btn btn-primary btn-block" @click='addCharacterType'>
        添加
      </button>
      <button type="button" class="btn btn-success btn-block"
        :disabled='totalCharacters !== playersNumber * 2' 
        @click='startGame'>
        {{ totalCharacters < playersNumber * 2
          ? "角色数量还不够哟"
          : totalCharacters > playersNumber * 2 ? "角色数量太多了啦" : "发车!" }}
      </button>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { getSupportedCharacterTypes, createGame } from '@/lib/MafiaServiceConnector';
import { toReadableName, fromEnumName, toEnumName, fromReadableName } from '@/lib/MafiaConstants';
import { CharacterType } from '@/protos/game_pb';
import * as _ from 'lodash';

@Component
export default class Start extends Vue {

  private supportedCharacters: string[] = [];
  private playersNumber: number = 0;
  private usedCharacters: string[] = [];
  private charactersCount: number[] = [];

  private async beforeCreate() {
    const supportedCharactersEnumNames = await getSupportedCharacterTypes();
    this.supportedCharacters = supportedCharactersEnumNames
      .map(fromEnumName).map(toReadableName);
  }

  private updatePlayersNumber() {
    this.usedCharacters = [toReadableName(CharacterType.NORMAL_VILLAGER)];
    this.charactersCount = [this.playersNumber];
  }

  private deleteCharacterType(index: number) {
    this.usedCharacters.splice(index, 1);
    this.charactersCount.splice(index, 1);
  }

  private addCharacterType() {
    this.usedCharacters.push('');
    this.charactersCount.push(0);
  }

  private get totalCharacters() {
    return this.charactersCount.length <= 0 ? 0 : this.charactersCount.reduce((p, c) => p + c);
  }

  private async startGame() {
    if (confirm('确认发车吗?')) {
      const ret = new Map<string, number>();
      this.usedCharacters.forEach((characterReadableName, index) => {
        ret.set(toEnumName(fromReadableName(characterReadableName)), this.charactersCount[index]);
      });
      const game = await createGame(ret);
      this.$router.push(`/game?id=${game.getId()}`);
    }
  }
}
</script>

<style scoped>
.btn {
  border-radius: 0px;
}
</style>
