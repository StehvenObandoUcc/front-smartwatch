import { useState } from 'react';

import { useGetAdherence, useListDoseHistory, type DoseStatus } from '../api/generated';
import { Badge } from '../components/atoms/Badge/Badge';
import { Button } from '../components/atoms/Button/Button';
import { Spinner } from '../components/atoms/Spinner/Spinner';
import { Text } from '../components/atoms/Text/Text';
import { usePatient } from '../features/patients/PatientGate';
import { daysAgo, formatDay, formatTime, localDate } from '../lib/dates';
import { errorMessage } from '../lib/errors';

const statusView: Record<DoseStatus, { label: string; tone: 'success' | 'warning' | 'danger' }> = {
  TAKEN: { label: 'Tomada', tone: 'success' },
  SKIPPED: { label: 'Omitida', tone: 'warning' },
  MISSED: { label: 'No tomada', tone: 'danger' },
};

export function AdherencePage() {
  const { patientId, timezone } = usePatient();
  const [days, setDays] = useState(7);
  const range = { from: daysAgo(days - 1, timezone), to: localDate(new Date(), timezone) };
  const adherence = useGetAdherence(patientId, range);
  const history = useListDoseHistory(patientId, { ...range, limit: 50 });

  if (adherence.isPending || history.isPending) return <Spinner label="Cargando historial" />;
  if (adherence.isError || history.isError) {
    return (
      <div role="alert" className="flex flex-col items-start gap-2">
        <Text tone="danger">{errorMessage(adherence.error ?? history.error)}</Text>
        <Button
          variant="secondary"
          onClick={() => {
            void adherence.refetch();
            void history.refetch();
          }}
        >
          Reintentar
        </Button>
      </div>
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

      <section aria-labelledby="adh-title" className="flex flex-col gap-1">
        <Text id="adh-title" variant="subtitle" as="h2">
          Adherencia
        </Text>
        <Text variant="display" as="p">
          {percentage === null ? 'Sin datos' : `${Math.round(percentage)} %`}
        </Text>
        <Text tone="muted">
          {taken} tomadas · {skipped} omitidas · {missed} no tomadas
        </Text>
      </section>

      <section aria-labelledby="hist-title" className="flex flex-col gap-2">
        <Text id="hist-title" variant="subtitle" as="h2">
          Historial
        </Text>
        {history.data.items.length === 0 && (
          <Text tone="muted">Aún no hay tomas en este rango.</Text>
        )}
        <ul className="flex flex-col gap-2">
          {history.data.items.map((item) => (
            <li
              key={`${item.scheduleId}-${item.scheduledAt}`}
              className="flex items-center justify-between gap-4 rounded-md border-2 border-border bg-surface-raised p-4"
            >
              <div>
                <Text as="p">
                  {item.medicationName} · {item.dosage}
                </Text>
                <Text tone="muted" variant="caption">
                  {formatDay(item.scheduledAt, timezone)}, {formatTime(item.scheduledAt, timezone)}
                </Text>
              </div>
              <Badge tone={statusView[item.status].tone}>{statusView[item.status].label}</Badge>
            </li>
          ))}
        </ul>
      </section>
    </div>
  );
}
