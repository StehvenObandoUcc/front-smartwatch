import { useState } from 'react';

import { useGetPatientPlan } from '../../api/generated';
import { Skeleton } from '../../components/atoms/Skeleton/Skeleton';
import { Text } from '../../components/atoms/Text/Text';
import { EmptyState } from '../../components/molecules/EmptyState';
import { ErrorRetry } from '../../components/molecules/ErrorRetry';
import { SignBar } from '../../components/molecules/SignBar';
import { localDate } from '../../lib/dates';
import { errorMessage } from '../../lib/errors';
import { usePatient } from '../patients/PatientGate';
import { DoseRow } from './DoseRow';
import { doseState, useDoseHistory } from './doseState';
import { FRANJAS, groupByFranja, type Franja } from './franja';

// Cada franja es una señal de su color, con su pictograma: sol, tarde y noche.
const franjaSign: Record<
  Franja,
  { tone: 'sun' | 'dusk' | 'night'; icon: 'morning' | 'afternoon' | 'night' }
> = {
  morning: { tone: 'sun', icon: 'morning' },
  afternoon: { tone: 'dusk', icon: 'afternoon' },
  night: { tone: 'night', icon: 'night' },
};

/**
 * «Hoy» como un pastillero: primero cuántas van tomadas y, por franja, cada dosis con su estado.
 * Las pendientes llevan Tomada / Omitir para marcarlas desde la web.
 */
export function TodayPillbox() {
  const { patientId, timezone } = usePatient();
  const plan = useGetPatientPlan(patientId);
  const history = useDoseHistory(patientId);
  // Día de referencia fijo por visita: al volver a la pantalla se recalcula.
  const [now] = useState(() => new Date());

  if (plan.isPending) return <Skeleton label="Cargando tu día" rows={3} />;
  if (plan.isError) {
    return <ErrorRetry message={errorMessage(plan.error)} onRetry={() => void plan.refetch()} />;
  }

  const groups = groupByFranja(plan.data.doses, localDate(now, timezone), timezone);
  const today = FRANJAS.flatMap((f) => groups[f.id]);
  if (today.length === 0) {
    return (
      <EmptyState
        icon="medications"
        title="Hoy no hay dosis"
        body="Las dosis aparecen aquí cuando agregas un medicamento con su horario."
      />
    );
  }
  const states = today.map((dose) => doseState(dose, history.index, now.getTime()));
  const taken = states.filter((s) => s === 'taken').length;

  return (
    <section aria-labelledby="pillbox-title" className="flex flex-col gap-4">
      <div className="flex items-baseline justify-between gap-4">
        <Text id="pillbox-title" variant="title" as="h2">
          {taken} de {today.length} tomadas
        </Text>
        <Text tone="muted">{today.length === 1 ? '1 dosis hoy' : `${today.length} dosis hoy`}</Text>
      </div>
      <div className="grid grid-cols-1 gap-5 desktop:grid-cols-3">
        {FRANJAS.map(({ id, label, range }) => (
          <section key={id} aria-labelledby={`franja-${id}`} className="flex flex-col gap-3">
            <SignBar id={`franja-${id}`} as="h3" title={label} meta={range} {...franjaSign[id]} />
            {groups[id].length === 0 ? (
              <Text tone="muted" className="px-1">
                Nada programado
              </Text>
            ) : (
              <ul className="flex flex-col gap-3">
                {groups[id].map((dose) => (
                  <DoseRow
                    key={`${dose.scheduleId}-${dose.scheduledAt}`}
                    dose={dose}
                    state={doseState(dose, history.index, now.getTime())}
                    timeZone={timezone}
                    patientId={patientId}
                    recordable
                  />
                ))}
              </ul>
            )}
          </section>
        ))}
      </div>
    </section>
  );
}
