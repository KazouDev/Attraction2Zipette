import type { Lieu } from './Lieu';
import type { Personnage } from './Personnage';

export interface RencontrePersonnage {
  id: number;
  personnage: Personnage;
  lieu: Lieu;
  jourSemaine: string;
  heureDebut: string;
  heureFin: string;
}

