import { useState } from 'react';

import { useGetPatientPlan } from '../api/generated';
import { Skeleton } from '../components/atoms/Skeleton/Skeleton';
import { EmptyState } from '../components/molecules/EmptyState';
import { ErrorRetry } from '../components/molecules/ErrorRetry';
import { SignBar } from '../components/molecules/SignBar';
import { DoseRow } from '../features/doses/DoseRow';
import { doseState, useDoseHistory } from '../features/doses/doseState';
import { groupByDay } from '../features/plan/groupByDay';
import { usePatient } from '../features/patients/PatientGate';
import { formatDay, localDate } from '../lib/dates';
import { errorMessage } from '../lib/errors';

/** Los próximos 7 días: una señal por día y sus dosis con estado; las de hoy se pueden marcar. */
export function AgendaPage() {
  const { patientId } = usePatient();
  const plan = useGetPatientPlan(patientId);
  const history = useDoseHistory(patientId);
  // Hora de referencia fija por visita: al volver a la pantalla se recalcula.
  const [now] = useState(() => Date.now());

  if (plan.isPending) return <Skeleton label="Cargando agenda" rows={5} />;
  if (plan.isError) {
    return <ErrorRetry message={errorMessage(plan.error)} onRetry={() => void plan.refetch()} />;
  }

  const { patientTimezone: tz, doses } = plan.data;
  if (doses.length === 0) {
    return (
      <EmptyState
        icon="agenda"
        title="No hay tomas en los próximos 7 días"
        body="Agrega un medicamento con su horario y la semana aparecerá aquí."
      />
    );
  }
  const today = localDate(new Date(now), tz);

  return (
    <div className="flex flex-col gap-6">
      {groupByDay(doses, tz).map(({ day, doses: dayDoses }) => (
        <section key={day} aria-labelledby={`day-${day}`} className="flex flex-col gap-3">
          <SignBar
            id={`day-${day}`}
            icon={day === today ? 'today' : 'agenda'}
            title={day === today ? 'Hoy' : formatDay(dayDoses[0]?.scheduledAt ?? day, tz)}
            meta={dayDoses.length === 1 ? '1 dosis' : `${dayDoses.length} dosis`}
            tone={day === today ? 'sign' : 'neutral'}
          />
          <ul className="rows">
            {dayDoses.map((dose) => (
              <DoseRow
                key={`${dose.scheduleId}-${dose.scheduledAt}`}
                dose={dose}
                state={doseState(dose, history.index, now)}
                timeZone={tz}
                patientId={patientId}
                recordable={day === today}
              />
            ))}
          </ul>
        </section>
      ))}
    </div>
  );
}
