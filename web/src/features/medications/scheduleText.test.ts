import { describe, expect, it } from 'vitest';

import { scheduleText } from './scheduleText';

describe('scheduleText', () => {
  it('nombra los grupos de días habituales', () => {
    expect(scheduleText({ times: ['20:00', '08:00'], daysOfWeek: [7, 1, 2, 3, 4, 5, 6] })).toBe(
      '08:00 · 20:00 — todos los días',
    );
    expect(scheduleText({ times: ['13:00'], daysOfWeek: [1, 2, 3, 4, 5] })).toBe(
      '13:00 — de lunes a viernes',
    );
    expect(scheduleText({ times: ['10:00'], daysOfWeek: [6, 7] })).toBe('10:00 — fines de semana');
  });

  it('lista los días sueltos', () => {
    expect(scheduleText({ times: ['07:00'], daysOfWeek: [5, 1, 3] })).toBe('07:00 — lun, mié, vie');
  });
});
