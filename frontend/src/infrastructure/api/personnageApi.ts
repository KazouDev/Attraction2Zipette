import type { Personnage } from '@/domain/entities/Personnage';
import { httpClient } from './httpClient';

const BASE_URL = '/personnages';

export const personnageApi = {
  fetchAll: async (): Promise<Personnage[]> => {
    const { data } = await httpClient.get<Personnage[]>(BASE_URL);
    return data;
  },

  fetchById: async (id: number): Promise<Personnage> => {
    const { data } = await httpClient.get<Personnage>(`${BASE_URL}/${id}`);
    return data;
  },

  create: async (nom: string): Promise<{ id: number }> => {
    const { data } = await httpClient.post<{ id: number }>(BASE_URL, { nom });
    return data;
  },

  delete: async (id: number): Promise<void> => {
    await httpClient.delete(`${BASE_URL}/${id}`);
  }
};
