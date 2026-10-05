import { useState } from 'react';
import { useQueryClient } from '@tanstack/react-query';

import { useCreatePatientDoseEvents, type DoseEventStatus } from '../../api/generated';
import { Button } from '../../components/atoms/Button/Button';
import { Text } from '../../components/atoms/Text/Text';
import { errorMessage } from '../../lib/errors';
import { doseEventBatch, resultMessage } from './record';

type Props = {
  patientId: string;
  scheduleId: string;
  scheduledAt: string;
  /** Nombre del medicamento, para que cada botón se distinga con lector de pantalla. */
  label: string;
};

/** Marcar una dosis como tomada u omitida desde la web (omitir pide confirmación). */
export function DoseActions({ patientId, scheduleId, scheduledAt, label }: Props) {
  const queryClient = useQueryClient();
  // Un solo eventId por dosis: si se reintenta tras un fallo de red, el backend no la duplica.
  const [eventId] = useState(() => crypto.randomUUID());
  const [status, setStatus] = useState<DoseEventStatus>('TAKEN');
  const [confirmingSkip, setConfirmingSkip] = useState(false);
  const record = useCreatePatientDoseEvents({
    mutation: { onSuccess: () => queryClient.invalidateQueries() },
  });

  function send(next: DoseEventStatus) {
    setStatus(next);
    setConfirmingSkip(false);
    record.mutate({ patientId, data: doseEventBatch(eventId, scheduleId, scheduledAt, next) });
  }

  const result = record.data?.results[0];
  if (result) {
    const { text, ok } = resultMessage(result, status);
    return (
      <div role="status">
        <Text tone={ok ? 'success' : 'danger'}>{text}</Text>
      </div>
    );
  }

  return (
    <div className="flex flex-wrap items-center gap-2">
      {confirmingSkip ? (
        <>
          <Text>¿Omitir {label}?</Text>
          <Button variant="danger" loading={record.isPending} onClick={() => send('SKIPPED')}>
            Sí, omitir
          </Button>
          <Button variant="secondary" onClick={() => setConfirmingSkip(false)}>
            No
          </Button>
        </>
      ) : (
        <>
          <Button
            loading={record.isPending}
            aria-label={`Marcar ${label} como tomada`}
            onClick={() => send('TAKEN')}
          >
            Tomada
          </Button>
          <Button
            variant="secondary"
            disabled={record.isPending}
            aria-label={`Marcar ${label} como omitida`}
            onClick={() => setConfirmingSkip(true)}
          >
            Omitir
          </Button>
        </>
      )}
      {record.error && (
        <div role="alert">
          <Text tone="danger">{errorMessage(record.error)}</Text>
        </div>
      )}
    </div>
  );
}
