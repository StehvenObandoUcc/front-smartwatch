import { describe, expect, it } from 'vitest';

import type { Device } from '../../api/generated';
import { timeAgo, watchAlert } from './watchStatus';

const now = Date.parse('2026-11-03T15:00:00Z');

function device(lastSeenAt: string | null, batteryLevel: number | null = 80): Device {
  return {
    id: 'd',
    patientId: 'p',
    model: 'Pixel Watch',
    pairedAt: '2026-10-01T00:00:00Z',
    lastSeenAt,
    hasPushToken: false,
    batteryLevel,
    batteryReportedAt: lastSeenAt,
  };
}

describe('watchAlert', () => {
  it('avisa si nunca se conectó, si lleva más de 3 h o si tiene poca batería', () => {
    expect(watchAlert(device(null), now)).toBe('never');
    expect(watchAlert(device('2026-11-03T11:30:00Z'), now)).toBe('offline');
    expect(watchAlert(device('2026-11-03T14:30:00Z', 15), now)).toBe('battery');
    expect(watchAlert(device('2026-11-03T12:30:00Z', 20), now)).toBeNull();
  });
});

describe('timeAgo', () => {
  it('dice cuánto hace en palabras', () => {
    expect(timeAgo('2026-11-03T14:35:00Z', now)).toBe('hace 25 minutos');
    expect(timeAgo('2026-11-03T12:00:00Z', now)).toBe('hace 3 horas');
  });
});
