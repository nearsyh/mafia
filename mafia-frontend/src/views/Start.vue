<template>
  <div class='start-view'>
    <div id='step-1' v-if='!started && !readyToPlay'>
      <div class='PlayersNumberSetter input-group'>
        <div class='input-group-prepend'>
          <span class='input-group-text'>设置玩家数量</span>
        </div>
        <input v-model.number='playersNumber' type='number' class='form-control'
          v-on:change='updatePlayersNumber'>
      </div>
      <div v-if='playersNumber > 0'>
        <div v-for='(usedCharacter, index) of usedCharacters' :key='index' class='input-group'>
          <select class="custom-select" v-model='usedCharacters[index]' :disabled='index <= 3'>
            <option v-if='usedCharacter.length > 0' selected :value='usedCharacter'>{{ usedCharacter + (index <= 3 ? " (固定)" : "")}}</option>
            <option v-for='(supportedCharacter, index) of supportedCharacters'
              :value='supportedCharacter' :key='index'
              v-if='usedCharacters.indexOf(supportedCharacter) < 0'>
              {{ supportedCharacter }}
            </option>
          </select>
          <input v-model.number='charactersCount[index]' type='number' class='form-control'
            :disabled='index <= 3'>
          <button style='float:left' class="btn btn-danger"
            :disabled='index <= 3'
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
            ? `角色数量还不够哟. 差 ${playersNumber * 2 - totalCharacters} 个`
            : totalCharacters > playersNumber * 2 ? `角色数量太多了啦. 多了 ${totalCharacters - playersNumber * 2} 个` : "发车!" }}
        </button>

        <div v-if='templates.length > 0' class='list-group'>
          <a href='#' class='list-group-item list-group-item-action'
            v-for='(template, index) of templates' :key='index'
            @click='useTemplate(template)'>
            预置组合: {{ template }}
          </a>
        </div>

        <div class='PlayersNumberSetter input-group'>
          <div class='input-group-prepend'>
            <span class='input-group-text'>预置组合名字</span>
          </div>
          <input v-model='templateName' type='text' class='form-control'>
          <button type="button" class="btn btn-success"
            :disabled='totalCharacters !== playersNumber * 2 || templateName.length <= 0' 
            @click='upsertTemplate'>
            {{ templateName.length > 0 && templateName === selectedTemplateName ? "更新" : "保存" }}
          </button>
        </div>
      </div>
    </div>

    <div id='step-2' v-if='started && !readyToPlay'>
      <h5 class='title'>{{ this.currentPlayerIndex + 1}} 号玩家</h5>
      <div class='characters-view container-fluid row'>
        <div class='half'>
          <div class='card character-view'>
            <div class='card-body'>
              <h6 class="card-title">第一张</h6>
              <p class="card-text" :class='{hidden: !showCharacters}'>
                {{ characters[0] }}
              </p>
            </div>
          </div>
        </div>
        <div class='half'>
          <div class='card character-view'>
            <div class='card-body'>
              <h6 class="card-title">第二张</h6>
              <p class="card-text" :class='{hidden: !showCharacters}'>
                {{ characters[1] }}
              </p>
            </div>
          </div>
        </div>
      </div>

      <h5 style='padding-top:10px'>最后机会确认一下板子</h5>
      <div class='game-info'>
        <table class="table table-sm">
          <thead>
            <tr>
              <th scope="col">角色名</th>
              <th scope="col">数量</th>
              <th scope="col">角色名</th>
              <th scope="col">数量</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for='(characterName, index) of usedCharacters' :key='index'
              v-if='index % 2 === 0'>
              <th scope="row">{{ usedCharacters[index] }}</th>
              <td>{{ charactersCount[index] }}</td>
              <th v-if='index < usedCharacters.length' scope="row">{{ usedCharacters[index + 1] }}</th>
              <td v-if='index < usedCharacters.length'>{{ charactersCount[index + 1] }}</td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class='row bottom'>
        <button class='col btn btn-success' @click="toggleCharacters">{{ showCharacters ? "隐藏" : "查看" }}</button>
        <button class='col btn btn-primary' @click="swap">交换</button>
        <button class='col btn btn-danger' @click="confirmCharacters">确认</button>
        <button class='col btn btn-primary' @click="previousPlayer">上一位</button>
      </div>
    </div>

    <div id='step-3' v-if='readyToPlay'>
      <h4 class='big-space'>还给上帝吧!</h4>
      <button type="button" class="btn btn-danger btn-block bottom" @click='play'>
        <b>上帝点击开始游戏正式开始</b>
      </button>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { getSupportedCharacterTypes, createGame, swapGame,
         listTemplates, getTemplate, upsertTemplate } from '@/lib/MafiaServiceConnector';
import { toReadableName, fromEnumName, toEnumName, fromReadableName, NO_PLAYER } from '@/lib/MafiaConstants';
import { CharacterType, Game } from '@/protos/game_pb';
import * as _ from 'lodash';

@Component
export default class Start extends Vue {

  private supportedCharacters: string[] = [];
  private playersNumber: number = 0;
  private usedCharacters: string[] = [];
  private charactersCount: number[] = [];

  private templates: string[] = [];
  private templateName: string = '';
  private selectedTemplateName: string = '';

  private game: Game = new Game();
  private currentPlayerIndex: number = NO_PLAYER;
  private characters: string[] = [];
  private showCharacters = false;

  private async beforeCreate() {
    const supportedCharactersEnumNames = await getSupportedCharacterTypes();
    this.supportedCharacters = supportedCharactersEnumNames
      .map(fromEnumName).map(toReadableName);
  }

  private async updatePlayersNumber() {
    this.usedCharacters = [
      toReadableName(CharacterType.NORMAL_VILLAGER),
      toReadableName(CharacterType.SEER),
      toReadableName(CharacterType.WITCH),
      toReadableName(CharacterType.GUARDIAN)];
    this.charactersCount = [
      this.playersNumber,
      1,
      1,
      1];
    this.templates = await listTemplates(this.playersNumber);
  }

  private async useTemplate(templateName: string) {
    this.selectedTemplateName = templateName;
    this.templateName = templateName;
    const template = await getTemplate(this.playersNumber, templateName);
    this.usedCharacters = [
      toReadableName(CharacterType.NORMAL_VILLAGER),
      toReadableName(CharacterType.SEER),
      toReadableName(CharacterType.WITCH),
      toReadableName(CharacterType.GUARDIAN)];
    this.charactersCount = [
      this.playersNumber,
      1,
      1,
      1];
    template.getCountsMap().forEach((count, character) => {
      const type = fromEnumName(character);
      if (type !== CharacterType.NORMAL_VILLAGER
      && type !== CharacterType.SEER
      && type !== CharacterType.WITCH
      && type !== CharacterType.GUARDIAN) {
        this.usedCharacters.push(toReadableName(type));
        this.charactersCount.push(count);
      }
    });
  }

  private async upsertTemplate() {
    if (this.playersNumber > 0 && this.templateName.length > 0) {
      const ret = new Map<string, number>();
      this.usedCharacters.forEach((characterReadableName, index) => {
        ret.set(toEnumName(fromReadableName(characterReadableName)), this.charactersCount[index]);
      });
      await upsertTemplate(this.playersNumber, this.templateName, ret);
      if (!this.templates.includes(this.templateName)) {
        this.templates.push(this.templateName);
      }
    }
  }

  private deleteCharacterType(index: number) {
    this.usedCharacters.splice(index, 1);
    this.charactersCount.splice(index, 1);
  }

  private addCharacterType() {
    this.usedCharacters.push('');
    this.charactersCount.push(1);
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
      this.game = await createGame(ret);
      this.currentPlayerIndex = NO_PLAYER;
      this.nextPlayer();
    }
  }

  private get started() {
    return this.game.getId() && this.game.getId().length > 0;
  }

  private previousPlayer() {
    if (this.currentPlayerIndex > 0) {
      this.showCharacters = false;
      this.currentPlayerIndex -= 1;
      if (this.currentPlayerIndex >= this.playersNumber) {
        return;
      }
      const player = this.game.getPlayersList()[this.currentPlayerIndex];
      this.characters = [
        player.getCharacterTop()!.getCharacterType(),
        player.getCharacterBot()!.getCharacterType()].map(toReadableName);
    }
  }

  private nextPlayer() {
    this.showCharacters = false;
    this.currentPlayerIndex += 1;
    if (this.currentPlayerIndex >= this.playersNumber) {
      return;
    }
    const player = this.game.getPlayersList()[this.currentPlayerIndex];
    this.characters = [
      player.getCharacterTop()!.getCharacterType(),
      player.getCharacterBot()!.getCharacterType()].map(toReadableName);
  }

  private swap() {
    this.characters = [
      this.characters[1],
      this.characters[0]];
  }

  private async confirmCharacters() {
    const player = this.game.getPlayersList()[this.currentPlayerIndex];
    const firstName = toReadableName(player.getCharacterTop()!.getCharacterType());
    if (firstName !== this.characters[0]) {
      this.game = await swapGame(this.game.getId(), this.currentPlayerIndex);
    }
    this.nextPlayer();
  }

  private toggleCharacters() {
    this.showCharacters = !this.showCharacters;
  }

  private get readyToPlay() {
    return this.currentPlayerIndex >= this.playersNumber;
  }

  private play() {
    this.$router.push(`/game?id=${this.game.getId()}`);
  }
}
</script>

<style scoped>
.btn {
  border-radius: 0px;
}

#step-3 {
  height: 100%;
}

.big-space {
  margin-top: 50%;
}

.bottom {
  position:absolute;
  bottom:0;
  height: 100px;
  width: 100%;
  margin: 0px;
}

.card-body {
  padding: 10px;
  padding-bottom: 5px;
}

.characters-view {
  margin: auto;
}

.character-view {
  margin: 10px;
}

.half {
  width: 50%;
}

.card-title {
  margin-bottom: 5px;
}

.card-text {
  margin-bottom: 5px;
}

.hidden {
  background-color: black;
  color: black;
}

.title {
  padding-top: 30px;
}

.game-info {
  margin-top: 20px;
}
</style>
