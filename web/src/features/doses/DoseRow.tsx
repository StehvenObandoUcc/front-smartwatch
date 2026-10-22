import type { PlannedDose } from '../../api/generated';
import { Plate } from '../../components/atoms/Plate/Plate';
import { cn } from '../../lib/cn';
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
 * Una dosis como señal: placa del medicamento en su color, nombre y dosis, y la hora grande a la
 * derecha; debajo, la placa de estado y, si falta registrarla, Tomada / Omitir. La pendiente lleva
 * borde de tinta.
 */
export function DoseRow({ dose, state, timeZone, patientId, recordable }: Props) {
  const time = formatTime(dose.scheduledAt, timeZone);
  const actionable = recordable && canRecord(state);
  return (
    <li
      className={cn(
        'flex flex-col gap-3 rounded-lg border-2 bg-surface-raised p-3',
        actionable ? 'border-border-strong' : 'border-border',
      )}
    >
      <div className="flex items-center gap-3">
        <Plate icon="medications" hex={dose.color} />
        <div className="min-w-0 flex-1">
          <p className="text-lg leading-tight font-extrabold break-words hyphens-auto">
            {dose.medicationName}
          </p>
          <p className="text-text-muted">{dose.dosage}</p>
        </div>
        <span className="shrink-0 text-lg font-extrabold">{time}</span>
      </div>
      {(state !== 'upcoming' || actionable) && (
        <div className="flex flex-wrap items-center gap-2">
          <DoseStateBadge state={state} />
          {actionable && (
            <DoseActions
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
