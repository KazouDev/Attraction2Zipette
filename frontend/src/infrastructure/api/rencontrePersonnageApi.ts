import type { IdResponse } from '@/domain/entities/IdResponse';
import type { RencontrePersonnage } from '@/domain/entities/RencontrePersonnage';
import { httpClient } from './httpClient';

export interface RencontrePersonnageCreatePayload {
  personnageId: number;
  lieuId: number;
  jourSemaine: number;
  heureOuverture: string;
  heureFermeture: string;
}

const toCreateBody = (payload: RencontrePersonnageCreatePayload): Record<string, unknown> => {
  return {
    personnageId: payload.personnageId,
    lieuId: payload.lieuId,
    jourSemaine: payload.jourSemaine,
    heureOuverture: ensureSeconds(payload.heureOuverture),
    heureFermeture: ensureSeconds(payload.heureFermeture)
  };
};

export const rencontrePersonnageApi = {
  async fetchAll(): Promise<RencontrePersonnage[]> {
    const { data } = await httpClient.get<RencontrePersonnage[]>('/rencontres');
    return data;
  },

  async fetchById(id: number): Promise<RencontrePersonnage> {
    const { data } = await httpClient.get<RencontrePersonnage>(`/rencontres/${id}`);
    return data;
  },

  async create(payload: RencontrePersonnageCreatePayload): Promise<IdResponse> {
    const { data } = await httpClient.post<IdResponse>('/rencontres', toCreateBody(payload));
    return data;
  },

  async delete(id: number): Promise<IdResponse> {
    const { data } = await httpClient.delete<IdResponse>(`/rencontres/${id}`);
    return data;
  }
};

const ensureSeconds = (value: string): string => {
  if (!value) {
    return '00:00:00';
  }
  if (/^\d{2}:\d{2}:\d{2}$/.test(value)) {
    return value;
  }
  if (/^\d{2}:\d{2}$/.test(value)) {
    return `${value}:00`;
  }
  return '00:00:00';
};

