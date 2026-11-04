import type { Schedule } from '../../api/generated';
import { i18n } from '../../i18n';

type Weekday = 1 | 2 | 3 | 4 | 5 | 6 | 7;

/** «08:00 · 20:00 — todos los días»: el horario a la vista en la lista, sin entrar a Editar. */
export function scheduleText(schedule: Pick<Schedule, 'times' | 'daysOfWeek'>): string {
  const times = [...schedule.times].sort().join(' · ');
  const days = [...schedule.daysOfWeek].sort((a, b) => a - b);
  const joined = days.join(',');
  const when =
    joined === '1,2,3,4,5,6,7'
      ? i18n.t('medications.schedule.everyDay')
      : joined === '1,2,3,4,5'
        ? i18n.t('medications.schedule.weekdays')
        : joined === '6,7'
          ? i18n.t('medications.schedule.weekends')
          : days.map((d) => i18n.t(`medications.schedule.day.${d as Weekday}`)).join(', ');
  return `${times} — ${when}`;
}
