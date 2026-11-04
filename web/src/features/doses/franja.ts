import type { PlannedDose } from '../../api/generated';
import { localDate } from '../../lib/dates';

export type Franja = 'morning' | 'afternoon' | 'night';

// En orden del día; el nombre y el rango de horas de cada una están en i18n (franja.*).
export const FRANJAS: Franja[] = ['morning', 'afternoon', 'night'];

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

/** La franja en la que está ahora el paciente (en su zona horaria). */
export function franjaAt(now: Date, timeZone: string): Franja {
  return franjaOf(localHour(now.toISOString(), timeZone));
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
