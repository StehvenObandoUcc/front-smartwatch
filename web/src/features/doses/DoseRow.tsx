import { useState } from 'react';

import type { PlannedDose } from '../../api/generated';
import { Button } from '../../components/atoms/Button/Button';
import { Plate } from '../../components/atoms/Plate/Plate';
import { formatTime } from '../../lib/dates';
import { DoseActions } from './DoseActions';
import { DoseStateBadge } from './DoseStateBadge';
import { canRecord, type DoseState } from './doseState';

type Props = {
  dose: PlannedDose;
  state: DoseState;
  timeZone: string;
  patientId: string;
  /** Mostrar Tomada / Omitir cuando aún no tiene registro (solo las de hoy). */
  recordable: boolean;
  /** Tomada / Omitir detrás de «Marcar», cuando la acción principal vive en otro sitio (el bloque Hoy). */
  disclose?: boolean;
};

/**
 * Una dosis como señal, en una fila de una lista `rows`: placa del medicamento en su color, nombre
 * y dosis; a la derecha la hora grande y la placa de estado (bajan a otra línea si no caben). Si
 * falta registrarla, debajo van Tomada / Omitir (o «Marcar», que las muestra, con `disclose`).
 */
export function DoseRow({ dose, state, timeZone, patientId, recordable, disclose }: Props) {
  const actionable = recordable && canRecord(state);
  const [open, setOpen] = useState(!disclose);
  return (
    <li className="flex flex-col gap-3">
      <div className="flex flex-wrap items-center gap-x-3 gap-y-2">
        <Plate icon="medications" hex={dose.color} />
        <div className="min-w-0 flex-1 basis-40">
          <p className="text-lg leading-tight font-extrabold break-words">{dose.medicationName}</p>
          <p className="text-text-muted">{dose.dosage}</p>
        </div>
        <div className="ml-auto flex flex-wrap items-center gap-x-3 gap-y-2">
          <span className="text-lg font-extrabold">{formatTime(dose.scheduledAt, timeZone)}</span>
          {/* «Más tarde» solo en las de hoy: en los días que vienen, todas lo son. */}
          {(recordable || state !== 'upcoming') && <DoseStateBadge state={state} />}
          {actionable && disclose && (
            <Button
              variant="secondary"
              icon="edit"
              aria-expanded={open}
              aria-label={`Marcar ${dose.medicationName}`}
              onClick={() => setOpen((o) => !o)}
            >
              Marcar
            </Button>
          )}
        </div>
      </div>
      {actionable && open && (
        <DoseActions
          patientId={patientId}
          scheduleId={dose.scheduleId}
          scheduledAt={dose.scheduledAt}
          label={dose.medicationName}
        />
      )}
    </li>
  );
}
