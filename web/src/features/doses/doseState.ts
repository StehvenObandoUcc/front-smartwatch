import { useListDoseHistory, type DoseHistoryItem, type PlannedDose } from '../../api/generated';

/** Lo que el cuidador necesita saber de cada dosis de un vistazo. */
export type DoseState = 'taken' | 'skipped' | 'missed' | 'due' | 'upcoming';

/** Texto y tono de cada estado: el color nunca va solo. Sin etiqueta para las que aún no tocan. */
export const doseStateLabel: Record<
  Exclude<DoseState, 'upcoming'>,
  { text: string; tone: 'success' | 'neutral' | 'danger' | 'warning' }
> = {
  taken: { text: 'Tomada', tone: 'success' },
  skipped: { text: 'Omitida', tone: 'neutral' },
  missed: { text: 'No tomada', tone: 'danger' },
  due: { text: 'Pendiente', tone: 'warning' },
};

/** Clave común entre el plan y el historial (mismo instante aunque cambie el formato ISO). */
function key(scheduleId: string, scheduledAt: string) {
  return `${scheduleId}@${new Date(scheduledAt).getTime()}`;
}

export function historyIndex(items: DoseHistoryItem[]): Map<string, DoseHistoryItem['status']> {
  return new Map(items.map((item) => [key(item.scheduleId, item.scheduledAt), item.status]));
}

/**
 * Estado de una dosis del plan cruzándola con el historial. El backend informa MISSED a los 60 min;
 * antes de eso, una dosis ya en su hora y sin registro está «Pendiente».
 */
export function doseState(
  dose: PlannedDose,
  history: Map<string, DoseHistoryItem['status']>,
  now: number,
): DoseState {
  const status = history.get(key(dose.scheduleId, dose.scheduledAt));
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
