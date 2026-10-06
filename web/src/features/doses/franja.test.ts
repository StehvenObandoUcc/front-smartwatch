import { describe, expect, it } from 'vitest';

import type { PlannedDose } from '../../api/generated';
import { franjaOf, groupByFranja } from './franja';

function dose(scheduledAt: string, name = 'Losartán'): PlannedDose {
  return {
    scheduleId: 's',
    medicationId: 'm',
    medicationName: name,
    dosage: '1 tableta',
    color: '#1565c0',
    scheduledAt,
  };
}

describe('franjaOf', () => {
  it('corta en 5, 12 y 19 horas', () => {
    expect([4, 5, 11, 12, 18, 19, 23, 0].map(franjaOf)).toEqual([
      'night',
      'morning',
      'morning',
      'afternoon',
      'afternoon',
      'night',
      'night',
      'night',
    ]);
  });
});

describe('groupByFranja', () => {
  it('usa la hora y el día de la zona del paciente y ordena por hora', () => {
    // Bogotá es UTC-5: 13:00Z = 08:00 (mañana), 00:30Z del día 6 = 19:30 del día 5 (noche).
    const doses = [
      dose('2026-10-06T00:30:00Z', 'Noche'),
      dose('2026-10-05T19:00:00Z', 'Tarde'),
      dose('2026-10-05T13:00:00Z', 'Mañana'),
      dose('2026-10-06T14:00:00Z', 'Otro día'),
    ];
    const groups = groupByFranja(doses, '2026-10-05', 'America/Bogota');
    expect(groups.morning.map((d) => d.medicationName)).toEqual(['Mañana']);
    expect(groups.afternoon.map((d) => d.medicationName)).toEqual(['Tarde']);
    expect(groups.night.map((d) => d.medicationName)).toEqual(['Noche']);
  });

  it('medianoche cuenta como noche, no como hora 24', () => {
    const groups = groupByFranja([dose('2026-10-05T05:00:00Z')], '2026-10-05', 'America/Bogota');
    expect(groups.night).toHaveLength(1);
  });
});
