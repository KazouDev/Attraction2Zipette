import type { AttractionType } from '@/domain/entities/AttractionType';
import type { IdResponse } from '@/domain/entities/IdResponse';
import { httpClient } from './httpClient';

export interface AttractionTypeCreatePayload {
  nom: string;
}

export const attractionTypeApi = {
  async fetchAll(): Promise<AttractionType[]> {
    const { data } = await httpClient.get<AttractionType[]>('/types');
    return data;
  },
  async create(payload: AttractionTypeCreatePayload): Promise<IdResponse> {
    const { data } = await httpClient.post<IdResponse>('/types', payload);
    return data;
  },
  async update(id: number, nom: string): Promise<IdResponse> {
    const { data } = await httpClient.put<IdResponse>(`/types/${id}`, { nom });
    return data;
  },
  async remove(id: number): Promise<IdResponse> {
    const { data } = await httpClient.delete<IdResponse>(`/types/${id}`);
    return data;
  }
};
