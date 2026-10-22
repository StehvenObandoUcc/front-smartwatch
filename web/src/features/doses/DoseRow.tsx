import type { PlannedDose } from '../../api/generated';
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
};

/**
 * Una dosis como señal, en una fila de una lista `rows`: placa del medicamento en su color, nombre
 * y dosis; a la derecha la hora grande y la placa de estado (bajan a otra línea si no caben). Si
 * falta registrarla, debajo van Tomada / Omitir.
 */
export function DoseRow({ dose, state, timeZone, patientId, recordable }: Props) {
  const actionable = recordable && canRecord(state);
  return (
    <li className="flex flex-col gap-3">
      <div className="flex flex-wrap items-center gap-x-3 gap-y-2">
        <Plate icon="medications" hex={dose.color} />
        <div className="min-w-0 flex-1 basis-40">
          <p className="text-lg leading-tight font-extrabold break-words">{dose.medicationName}</p>
          <p className="text-text-muted">{dose.dosage}</p>
        </div>
        <div className="ml-auto flex items-center gap-3">
          <span className="text-lg font-extrabold">{formatTime(dose.scheduledAt, timeZone)}</span>
          {/* «Más tarde» solo en las de hoy: en los días que vienen, todas lo son. */}
          {(recordable || state !== 'upcoming') && <DoseStateBadge state={state} />}
        </div>
      </div>
      {actionable && (
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
