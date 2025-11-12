export interface Horaire {
  jourSemaine: string;
  heureOuverture: string;
  heureFermeture: string;
}


export const JOUR_NUMBER_TO_NAME: Record<number, string> = {
  0: 'Dimanche',
  1: 'Lundi',
  2: 'Mardi',
  3: 'Mercredi',
  4: 'Jeudi',
  5: 'Vendredi',
  6: 'Samedi'
};
