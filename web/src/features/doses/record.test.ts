import { describe, expect, it } from 'vitest';

import { doseEventBatch, resultMessage } from './record';

describe('doseEventBatch', () => {
  it('arma un único evento con el eventId dado y la hora de la acción en UTC', () => {
    const now = new Date('2026-10-05T12:00:00Z');
    const batch = doseEventBatch('e1', 's1', '2026-10-05T08:00:00-05:00', 'TAKEN', now);
    expect(batch.events).toEqual([
      {
        eventId: 'e1',
        scheduleId: 's1',
        scheduledAt: '2026-10-05T08:00:00-05:00',
        status: 'TAKEN',
        actedAt: '2026-10-05T12:00:00.000Z',
      },
    ]);
  });
});

describe('resultMessage', () => {
  it('created y duplicate confirman la acción', () => {
    expect(resultMessage({ eventId: 'e1', outcome: 'created' }, 'SKIPPED')).toEqual({
      text: 'Marcada como omitida.',
      ok: true,
    });
    expect(resultMessage({ eventId: 'e1', outcome: 'duplicate' }, 'TAKEN').ok).toBe(true);
  });

  it('una dosis ya registrada no es un error', () => {
    const r = resultMessage(
      { eventId: 'e1', outcome: 'rejected', code: 'dose_already_recorded' },
      'TAKEN',
    );
    expect(r).toEqual({ text: 'Esta dosis ya estaba registrada.', ok: true });
  });

  it('otro rechazo es un error', () => {
    expect(
      resultMessage({ eventId: 'e1', outcome: 'rejected', code: 'dose_not_found' }, 'TAKEN').ok,
    ).toBe(false);
  });
});
