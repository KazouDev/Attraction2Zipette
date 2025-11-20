export const formatTime = (value: string): string => {
  if (!value) {
    return '—';
  }
  if (/^\d{2}:\d{2}$/.test(value)) {
    return value;
  }
  if (/^\d{2}:\d{2}:\d{2}$/.test(value)) {
    return value.slice(0, 5);
  }
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return '—';
  }
  return date.toTimeString().slice(0, 5);
};

export const durationBetween = (start: string, end: string): number => {
  const startParts = start.split(':').map(Number);
  const endParts = end.split(':').map(Number);
  if (startParts.length < 2 || endParts.length < 2) {
    return 0;
  }
  const startMinutes = startParts[0] * 60 + startParts[1];
  const endMinutes = endParts[0] * 60 + endParts[1];
  return Math.max(endMinutes - startMinutes, 0);
};

export const formatDuration = (minutes: number): string => {
  const hours = Math.floor(minutes / 60);
  const mins = minutes % 60;
  if (hours === 0) {
    return `${mins} min`;
  }
  if (mins === 0) {
    return `${hours} h`;
  }
  return `${hours} h ${mins} min`;
};

export const summarizeTimeRange = (start: string, end: string): string => {
  return `${formatTime(start)} → ${formatTime(end)}`;
};
