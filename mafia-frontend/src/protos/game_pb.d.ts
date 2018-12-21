// package: com.nearsyh.mafia.protos
// file: game.proto

import * as jspb from "google-protobuf";

export class TurnStatus extends jspb.Message {
  getFrozenPlayerIndex(): number;
  setFrozenPlayerIndex(value: number): void;

  getToastPlayerIndex(): number;
  setToastPlayerIndex(value: number): void;

  hasInjectionCharacterIndex(): boolean;
  clearInjectionCharacterIndex(): void;
  getInjectionCharacterIndex(): CharacterIndex | undefined;
  setInjectionCharacterIndex(value?: CharacterIndex): void;

  hasGuardCharacterIndex(): boolean;
  clearGuardCharacterIndex(): void;
  getGuardCharacterIndex(): CharacterIndex | undefined;
  setGuardCharacterIndex(value?: CharacterIndex): void;

  hasKillCharacterIndex(): boolean;
  clearKillCharacterIndex(): void;
  getKillCharacterIndex(): CharacterIndex | undefined;
  setKillCharacterIndex(value?: CharacterIndex): void;

  getHasAffection(): boolean;
  setHasAffection(value: boolean): void;

  hasAffectedCharacter(): boolean;
  clearAffectedCharacter(): void;
  getAffectedCharacter(): CharacterIndex | undefined;
  setAffectedCharacter(value?: CharacterIndex): void;

  hasCureCharacterIndex(): boolean;
  clearCureCharacterIndex(): void;
  getCureCharacterIndex(): CharacterIndex | undefined;
  setCureCharacterIndex(value?: CharacterIndex): void;

  hasToxicCharacterIndex(): boolean;
  clearToxicCharacterIndex(): void;
  getToxicCharacterIndex(): CharacterIndex | undefined;
  setToxicCharacterIndex(value?: CharacterIndex): void;

  hasVerifyCharacterIndex(): boolean;
  clearVerifyCharacterIndex(): void;
  getVerifyCharacterIndex(): CharacterIndex | undefined;
  setVerifyCharacterIndex(value?: CharacterIndex): void;

  getMutedPlayerIndex(): number;
  setMutedPlayerIndex(value: number): void;

  clearDeadCharactersList(): void;
  getDeadCharactersList(): Array<CharacterIndex>;
  setDeadCharactersList(value: Array<CharacterIndex>): void;
  addDeadCharacters(value?: CharacterIndex, index?: number): CharacterIndex;

  clearVotedCharactersList(): void;
  getVotedCharactersList(): Array<CharacterIndex>;
  setVotedCharactersList(value: Array<CharacterIndex>): void;
  addVotedCharacters(value?: CharacterIndex, index?: number): CharacterIndex;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): TurnStatus.AsObject;
  static toObject(includeInstance: boolean, msg: TurnStatus): TurnStatus.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: TurnStatus, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): TurnStatus;
  static deserializeBinaryFromReader(message: TurnStatus, reader: jspb.BinaryReader): TurnStatus;
}

export namespace TurnStatus {
  export type AsObject = {
    frozenPlayerIndex: number,
    toastPlayerIndex: number,
    injectionCharacterIndex?: CharacterIndex.AsObject,
    guardCharacterIndex?: CharacterIndex.AsObject,
    killCharacterIndex?: CharacterIndex.AsObject,
    hasAffection: boolean,
    affectedCharacter?: CharacterIndex.AsObject,
    cureCharacterIndex?: CharacterIndex.AsObject,
    toxicCharacterIndex?: CharacterIndex.AsObject,
    verifyCharacterIndex?: CharacterIndex.AsObject,
    mutedPlayerIndex: number,
    deadCharactersList: Array<CharacterIndex.AsObject>,
    votedCharactersList: Array<CharacterIndex.AsObject>,
  }
}

export class GameStatus extends jspb.Message {
  getIsCureUsed(): boolean;
  setIsCureUsed(value: boolean): void;

  getIsToxicUsed(): boolean;
  setIsToxicUsed(value: boolean): void;

  getIdolIndex(): number;
  setIdolIndex(value: number): void;

  getLoverIndex(): number;
  setLoverIndex(value: number): void;

  getPardonIndex(): number;
  setPardonIndex(value: number): void;

  getDuelIndex(): number;
  setDuelIndex(value: number): void;

  clearDeadCharactersList(): void;
  getDeadCharactersList(): Array<CharacterIndex>;
  setDeadCharactersList(value: Array<CharacterIndex>): void;
  addDeadCharacters(value?: CharacterIndex, index?: number): CharacterIndex;

  clearAliveCharactersList(): void;
  getAliveCharactersList(): Array<CharacterIndex>;
  setAliveCharactersList(value: Array<CharacterIndex>): void;
  addAliveCharacters(value?: CharacterIndex, index?: number): CharacterIndex;

  clearAffectedCharactersList(): void;
  getAffectedCharactersList(): Array<CharacterIndex>;
  setAffectedCharactersList(value: Array<CharacterIndex>): void;
  addAffectedCharacters(value?: CharacterIndex, index?: number): CharacterIndex;

  clearOnSurfaceWolvesList(): void;
  getOnSurfaceWolvesList(): Array<number>;
  setOnSurfaceWolvesList(value: Array<number>): void;
  addOnSurfaceWolves(value: number, index?: number): number;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): GameStatus.AsObject;
  static toObject(includeInstance: boolean, msg: GameStatus): GameStatus.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: GameStatus, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): GameStatus;
  static deserializeBinaryFromReader(message: GameStatus, reader: jspb.BinaryReader): GameStatus;
}

export namespace GameStatus {
  export type AsObject = {
    isCureUsed: boolean,
    isToxicUsed: boolean,
    idolIndex: number,
    loverIndex: number,
    pardonIndex: number,
    duelIndex: number,
    deadCharactersList: Array<CharacterIndex.AsObject>,
    aliveCharactersList: Array<CharacterIndex.AsObject>,
    affectedCharactersList: Array<CharacterIndex.AsObject>,
    onSurfaceWolvesList: Array<number>,
  }
}

export class Game extends jspb.Message {
  getId(): string;
  setId(value: string): void;

  clearPlayersList(): void;
  getPlayersList(): Array<Player>;
  setPlayersList(value: Array<Player>): void;
  addPlayers(value?: Player, index?: number): Player;

  getTurnId(): number;
  setTurnId(value: number): void;

  hasLastTurn(): boolean;
  clearLastTurn(): void;
  getLastTurn(): TurnStatus | undefined;
  setLastTurn(value?: TurnStatus): void;

  hasCurrentTurn(): boolean;
  clearCurrentTurn(): void;
  getCurrentTurn(): TurnStatus | undefined;
  setCurrentTurn(value?: TurnStatus): void;

  clearPastTurnsList(): void;
  getPastTurnsList(): Array<TurnStatus>;
  setPastTurnsList(value: Array<TurnStatus>): void;
  addPastTurns(value?: TurnStatus, index?: number): TurnStatus;

  hasGameStatus(): boolean;
  clearGameStatus(): void;
  getGameStatus(): GameStatus | undefined;
  setGameStatus(value?: GameStatus): void;

  hasNextEvent(): boolean;
  clearNextEvent(): void;
  getNextEvent(): Event | undefined;
  setNextEvent(value?: Event): void;

  getStartTimestamp(): number;
  setStartTimestamp(value: number): void;

  getEndTimestamp(): number;
  setEndTimestamp(value: number): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): Game.AsObject;
  static toObject(includeInstance: boolean, msg: Game): Game.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: Game, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): Game;
  static deserializeBinaryFromReader(message: Game, reader: jspb.BinaryReader): Game;
}

export namespace Game {
  export type AsObject = {
    id: string,
    playersList: Array<Player.AsObject>,
    turnId: number,
    lastTurn?: TurnStatus.AsObject,
    currentTurn?: TurnStatus.AsObject,
    pastTurnsList: Array<TurnStatus.AsObject>,
    gameStatus?: GameStatus.AsObject,
    nextEvent?: Event.AsObject,
    startTimestamp: number,
    endTimestamp: number,
  }
}

export class Player extends jspb.Message {
  hasCharacterTop(): boolean;
  clearCharacterTop(): void;
  getCharacterTop(): Character | undefined;
  setCharacterTop(value?: Character): void;

  hasCharacterBot(): boolean;
  clearCharacterBot(): void;
  getCharacterBot(): Character | undefined;
  setCharacterBot(value?: Character): void;

  getIndex(): number;
  setIndex(value: number): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): Player.AsObject;
  static toObject(includeInstance: boolean, msg: Player): Player.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: Player, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): Player;
  static deserializeBinaryFromReader(message: Player, reader: jspb.BinaryReader): Player;
}

export namespace Player {
  export type AsObject = {
    characterTop?: Character.AsObject,
    characterBot?: Character.AsObject,
    index: number,
  }
}

export class CharacterIndex extends jspb.Message {
  getPlayerIndex(): number;
  setPlayerIndex(value: number): void;

  getCharacterIndex(): number;
  setCharacterIndex(value: number): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): CharacterIndex.AsObject;
  static toObject(includeInstance: boolean, msg: CharacterIndex): CharacterIndex.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: CharacterIndex, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): CharacterIndex;
  static deserializeBinaryFromReader(message: CharacterIndex, reader: jspb.BinaryReader): CharacterIndex;
}

export namespace CharacterIndex {
  export type AsObject = {
    playerIndex: number,
    characterIndex: number,
  }
}

export class Character extends jspb.Message {
  getCharacterType(): CharacterType;
  setCharacterType(value: CharacterType): void;

  getIsDead(): boolean;
  setIsDead(value: boolean): void;

  getInjectionCount(): number;
  setInjectionCount(value: number): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): Character.AsObject;
  static toObject(includeInstance: boolean, msg: Character): Character.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: Character, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): Character;
  static deserializeBinaryFromReader(message: Character, reader: jspb.BinaryReader): Character;
}

export namespace Character {
  export type AsObject = {
    characterType: CharacterType,
    isDead: boolean,
    injectionCount: number,
  }
}

export class Event extends jspb.Message {
  getEventType(): EventType;
  setEventType(value: EventType): void;

  clearCandidateTargetsList(): void;
  getCandidateTargetsList(): Array<number>;
  setCandidateTargetsList(value: Array<number>): void;
  addCandidateTargets(value: number, index?: number): number;

  clearTargetsList(): void;
  getTargetsList(): Array<number>;
  setTargetsList(value: Array<number>): void;
  addTargets(value: number, index?: number): number;

  getTimestamp(): number;
  setTimestamp(value: number): void;

  getTurnId(): number;
  setTurnId(value: number): void;

  getLastEventResponse(): string;
  setLastEventResponse(value: string): void;

  getCurrentEventResponse(): string;
  setCurrentEventResponse(value: string): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): Event.AsObject;
  static toObject(includeInstance: boolean, msg: Event): Event.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: Event, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): Event;
  static deserializeBinaryFromReader(message: Event, reader: jspb.BinaryReader): Event;
}

export namespace Event {
  export type AsObject = {
    eventType: EventType,
    candidateTargetsList: Array<number>,
    targetsList: Array<number>,
    timestamp: number,
    turnId: number,
    lastEventResponse: string,
    currentEventResponse: string,
  }
}

export enum CharacterType {
  CHARACTER_TYPE_UNSPECIFIED = 0,
  NORMAL_VILLAGER = 1,
  WEREWOLF = 2,
  SEER = 3,
  DOCTOR = 4,
  WITCH = 5,
  GUARDIAN = 6,
  PENGUIN = 7,
  TOAST_BAKER = 8,
  WOLF_BEAUTY = 9,
  WOLF_CHILD = 10,
  SUCCUBUS = 11,
  MUTER = 12,
  PRINCESS = 13,
  KNIGHT = 14,
  FOX = 15,
}

export enum EventType {
  EVENT_TYPE_UNSPECIFIED = 0,
  SUNSET = 1,
  CHOOSE_IDOL = 2,
  CHOOSE_LOVER = 3,
  FREEZE = 4,
  TOAST = 5,
  INJECT = 6,
  GUARD = 7,
  KILL = 8,
  AFFECT = 9,
  CURE = 10,
  TOXIC = 11,
  VERIFY = 12,
  FOX_VERIFY = 13,
  MUTE = 14,
  SUNRISE = 15,
  ANNOUNCE_DEATH = 16,
  SPEECH = 17,
  VOTE = 18,
  DUEL = 19,
  PARDON = 20,
  CONFESS = 21,
  END = 22,
}

