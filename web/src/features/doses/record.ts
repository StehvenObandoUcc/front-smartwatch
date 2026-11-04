import { i18n } from '../../i18n';
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
      text: i18n.t(status === 'TAKEN' ? 'doses.markedTaken' : 'doses.markedSkipped'),
      ok: true,
    };
  }
  if (result.code === 'dose_already_recorded') {
    return { text: i18n.t('doses.alreadyRecorded'), ok: true };
  }
  return { text: i18n.t('doses.notRecorded'), ok: false };
}
