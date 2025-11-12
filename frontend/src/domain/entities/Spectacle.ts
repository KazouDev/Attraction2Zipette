import type { Lieu } from './Lieu';
import type { Personnage } from './Personnage';
import type { Programmation } from './Programmation';

export interface Spectacle {
  id: number;
  titre: string;
  lieu: Lieu;
  personnages: Personnage[];
  programmations: Programmation[];
}
