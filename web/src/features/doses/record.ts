import type { DoseEventBatch, DoseEventResult, DoseEventStatus } from '../../api/generated';

/** Un evento por dosis; el eventId lo genera el cliente y se reutiliza al reintentar (idempotente). */
export function doseEventBatch(
  eventId: string,
  scheduleId: string,
  scheduledAt: string,
  status: DoseEventStatus,
  now = new Date(),
): DoseEventBatch {
  return {
    events: [{ eventId, scheduleId, scheduledAt, status, actedAt: now.toISOString() }],
  };
}

export function resultMessage(
  result: DoseEventResult,
  status: DoseEventStatus,
): { text: string; ok: boolean } {
  if (result.outcome !== 'rejected') {
    return {
      text: status === 'TAKEN' ? 'Marcada como tomada.' : 'Marcada como omitida.',
      ok: true,
    };
  }
  if (result.code === 'dose_already_recorded') {
    return { text: 'Esta dosis ya estaba registrada.', ok: true };
  }
  return { text: 'No se pudo registrar esta dosis.', ok: false };
}
