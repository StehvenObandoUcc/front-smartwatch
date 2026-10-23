import { useEffect, useRef, useState } from 'react';
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

/** Tiempo para «Deshacer» un «Tomada»: el backend no corrige una dosis registrada, así que se espera. */
export const UNDO_MS = 5_000;

/**
 * Marcar una dosis como tomada u omitida desde la web (omitir pide confirmación). «Tomada» se ve al
 * momento con su hora y «Deshacer»; se envía pasados [UNDO_MS], o en cuanto se sale de la pantalla.
 */
export function DoseActions({ patientId, scheduleId, scheduledAt, label }: Props) {
  const queryClient = useQueryClient();
  // Un solo eventId por dosis: si se reintenta tras un fallo de red, el backend no la duplica.
  const [eventId] = useState(() => crypto.randomUUID());
  const [status, setStatus] = useState<DoseEventStatus>('TAKEN');
  const [confirmingSkip, setConfirmingSkip] = useState(false);
  // «Tomada» a la espera de enviarse (la hora en que se tocó), o null.
  const [holding, setHolding] = useState<Date | null>(null);
  const undoRef = useRef<HTMLButtonElement>(null);
  // «Deshacer» marca que la toma en espera no debe enviarse al limpiar el efecto.
  const undone = useRef(false);
  const record = useCreatePatientDoseEvents({
    mutation: { onSuccess: () => queryClient.invalidateQueries() },
    // Si la página se cierra con la toma en espera, el envío sigue (keepalive).
    request: { keepalive: true },
  });

  function send(next: DoseEventStatus, at = new Date()) {
    setStatus(next);
    setConfirmingSkip(false);
    record.mutate({ patientId, data: doseEventBatch(eventId, scheduleId, scheduledAt, next, at) });
  }

  // Mientras «Tomada» espera: se envía al acabar el tiempo, al salir de la pantalla o al cerrar la página.
  const sendRef = useRef(send);
  useEffect(() => {
    sendRef.current = send;
  });
  useEffect(() => {
    if (!holding) return;
    let sent = false;
    const flush = () => {
      if (sent) return;
      sent = true;
      sendRef.current('TAKEN', holding);
    };
    const timer = window.setTimeout(() => {
      flush();
      setHolding(null);
    }, UNDO_MS);
    window.addEventListener('pagehide', flush);
    undoRef.current?.focus();
    return () => {
      window.clearTimeout(timer);
      window.removeEventListener('pagehide', flush);
      // Al salir de la pantalla con la toma en espera, se envía; tras «Deshacer», no.
      if (!undone.current) flush();
      undone.current = false;
    };
  }, [holding]);

  function undo() {
    undone.current = true;
    setHolding(null);
  }

  if (holding) {
    return (
      <div role="status" className="flex flex-wrap items-center gap-2">
        <Text tone="success" className="font-bold">
          Tomada · {holding.toLocaleTimeString('es', { hour: '2-digit', minute: '2-digit' })}
        </Text>
        <Button ref={undoRef} variant="secondary" icon="undo" onClick={undo}>
          Deshacer
        </Button>
      </div>
    );
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
          <Button variant="secondary" icon="back" onClick={() => setConfirmingSkip(false)}>
            No
          </Button>
          <Button
            variant="danger"
            icon="skipped"
            loading={record.isPending}
            onClick={() => send('SKIPPED')}
          >
            Sí, omitir
          </Button>
        </>
      ) : (
        <>
          <Button
            icon="taken"
            loading={record.isPending}
            aria-label={`Marcar ${label} como tomada`}
            onClick={() => setHolding(new Date())}
          >
            Tomada
          </Button>
          <Button
            variant="secondary"
            icon="skipped"
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
