import Axios from 'axios';
import { Game, Event } from '@/protos/game_pb';

const MAFIA_ENDPOINT = 'http://localhost:8080';
const axios = Axios.create({
  baseURL: MAFIA_ENDPOINT,
  timeout: 100000,
  responseType: 'arraybuffer',
  headers: {
    'Content-Type': 'application/x-protobuf',
    'Accept': 'application/x-protobuf',
  },
});

export async function getSupportedCharacterTypes() {
  const response = await axios.get<string[]>(`/games/support_characters`, {
    responseType: '',
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    },
  });
  return response.data;
}

export async function createGame(charactersCount: Map<string, number>) {
  const payload: any = {};
  charactersCount.forEach((v, k) => payload[k] = v);
  const response = await axios.put<Uint8Array>(`/games`, payload, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
  return Game.deserializeBinary(response.data);
}

export async function getGame(gameId: string) {
  const response = await axios.get<Uint8Array>(`/games/${gameId}`);
  return Game.deserializeBinary(response.data);
}

export async function updateGame(gameId: string, event: Event) {
  const response = await axios.post<Uint8Array>(`/games/${gameId}`, event.serializeBinary());
  return Game.deserializeBinary(response.data);
}
