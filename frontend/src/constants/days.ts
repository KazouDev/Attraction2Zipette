export const DAY_NUMBERS = [0, 1, 2, 3, 4, 5, 6] as const;

export type DayNumber = (typeof DAY_NUMBERS)[number];

export const DAY_LABEL_BY_NUMBER: Record<DayNumber, string> = {
  0: 'Lundi',
  1: 'Mardi',
  2: 'Mercredi',
  3: 'Jeudi',
  4: 'Vendredi',
  5: 'Samedi',
  6: 'Dimanche'
};

export const DAY_ENUM_TO_NUMBER: Record<string, DayNumber> = {
  LUNDI: 0,
  MARDI: 1,
  MERCREDI: 2,
  JEUDI: 3,
  VENDREDI: 4,
  SAMEDI: 5,
  DIMANCHE: 6
};

export const DAY_NUMBER_TO_ENUM: Record<DayNumber, string> = {
  0: 'LUNDI',
  1: 'MARDI',
  2: 'MERCREDI',
  3: 'JEUDI',
  4: 'VENDREDI',
  5: 'SAMEDI',
  6: 'DIMANCHE'
};

export const readDayLabel = (input: string | number): string => {
  if (typeof input === 'number' && input in DAY_LABEL_BY_NUMBER) {
    return DAY_LABEL_BY_NUMBER[input as DayNumber];
  }
  const normalized = typeof input === 'string' ? input.toUpperCase() : '';
  const number = DAY_ENUM_TO_NUMBER[normalized];
  if (typeof number === 'number') {
    return DAY_LABEL_BY_NUMBER[number as DayNumber];
  }
  return normalized || '';
};
