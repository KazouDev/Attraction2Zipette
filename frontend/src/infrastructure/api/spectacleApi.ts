import type { IdResponse } from '@/domain/entities/IdResponse';
import type { Spectacle } from '@/domain/entities/Spectacle';
import { httpClient } from './httpClient';

export interface SpectacleCreatePayload {
  titre: string;
  lieuId: number;
}

export interface SpectacleUpdatePayload {
  titre?: string;
  lieuId?: number;
}

export interface SpectacleProgrammationPayload {
  jourSemaine: number;
  heureOuverture: string;
  heureFermeture: string;
}

const toUpdateBody = (payload: SpectacleUpdatePayload): Record<string, unknown> => {
  const body: Record<string, unknown> = {};
  if (payload.titre !== undefined) body.titre = payload.titre;
  if (payload.lieuId !== undefined) body.lieu_id = payload.lieuId;
  return body;
};

const enrichTimes = (payload: SpectacleProgrammationPayload) => ({
  jourSemaine: payload.jourSemaine,
  heureOuverture: ensureSeconds(payload.heureOuverture),
  heureFermeture: ensureSeconds(payload.heureFermeture)
});

export const spectacleApi = {
  async fetchAll(): Promise<Spectacle[]> {
    const { data } = await httpClient.get<Spectacle[]>('/spectacles');
    return data;
  },
  async fetchById(id: number): Promise<Spectacle> {
    const { data } = await httpClient.get<Spectacle>(`/spectacles/${id}`);
    return data;
  },
  async create(payload: SpectacleCreatePayload): Promise<IdResponse> {
    const { data } = await httpClient.post<IdResponse>('/spectacles', payload);
    return data;
  },
  async update(id: number, payload: SpectacleUpdatePayload): Promise<IdResponse> {
    const body = toUpdateBody(payload);
    const { data } = await httpClient.put<IdResponse>(`/spectacles/${id}`, body);
    return data;
  },
  async setProgrammations(id: number, programmations: SpectacleProgrammationPayload[]): Promise<IdResponse> {
    const { data } = await httpClient.put<IdResponse>(
      `/spectacles/horaires/${id}`,
      programmations.map(enrichTimes)
    );
    return data;
  },
  async addProgrammation(id: number, programmation: SpectacleProgrammationPayload): Promise<IdResponse> {
    const { data } = await httpClient.post<IdResponse>(
      `/spectacles/horaires/${id}`,
      enrichTimes(programmation)
    );
    return data;
  },
  async addPersonnage(spectacleId: number, personnageId: number): Promise<IdResponse> {
    const { data } = await httpClient.post<IdResponse>(
      `/spectacles/personnages/${spectacleId}/${personnageId}`
    );
    return data;
  },
  async removePersonnage(spectacleId: number, personnageId: number): Promise<IdResponse> {
    const { data } = await httpClient.delete<IdResponse>(
      `/spectacles/personnages/${spectacleId}/${personnageId}`
    );
    return data;
  },
  async delete(id: number): Promise<IdResponse> {
    const { data } = await httpClient.delete<IdResponse>(`/spectacles/${id}`);
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
  const date = new Date(value);
  if (!Number.isNaN(date.getTime())) {
    return date.toTimeString().slice(0, 8);
  }
  return '00:00:00';
};
