import { useState } from 'react';

import { useGetPatientPlan } from '../api/generated';
import { Button } from '../components/atoms/Button/Button';
import { ColorDot } from '../components/atoms/ColorDot/ColorDot';
import { Spinner } from '../components/atoms/Spinner/Spinner';
import { Text } from '../components/atoms/Text/Text';
import { DoseActions } from '../features/doses/DoseActions';
import { groupByDay } from '../features/plan/groupByDay';
import { usePatient } from '../features/patients/PatientGate';
import { formatDay, formatTime, localDate } from '../lib/dates';
import { errorMessage } from '../lib/errors';

export function AgendaPage() {
  const { patientId } = usePatient();
  const plan = useGetPatientPlan(patientId);
  // Hora de referencia fija por visita: al volver a la pantalla se recalcula.
  const [now] = useState(() => Date.now());

  if (plan.isPending) return <Spinner label="Cargando agenda" />;
  if (plan.isError) {
    return (
      <div role="alert" className="flex flex-col items-start gap-2">
        <Text tone="danger">{errorMessage(plan.error)}</Text>
        <Button variant="secondary" onClick={() => void plan.refetch()}>
          Reintentar
        </Button>
      </div>
    );
  }

  const { patientTimezone: tz, doses } = plan.data;
  if (doses.length === 0) {
    return <Text tone="muted">No hay tomas en los próximos 7 días.</Text>;
  }
  const today = localDate(new Date(now), tz);

  return (
    <div className="flex flex-col gap-6">
      {groupByDay(doses, tz).map(({ day, doses: dayDoses }) => (
        <section key={day} aria-labelledby={`day-${day}`} className="flex flex-col gap-2">
          <Text id={`day-${day}`} variant="subtitle" as="h2">
            {day === today ? 'Hoy' : formatDay(dayDoses[0]?.scheduledAt ?? day, tz)}
          </Text>
          <ul className="flex flex-col gap-2">
            {dayDoses.map((dose) => (
              <li
                key={`${dose.scheduleId}-${dose.scheduledAt}`}
                className="flex flex-wrap items-center gap-4 card p-4"
              >
                <Text variant="subtitle" as="p" className="min-w-16 tabular-nums">
                  {formatTime(dose.scheduledAt, tz)}
                </Text>
                <ColorDot hex={dose.color} />
                <div className="min-w-0 flex-1">
                  <Text as="p">{dose.medicationName}</Text>
                  <Text tone="muted" variant="caption">
                    {dose.dosage}
                  </Text>
                </div>
                {/* Solo las de hoy que ya llegaron a su hora; si el reloj ya la registró, el backend lo indica. */}
                {day === today && new Date(dose.scheduledAt).getTime() <= now && (
                  <DoseActions
                    patientId={patientId}
                    scheduleId={dose.scheduleId}
                    scheduledAt={dose.scheduledAt}
                    label={dose.medicationName}
                  />
                )}
              </li>
            ))}
          </ul>
        </section>
      ))}
    </div>
  );
}
