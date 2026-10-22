import type { Schedule } from '../../api/generated';

const DAY = ['', 'lun', 'mar', 'mié', 'jue', 'vie', 'sáb', 'dom'];

/** «08:00 · 20:00 — todos los días»: el horario a la vista en la lista, sin entrar a Editar. */
export function scheduleText(schedule: Pick<Schedule, 'times' | 'daysOfWeek'>): string {
  const times = [...schedule.times].sort().join(' · ');
  const days = [...schedule.daysOfWeek].sort((a, b) => a - b);
  const joined = days.join(',');
  const when =
    joined === '1,2,3,4,5,6,7'
      ? 'todos los días'
      : joined === '1,2,3,4,5'
        ? 'de lunes a viernes'
        : joined === '6,7'
          ? 'fines de semana'
          : days.map((d) => DAY[d]).join(', ');
  return `${times} — ${when}`;
}
