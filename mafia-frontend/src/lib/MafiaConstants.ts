import { CharacterType } from '@/protos/game_pb';
import * as _ from 'lodash';

function getFromMap<K, V>(k: K, defaultV: V, map: Map<K, V>): V {
  return map.has(k) ? map.get(k)! : defaultV;
}

const READABLE_NAME_PAIRS: Array<[CharacterType, string]> =
  [[CharacterType.NORMAL_VILLAGER, '普通村民'],
   [CharacterType.WEREWOLF, '狼人'],
   [CharacterType.SEER, '预言家'],
   [CharacterType.DOCTOR, '医生'],
   [CharacterType.WITCH, '女巫'],
   [CharacterType.GUARDIAN, '守卫'],
   [CharacterType.PENGUIN, '企鹅'],
   [CharacterType.TOAST_BAKER, '面包师傅'],
   [CharacterType.WOLF_BEAUTY, '狼美人'],
   [CharacterType.WOLF_CHILD, '野孩子'],
   [CharacterType.SUCCUBUS, '魅狼'],
   [CharacterType.MUTER, '禁言长老'],
   [CharacterType.PRINCESS, '公主'],
   [CharacterType.KNIGHT, '骑士'],
   [CharacterType.FOX, '狐狸']];

const READABLE_NAME_MAP = new Map<CharacterType, string>(READABLE_NAME_PAIRS);

const READABLE_NAME_MAP_INVERSE = new Map<string, CharacterType>(
  READABLE_NAME_PAIRS.map((pair): [string, CharacterType] => [pair[1], pair[0]]));

export function fromReadableName(readableName: string) {
  return getFromMap(readableName, CharacterType.CHARACTER_TYPE_UNSPECIFIED, READABLE_NAME_MAP_INVERSE);
}

export function toReadableName(characterType: CharacterType) {
  return getFromMap(characterType, '未知角色', READABLE_NAME_MAP);
}

const ENUM_NAME_PAIRS: Array<[CharacterType, string]> =
  [[CharacterType.NORMAL_VILLAGER, 'NORMAL_VILLAGER'],
   [CharacterType.WEREWOLF, 'WEREWOLF'],
   [CharacterType.SEER, 'SEER'],
   [CharacterType.DOCTOR, 'DOCTOR'],
   [CharacterType.WITCH, 'WITCH'],
   [CharacterType.GUARDIAN, 'GUARDIAN'],
   [CharacterType.PENGUIN, 'PENGUIN'],
   [CharacterType.TOAST_BAKER, 'TOAST_BAKER'],
   [CharacterType.WOLF_BEAUTY, 'WOLF_BEAUTY'],
   [CharacterType.WOLF_CHILD, 'WOLF_CHILD'],
   [CharacterType.SUCCUBUS, 'SUCCUBUS'],
   [CharacterType.MUTER, 'MUTER'],
   [CharacterType.PRINCESS, 'PRINCESS'],
   [CharacterType.KNIGHT, 'KNIGHT'],
   [CharacterType.FOX, 'FOX']];

const ENUM_NAME_MAP = new Map<CharacterType, string>(ENUM_NAME_PAIRS);

const ENUM_NAME_MAP_INVERSE = new Map<string, CharacterType>(
  ENUM_NAME_PAIRS.map((pair): [string, CharacterType] => [pair[1], pair[0]]));

export function fromEnumName(enumName: string) {
  return getFromMap(enumName, CharacterType.CHARACTER_TYPE_UNSPECIFIED, ENUM_NAME_MAP_INVERSE);
}

export function toEnumName(characterType: CharacterType) {
  return getFromMap(characterType, 'CHARACTER_TYPE_UNSPECIFIED', ENUM_NAME_MAP);
}

export const NO_PLAYER = -1;
