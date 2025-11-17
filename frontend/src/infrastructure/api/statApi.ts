import { httpClient } from './httpClient';

export interface SpectacleWithDuration {
  spectacleId: number;
  spectacleTitre: string;
  jourSemaine: number;
  durationMinutes: number;
}

export interface PersonnageWithActivity {
  personnageId: number;
  personnageNom: string;
  totalActivity: number;
}

export async function fetchSpectaclesRanking(day?: number): Promise<SpectacleWithDuration[]> {
  const url = '/stats/spectacles';
  if (typeof day === 'number') {
    const { data } = await httpClient.get<SpectacleWithDuration[]>(url, { params: { day } });
    return data;
  }
  const { data } = await httpClient.get<SpectacleWithDuration[]>(url);
  return data;
}

export async function fetchTopPersonnages(limit = 5): Promise<PersonnageWithActivity[]> {
  const { data } = await httpClient.get<PersonnageWithActivity[]>('/stats/top-personnages', { params: { limit } });
  return data;
}

