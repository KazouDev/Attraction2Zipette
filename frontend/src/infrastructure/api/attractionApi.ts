import type { Attraction } from '@/domain/entities/Attraction';
import type { IdResponse } from '@/domain/entities/IdResponse';
import { httpClient } from './httpClient';

export interface AttractionCreatePayload {
  nom: string;
  typeId: number;
  tailleMin: number;
  tailleMinAdulte: number;
}

export interface AttractionUpdatePayload {
  nom?: string;
  typeId?: number;
  tailleMin?: number;
  tailleMinAdulte?: number;
}

export interface AttractionHorairePayload {
  jourSemaine: number;
  heureOuverture: string;
  heureFermeture: string;
}

const toUpdateBody = (payload: AttractionUpdatePayload): Record<string, unknown> => {
  const body: Record<string, unknown> = {};
  if (payload.nom !== undefined) body.nom = payload.nom;
  if (payload.typeId !== undefined) body.type_id = payload.typeId;
  if (payload.tailleMin !== undefined) body.taille_min = payload.tailleMin;
  if (payload.tailleMinAdulte !== undefined) body.taille_min_adulte = payload.tailleMinAdulte;
  return body;
};

export const attractionApi = {
  async fetchAll(): Promise<Attraction[]> {
    const { data } = await httpClient.get<Attraction[]>('/attractions');
    return data;
  },
  async fetchById(id: number): Promise<Attraction> {
    const { data } = await httpClient.get<Attraction>(`/attractions/${id}`);
    return data;
  },
  async create(payload: AttractionCreatePayload): Promise<IdResponse> {
    const { data } = await httpClient.post<IdResponse>('/attractions', payload);
    return data;
  },
  async update(id: number, payload: AttractionUpdatePayload): Promise<IdResponse> {
    const body = toUpdateBody(payload);
    const { data } = await httpClient.put<IdResponse>(`/attractions/${id}`, body);
    return data;
  },
  async remove(id: number): Promise<IdResponse> {
    const { data } = await httpClient.delete<IdResponse>(`/attractions/${id}`);
    return data;
  },
  async updateHoraires(id: number, horaires: AttractionHorairePayload[]): Promise<IdResponse> {
    const normalized = horaires.map((horaire) => ({
      jourSemaine: horaire.jourSemaine,
      heureOuverture: ensureTimeFormat(horaire.heureOuverture),
      heureFermeture: ensureTimeFormat(horaire.heureFermeture)
    }));
    const { data } = await httpClient.put<IdResponse>(`/attractions/horaires/${id}`, normalized);
    return data;
  }
};

const ensureTimeFormat = (value: string): string => {
  if (!value) {
    return '00:00';
  }
  if (/^\d{2}:\d{2}$/.test(value)) {
    return value;
  }
  if (/^\d{2}:\d{2}:\d{2}$/.test(value)) {
    return value.substring(0, 5);
  }
  const date = new Date(value);
  if (!Number.isNaN(date.getTime())) {
    return date.toTimeString().slice(0, 5);
  }
  return '00:00';
};
