import type { PlannedDose } from '../../api/generated';
import { Plate } from '../../components/atoms/Plate/Plate';
import { formatTime } from '../../lib/dates';
import { DoseActions } from './DoseActions';
import { DoseStateBadge } from './DoseStateBadge';
import { UndoDose } from './UndoDose';
import { canRecord, type DoseState } from './doseState';

type Props = {
  dose: PlannedDose;
  state: DoseState;
  timeZone: string;
  patientId: string;
  /** Mostrar Marcar tomada / Omitir cuando aún no tiene registro (solo las de hoy). */
  recordable: boolean;
  /** Evento registrado: en las de hoy, ya tomadas u omitidas, permite «Deshacer». */
  eventId?: string | null;
};

/**
 * Una dosis en una fila de una lista `rows`: placa del medicamento en su color, nombre y dosis, y la
 * hora a la derecha; debajo, el estado y las acciones visibles (Marcar tomada / Omitir, o Deshacer).
 */
export function DoseRow({ dose, state, timeZone, patientId, recordable, eventId }: Props) {
  const actionable = recordable && canRecord(state);
  const undoable = recordable && (state === 'taken' || state === 'skipped') && eventId;
  // «Más tarde» solo en las de hoy: en los días que vienen, todas lo son.
  const showState = recordable || state !== 'upcoming';
  return (
    <li className="flex flex-col gap-3">
      <div className="flex items-center gap-3">
        <Plate icon="medications" hex={dose.color} />
        <div className="min-w-0 flex-1">
          <p className="text-lg leading-tight font-medium break-words">{dose.medicationName}</p>
          <p className="text-caption text-text-muted">{dose.dosage}</p>
        </div>
        <span className="text-lg font-medium">{formatTime(dose.scheduledAt, timeZone)}</span>
      </div>
      {(showState || actionable || undoable) && (
        <div className="flex flex-wrap items-center gap-2 tablet:pl-14">
          {showState && <DoseStateBadge state={state} />}
          {undoable && (
            <UndoDose patientId={patientId} eventId={eventId} label={dose.medicationName} />
          )}
          {actionable && (
            <DoseActions
              compact
              patientId={patientId}
              scheduleId={dose.scheduleId}
              scheduledAt={dose.scheduledAt}
              label={dose.medicationName}
            />
          )}
        </div>
      )}
    </li>
  );
}
