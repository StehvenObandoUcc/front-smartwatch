import { describe, expect, it } from 'vitest';

import type { DoseHistoryItem, PlannedDose } from '../../api/generated';
import { canRecord, doseState, historyIndex } from './doseState';

const dose: PlannedDose = {
  scheduleId: 's1',
  medicationId: 'm1',
  medicationName: 'Losartán',
  dosage: '50 mg',
  color: '#3B82F6',
  scheduledAt: '2026-10-22T13:00:00Z',
};

function item(status: DoseHistoryItem['status'], scheduledAt = dose.scheduledAt): DoseHistoryItem {
  return {
    scheduleId: 's1',
    medicationId: 'm1',
    medicationName: 'Losartán',
    dosage: '50 mg',
    scheduledAt,
    status,
    actedAt: null,
  };
}

const at = (iso: string) => new Date(iso).getTime();

describe('doseState', () => {
  it('usa el estado del historial aunque el ISO venga con otro formato', () => {
    const index = historyIndex([item('TAKEN', '2026-10-22T08:00:00-05:00')]);
    expect(doseState(dose, index, at('2026-10-22T15:00:00Z'))).toBe('taken');
  });

  it('distingue omitida y no tomada', () => {
    expect(doseState(dose, historyIndex([item('SKIPPED')]), 0)).toBe('skipped');
    expect(doseState(dose, historyIndex([item('MISSED')]), 0)).toBe('missed');
  });

  it('sin registro: pendiente si ya es la hora, si no aún no toca', () => {
    const empty = historyIndex([]);
    expect(doseState(dose, empty, at('2026-10-22T13:00:00Z'))).toBe('due');
    expect(doseState(dose, empty, at('2026-10-22T12:59:00Z'))).toBe('upcoming');
  });

  it('solo se registran las pendientes o no tomadas', () => {
    expect(
      ['taken', 'skipped', 'missed', 'due', 'upcoming'].filter((s) => canRecord(s as never)),
    ).toEqual(['missed', 'due']);
  });
});
