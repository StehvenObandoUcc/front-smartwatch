import type { PlannedDose } from '../../api/generated';
import { localDate } from '../../lib/dates';

export type Franja = 'morning' | 'afternoon' | 'night';

export const FRANJAS: { id: Franja; label: string; range: string }[] = [
  { id: 'morning', label: 'Mañana', range: '5:00 a 12:00' },
  { id: 'afternoon', label: 'Tarde', range: '12:00 a 19:00' },
  { id: 'night', label: 'Noche', range: '19:00 a 5:00' },
];

/** Franja del día para una hora local (0–23). */
export function franjaOf(hour: number): Franja {
  if (hour >= 5 && hour < 12) return 'morning';
  if (hour >= 12 && hour < 19) return 'afternoon';
  return 'night';
}

function localHour(iso: string, timeZone: string): number {
  // hourCycle h23 evita el "24" de medianoche que algunos motores devuelven con hour12: false.
  const hour = new Date(iso).toLocaleTimeString('en-GB', {
    timeZone,
    hour: '2-digit',
    hourCycle: 'h23',
  });
  return Number.parseInt(hour, 10);
}

/** Las dosis del día `day` (YYYY-MM-DD, zona del paciente) agrupadas por franja, en orden de hora. */
export function groupByFranja(
  doses: PlannedDose[],
  day: string,
  timeZone: string,
): Record<Franja, PlannedDose[]> {
  const groups: Record<Franja, PlannedDose[]> = { morning: [], afternoon: [], night: [] };
  const sorted = [...doses].sort((a, b) => a.scheduledAt.localeCompare(b.scheduledAt));
  for (const dose of sorted) {
    if (localDate(dose.scheduledAt, timeZone) !== day) continue;
    groups[franjaOf(localHour(dose.scheduledAt, timeZone))].push(dose);
  }
  return groups;
}
