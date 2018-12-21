import { Game, EventType, CharacterType } from '@/protos/game_pb';

function isCharacterTypeOnSurface(game: Game, characterType: CharacterType) {
  for (const player of game.getPlayersList()) {
    if (!player.getCharacterTop()!.getIsDead()) {
      return characterType === player.getCharacterTop()!.getCharacterType();
    } else if (!player.getCharacterBot()!.getIsDead()) {
      return characterType === player.getCharacterBot()!.getCharacterType();
    }
  }
  return false;
}

export function canPardon(game: Game) {
  if (!game.getNextEvent() || !game.getGameStatus()) {
    return false
  }
  const eventType = game.getNextEvent()!.getEventType();
  return isCharacterTypeOnSurface(game, CharacterType.PRINCESS)
    && eventType == EventType.VOTE
    && game.getGameStatus()!.getPardonIndex() < 0;
}

export function canDuel(game: Game) {
  if (!game.getNextEvent() || !game.getGameStatus()) {
    return false
  }
  const eventType = game.getNextEvent()!.getEventType();
  return isCharacterTypeOnSurface(game, CharacterType.KNIGHT)
    && eventType == EventType.SPEECH
    && game.getGameStatus()!.getPardonIndex() < 0;
}