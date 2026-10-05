import { describe, expect, it } from 'vitest';

import type { DoseHistoryItem } from '../../api/generated';
import { dailyAdherence } from './dailyAdherence';

function item(scheduledAt: string, status: DoseHistoryItem['status']): DoseHistoryItem {
  return {
    scheduleId: 's',
    medicationId: 'm',
    medicationName: 'Losartán',
    dosage: '1 tableta',
    scheduledAt,
    status,
    actedAt: null,
  };
}

describe('dailyAdherence', () => {
  it('cuenta por día local del paciente y deja null los días sin dosis', () => {
    const items = [
      // 23:30 en Bogotá del día 4 es 04:30 UTC del día 5: cuenta para el 4.
      item('2026-10-05T04:30:00Z', 'TAKEN'),
      item('2026-10-05T13:00:00Z', 'TAKEN'),
      item('2026-10-05T20:00:00Z', 'MISSED'),
      item('2026-10-05T22:00:00Z', 'SKIPPED'),
    ];
    const days = ['2026-10-03', '2026-10-04', '2026-10-05'];
    expect(dailyAdherence(items, days, 'America/Bogota')).toEqual([
      { day: '2026-10-03', taken: 0, total: 0, percentage: null },
      { day: '2026-10-04', taken: 1, total: 1, percentage: 100 },
      { day: '2026-10-05', taken: 1, total: 3, percentage: (1 / 3) * 100 },
    ]);
  });

  it('ignora dosis fuera de los días pedidos', () => {
    expect(dailyAdherence([item('2026-09-01T12:00:00Z', 'TAKEN')], ['2026-10-05'], 'UTC')).toEqual([
      { day: '2026-10-05', taken: 0, total: 0, percentage: null },
    ]);
  });
});
