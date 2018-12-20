<template>
  <div class='table'>
    <div class='player-container container-fluid row'>
      <div v-for='(characterReadableName, index) of characterReadableNames' class='half' :key='index'>
        <div class='player-view card'>
          <div class='card-body' @click="select(index)"
            :class='{candidate: candidatePlayersIndex.includes(index),
                     selected: selected === index}'>
            <h6 class="card-title">{{ index + 1 }} 号玩家</h6>
            <p class="card-text" 
              :class='{dead: characterCurrentEffectiveIndex[index] > 0,
                       effective: characterCurrentEffectiveIndex[index] === 0}'>
              {{ characterReadableName[0] }}
            </p>
            <p class="card-text"
              :class='{dead: characterCurrentEffectiveIndex[index] > 1,
                       effective: characterCurrentEffectiveIndex[index] === 1}'>
              {{ characterReadableName[1] }}
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Emit } from 'vue-property-decorator';
import { NO_PLAYER } from '@/lib/MafiaConstants';

@Component
export default class Table extends Vue {

  @Prop()
  private characterReadableNames!: Array<[string, string]>;

  @Prop()
  private characterCurrentEffectiveIndex!: number[];

  @Prop()
  private candidatePlayersIndex!: number[];

  @Prop()
  private selected!: number;

  private select(index: number) {
    if (this.candidatePlayersIndex.includes(index)) {
      this.emit(index);
    }
  }

  @Emit('select')
  private emit(index: number) {
    return index;
  }

}
</script>

<style scoped>
.btn {
  border-radius: 0px;
}

.dead {
  text-decoration: line-through;
}

.effective {
  font-weight: bold;
}

.candidate {
  background-color: aquamarine;
}

.selected {
  background-color: green;
}

.player-container {
  margin: auto;
}

.card-body {
  padding: 10px;
  padding-bottom: 5px;
}

.player-view {
  margin: 5px;
}

.half {
  width: 33%;
}

.card-title {
  margin-bottom: 5px;
}

.card-text {
  margin-bottom: 5px;
}
</style>
