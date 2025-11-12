import type { IdResponse } from '@/domain/entities/IdResponse';
import type { Lieu } from '@/domain/entities/Lieu';
import { httpClient } from './httpClient';

export interface LieuCreatePayload {
  nom: string;
}

export const lieuApi = {
  async fetchAll(): Promise<Lieu[]> {
    const { data } = await httpClient.get<Lieu[]>('/lieux');
    return data;
  },
  async create(payload: LieuCreatePayload): Promise<IdResponse> {
    const { data } = await httpClient.post<IdResponse>('/lieux', payload);
    return data;
  },
  async update(id: number, nom: string): Promise<IdResponse> {
    const { data } = await httpClient.put<IdResponse>(`/lieux/${id}`, { nom });
    return data;
  },
  async remove(id: number): Promise<IdResponse> {
    const { data } = await httpClient.delete<IdResponse>(`/lieux/${id}`);
    return data;
  }
};
