import { useState } from 'react';

import {
  useGetAdherence,
  useListDoseHistory,
  useListMedications,
  type DoseStatus,
} from '../api/generated';
import { Badge } from '../components/atoms/Badge/Badge';
import { Button } from '../components/atoms/Button/Button';
import { Skeleton } from '../components/atoms/Skeleton/Skeleton';
import { Plate } from '../components/atoms/Plate/Plate';
import { Text } from '../components/atoms/Text/Text';
import { EmptyState } from '../components/molecules/EmptyState';
import { ErrorRetry } from '../components/molecules/ErrorRetry';
import { SignBar } from '../components/molecules/SignBar';
import { AdherenceChart } from '../features/doses/AdherenceChart';
import { DoseActions } from '../features/doses/DoseActions';
import { UndoDose } from '../features/doses/UndoDose';
import { usePatient } from '../features/patients/PatientGate';
import { countText } from '../features/reports/ReportView';
import { daysAgo, formatDay, formatTime, localDate } from '../lib/dates';
import { errorMessage } from '../lib/errors';

// Los mismos estados, palabras y pictogramas que en Hoy y en el reloj.
const statusView: Record<
  DoseStatus,
  { label: string; tone: 'success' | 'neutral' | 'danger'; icon: 'taken' | 'skipped' | 'missed' }
> = {
  TAKEN: { label: 'Tomada', tone: 'success', icon: 'taken' },
  SKIPPED: { label: 'Omitida', tone: 'neutral', icon: 'skipped' },
  MISSED: { label: 'No tomada', tone: 'danger', icon: 'missed' },
};

export function AdherencePage() {
  const { patientId, timezone } = usePatient();
  const [days, setDays] = useState(7);
  const range = { from: daysAgo(days - 1, timezone), to: localDate(new Date(), timezone) };
  const adherence = useGetAdherence(patientId, range);
  const history = useListDoseHistory(patientId, { ...range, limit: 50 });
  // El historial no trae el color: se toma de la lista de medicamentos (ya en caché).
  const medications = useListMedications(patientId);
  const colorOf = new Map(medications.data?.items.map((m) => [m.id, m.color]));

  if (adherence.isPending || history.isPending) return <Skeleton label="Cargando historial" />;
  if (adherence.isError || history.isError) {
    return (
      <ErrorRetry
        message={errorMessage(adherence.error ?? history.error)}
        onRetry={() => {
          void adherence.refetch();
          void history.refetch();
        }}
      />
    );
  }

  const { percentage, taken, skipped, missed } = adherence.data;
  return (
    <div className="flex flex-col gap-6">
      <div className="flex gap-2" role="group" aria-label="Rango">
        {[7, 30].map((n) => (
          <Button key={n} variant={n === days ? 'primary' : 'secondary'} onClick={() => setDays(n)}>
            {n} días
          </Button>
        ))}
      </div>

      <section aria-labelledby="adh-title" className="card flex flex-col gap-3 p-4">
        <SignBar id="adh-title" icon="history" title="Dosis tomadas" />
        {percentage === null ? (
          <Text tone="muted">
            Aún no hay dosis vencidas en este rango. El porcentaje aparece cuando pase la primera.
          </Text>
        ) : (
          <>
            <Text variant="display" as="p">
              {Math.round(percentage)} %
            </Text>
            <div className="flex flex-wrap gap-2">
              <Badge tone="success" icon="taken">
                {countText(taken, 'tomada', 'tomadas')}
              </Badge>
              <Badge tone="neutral" icon="skipped">
                {countText(skipped, 'omitida', 'omitidas')}
              </Badge>
              <Badge tone="danger" icon="missed">
                {countText(missed, 'no tomada', 'no tomadas')}
              </Badge>
            </div>
          </>
        )}
      </section>

      <section aria-labelledby="chart-title" className="flex flex-col gap-3">
        <SignBar id="chart-title" icon="history" title="Últimos 7 días, día a día" tone="neutral" />
        <AdherenceChart />
      </section>

      <section aria-labelledby="hist-title" className="flex flex-col gap-3">
        <Text id="hist-title" variant="subtitle" as="h2">
          Cada dosis
        </Text>
        {history.data.items.length === 0 && (
          <EmptyState
            icon="history"
            title="Aún no hay dosis en este rango"
            body="Cada dosis aparece aquí una hora después de su horario, tomada o no."
          />
        )}
        <ul className="rows">
          {history.data.items.map((item) => (
            <li key={`${item.scheduleId}-${item.scheduledAt}`} className="flex flex-col gap-3">
              <div className="flex flex-wrap items-center gap-x-3 gap-y-2">
                {colorOf.get(item.medicationId) ? (
                  <Plate icon="medications" hex={colorOf.get(item.medicationId) ?? ''} />
                ) : (
                  <Plate icon="medications" tone="neutral" />
                )}
                <div className="min-w-0 flex-1 basis-40">
                  <p className="text-lg leading-tight font-extrabold break-words">
                    {item.medicationName}
                  </p>
                  <Text tone="muted">
                    {item.dosage} · {formatDay(item.scheduledAt, timezone)}
                  </Text>
                </div>
                <span className="ml-auto text-lg font-extrabold">
                  {formatTime(item.scheduledAt, timezone)}
                </span>
              </div>
              <div className="flex flex-wrap items-center gap-2">
                <Badge tone={statusView[item.status].tone} icon={statusView[item.status].icon}>
                  {statusView[item.status].label}
                </Badge>
                {/* Se olvidó de marcarla en el reloj: se puede registrar desde aquí. */}
                {item.status === 'MISSED' && (
                  <DoseActions
                    patientId={patientId}
                    scheduleId={item.scheduleId}
                    scheduledAt={item.scheduledAt}
                    label={item.medicationName}
                  />
                )}
                {/* Se marcó por error: se deshace y la dosis vuelve a pendiente. */}
                {item.eventId && (
                  <UndoDose
                    patientId={patientId}
                    eventId={item.eventId}
                    label={item.medicationName}
                  />
                )}
              </div>
            </li>
          ))}
        </ul>
      </section>
    </div>
  );
}
