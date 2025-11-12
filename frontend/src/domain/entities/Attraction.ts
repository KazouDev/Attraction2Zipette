import type { AttractionType } from './AttractionType';
import type { Horaire } from './Horaire';

export interface Attraction {
  id: number;
  nom: string;
  type: AttractionType;
  tailleMin: number;
  tailleMinAdulte: number;
  horaireOuvertures: Horaire[];
  tempsAttente?: number | null;
}
