import type { DoseHistoryItem } from '../../api/generated';
import { localDate } from '../../lib/dates';

export type DayAdherence = {
  /** Día local del paciente, YYYY-MM-DD. */
  day: string;
  taken: number;
  total: number;
  /** null si ese día no había dosis vencidas. */
  percentage: number | null;
};

/** Agrupa el historial por día local del paciente, en el orden de `days` (del más antiguo al más reciente). */
export function dailyAdherence(
  items: DoseHistoryItem[],
  days: string[],
  timeZone: string,
): DayAdherence[] {
  const byDay = new Map(days.map((day) => [day, { taken: 0, total: 0 }]));
  for (const item of items) {
    const counts = byDay.get(localDate(item.scheduledAt, timeZone));
    if (!counts) continue;
    counts.total += 1;
    if (item.status === 'TAKEN') counts.taken += 1;
  }
  return days.map((day) => {
    const { taken, total } = byDay.get(day) ?? { taken: 0, total: 0 };
    return { day, taken, total, percentage: total === 0 ? null : (taken / total) * 100 };
  });
}
