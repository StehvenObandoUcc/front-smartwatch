import { useState } from 'react';

import { useGetPatientPlan } from '../../api/generated';
import { ColorDot } from '../../components/atoms/ColorDot/ColorDot';
import { Spinner } from '../../components/atoms/Spinner/Spinner';
import { Text } from '../../components/atoms/Text/Text';
import { ErrorRetry } from '../../components/molecules/ErrorRetry';
import { cn } from '../../lib/cn';
import { formatTime, localDate } from '../../lib/dates';
import { errorMessage } from '../../lib/errors';
import { usePatient } from '../patients/PatientGate';
import { FRANJAS, groupByFranja, type Franja } from './franja';

// Clases completas para que Tailwind las detecte al compilar.
const headerClass: Record<Franja, string> = {
  morning: 'bg-morning text-on-morning',
  afternoon: 'bg-afternoon text-on-afternoon',
  night: 'bg-night text-on-night',
};

const icon: Record<Franja, string[]> = {
  morning: [
    'M12 16a4 4 0 1 0 0-8 4 4 0 0 0 0 8Z',
    'M12 2v2M12 20v2M2 12h2M20 12h2M5 5l1.5 1.5M17.5 17.5 19 19M5 19l1.5-1.5M17.5 6.5 19 5',
  ],
  afternoon: ['M4 18h16', 'M7 18a5 5 0 0 1 10 0', 'M12 7v3M5 11l2 2M19 11l-2 2'],
  night: ['M20 14.5A8 8 0 1 1 9.5 4a6.5 6.5 0 0 0 10.5 10.5Z'],
};

/** "Hoy" como un pastillero: un compartimento por franja del día con sus dosis. */
export function TodayPillbox() {
  const { patientId, timezone } = usePatient();
  const plan = useGetPatientPlan(patientId);
  // Día de referencia fijo por visita: al volver a la pantalla se recalcula.
  const [now] = useState(() => new Date());

  if (plan.isPending) return <Spinner label="Cargando tu día" />;
  if (plan.isError) {
    return <ErrorRetry message={errorMessage(plan.error)} onRetry={() => void plan.refetch()} />;
  }

  const groups = groupByFranja(plan.data.doses, localDate(now, timezone), timezone);
  const total = FRANJAS.reduce((sum, f) => sum + groups[f.id].length, 0);

  return (
    <section aria-labelledby="pillbox-title" className="flex flex-col gap-3">
      <Text id="pillbox-title" variant="title" as="h2">
        {total === 0 ? 'Hoy no tienes dosis' : `Hoy: ${total} dosis`}
      </Text>
      <ul className="grid gap-4 tablet:grid-cols-3">
        {FRANJAS.map(({ id, label, range }) => (
          <li key={id} className="card overflow-hidden">
            <div className={cn('flex items-center gap-3 px-4 py-3', headerClass[id])}>
              <svg
                width="24"
                height="24"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
                aria-hidden="true"
              >
                {icon[id].map((d) => (
                  <path key={d} d={d} />
                ))}
              </svg>
              <div>
                <h3 className="font-display text-lg leading-tight font-semibold">{label}</h3>
                <p className="text-caption leading-tight">{range}</p>
              </div>
            </div>
            <ul className="flex flex-col gap-3 p-4">
              {groups[id].length === 0 && <li className="text-text-muted">Nada programado</li>}
              {groups[id].map((dose) => (
                <li
                  key={`${dose.scheduleId}-${dose.scheduledAt}`}
                  className="flex items-center gap-3"
                >
                  <ColorDot hex={dose.color} />
                  <div className="min-w-0 flex-1">
                    <p className="font-semibold">{dose.medicationName}</p>
                    <p className="text-caption text-text-muted">{dose.dosage}</p>
                  </div>
                  <span className="font-display font-bold">
                    {formatTime(dose.scheduledAt, timezone)}
                  </span>
                </li>
              ))}
            </ul>
          </li>
        ))}
      </ul>
    </section>
  );
}
