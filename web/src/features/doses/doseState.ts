import { useListDoseHistory, type DoseHistoryItem, type PlannedDose } from '../../api/generated';

/** Lo que el cuidador necesita saber de cada dosis de un vistazo. */
export type DoseState = 'taken' | 'skipped' | 'missed' | 'due' | 'upcoming';

/** Tono de cada estado; la palabra está en i18n (doses.state.*): el color nunca va solo. */
export const doseStateTone: Record<
  DoseState,
  'success' | 'neutral' | 'danger' | 'warning' | 'outline'
> = {
  taken: 'success',
  skipped: 'neutral',
  missed: 'danger',
  due: 'warning',
  upcoming: 'outline',
};

/** Clave común entre el plan y el historial (mismo instante aunque cambie el formato ISO). */
function key(scheduleId: string, scheduledAt: string) {
  return `${scheduleId}@${new Date(scheduledAt).getTime()}`;
}

/** Registro del historial de cada dosis, por dosis. */
export type HistoryIndex = Map<string, DoseHistoryItem>;

export function historyIndex(items: DoseHistoryItem[]): HistoryIndex {
  return new Map(items.map((item) => [key(item.scheduleId, item.scheduledAt), item]));
}

/** Evento registrado de la dosis (para deshacerlo), o null si no tiene. */
export function eventOf(dose: PlannedDose, history: HistoryIndex): string | null {
  return history.get(key(dose.scheduleId, dose.scheduledAt))?.eventId ?? null;
}

/**
 * Estado de una dosis del plan cruzándola con el historial. El backend informa MISSED a los 60 min;
 * antes de eso, una dosis ya en su hora y sin registro está «Pendiente».
 */
export function doseState(dose: PlannedDose, history: HistoryIndex, now: number): DoseState {
  const status = history.get(key(dose.scheduleId, dose.scheduledAt))?.status;
  if (status === 'TAKEN') return 'taken';
  if (status === 'SKIPPED') return 'skipped';
  if (status === 'MISSED') return 'missed';
  return new Date(dose.scheduledAt).getTime() <= now ? 'due' : 'upcoming';
}

/** Se puede marcar desde la web mientras no tenga registro: pendiente o no tomada. */
export function canRecord(state: DoseState) {
  return state === 'due' || state === 'missed';
}

/** Historial reciente (los últimos 7 días por defecto del backend) indexado por dosis. */
export function useDoseHistory(patientId: string) {
  const history = useListDoseHistory(patientId, { limit: 100 });
  return { ...history, index: historyIndex(history.data?.items ?? []) };
}

/**
 * La dosis que toca mirar ahora: la primera aún por marcar (pendiente o no tomada) y, si no hay,
 * la próxima que viene. `null` si ya no queda ninguna. Las dosis van en orden de hora.
 */
export function nextDose<T>(doses: T[], states: DoseState[]): { dose: T; state: DoseState } | null {
  const i = states.findIndex(canRecord);
  const j = i >= 0 ? i : states.indexOf('upcoming');
  return j >= 0 ? { dose: doses[j] as T, state: states[j] as DoseState } : null;
}
