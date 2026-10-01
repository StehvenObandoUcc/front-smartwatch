import type { PlannedDose } from '../../api/generated';
import { localDate } from '../../lib/dates';

export function groupByDay(doses: PlannedDose[], timeZone: string) {
  const days = new Map<string, PlannedDose[]>();
  for (const dose of doses) {
    const day = localDate(dose.scheduledAt, timeZone);
    days.set(day, [...(days.get(day) ?? []), dose]);
  }
  return [...days].map(([day, items]) => ({ day, doses: items }));
}
