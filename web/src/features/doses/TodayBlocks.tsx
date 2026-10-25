import { useState } from 'react';

import { useGetPatientPlan } from '../../api/generated';
import { Plate } from '../../components/atoms/Plate/Plate';
import { Ring } from '../../components/atoms/Ring/Ring';
import { Skeleton } from '../../components/atoms/Skeleton/Skeleton';
import { Text } from '../../components/atoms/Text/Text';
import { Block } from '../../components/molecules/Block';
import { EmptyState } from '../../components/molecules/EmptyState';
import { ErrorRetry } from '../../components/molecules/ErrorRetry';
import { SignBar } from '../../components/molecules/SignBar';
import { formatTime, localDate } from '../../lib/dates';
import { errorMessage } from '../../lib/errors';
import { useCountUp } from '../../lib/useCountUp';
import { usePatient } from '../patients/PatientGate';
import { DoseActions } from './DoseActions';
import { DoseRow } from './DoseRow';
import { DoseStateBadge } from './DoseStateBadge';
import { canRecord, doseState, nextDose, useDoseHistory } from './doseState';
import { FRANJAS, groupByFranja, type Franja } from './franja';

// Cada franja es una señal de su color, con su pictograma: sol, tarde y noche.
const franjaSign: Record<
  Franja,
  { tone: 'sun' | 'dusk' | 'night'; icon: 'morning' | 'afternoon' | 'night' }
> = {
  morning: { tone: 'sun', icon: 'morning' },
  afternoon: { tone: 'dusk', icon: 'afternoon' },
  night: { tone: 'night', icon: 'night' },
};

/** Las dosis de hoy por franja y su estado. Hoy y Franjas comparten las mismas consultas. */
function useToday() {
  const { patientId, timezone } = usePatient();
  const plan = useGetPatientPlan(patientId);
  const history = useDoseHistory(patientId);
  // Momento de referencia fijo por visita: al volver a la pantalla se recalcula.
  const [now] = useState(() => new Date());
  const groups = plan.data && groupByFranja(plan.data.doses, localDate(now, timezone), timezone);
  const doses = groups ? FRANJAS.flatMap((f) => groups[f.id]) : [];
  const state = (dose: (typeof doses)[number]) => doseState(dose, history.index, now.getTime());
  return { plan, groups, doses, state, patientId, timezone };
}

/** Hoy: el anillo con cuántas van tomadas, la dosis que toca y su Tomada. */
export function TodayBlock({ className }: { className?: string }) {
  const { plan, doses, state, patientId, timezone } = useToday();
  const states = doses.map(state);
  const taken = states.filter((s) => s === 'taken').length;
  const shown = useCountUp(taken);
  const next = nextDose(doses, states);

  return (
    <Block icon="today" title="Hoy" question="¿Se tomó hoy su medicamento?" className={className}>
      {plan.isPending && <Skeleton label="Cargando tu día" rows={3} />}
      {plan.isError && (
        <ErrorRetry message={errorMessage(plan.error)} onRetry={() => void plan.refetch()} />
      )}
      {plan.data && doses.length === 0 && (
        <EmptyState
          icon="medications"
          title="Hoy no hay dosis"
          body="Las dosis aparecen aquí cuando agregas un medicamento con su horario."
        />
      )}
      {doses.length > 0 && (
        <div className="flex flex-col items-center gap-5 tablet:flex-row tablet:items-center">
          <Ring value={taken} total={doses.length}>
            <span aria-hidden="true" className="text-2xl font-extrabold">
              {shown}
            </span>
            <span className="sr-only">{taken}</span>
            <span className="text-text-muted">de {doses.length} tomadas</span>
          </Ring>
          <div className="flex w-full min-w-0 flex-1 flex-col gap-3">
            {next ? (
              <>
                <Text tone="muted">{canRecord(next.state) ? 'Toca ahora' : 'Próxima dosis'}</Text>
                <div className="flex flex-wrap items-center gap-x-3 gap-y-2">
                  <Plate icon="medications" hex={next.dose.color} />
                  <div className="min-w-0 flex-1 basis-32">
                    <p className="text-lg leading-tight font-extrabold break-words">
                      {next.dose.medicationName}
                    </p>
                    <p className="text-text-muted">
                      {next.dose.dosage} · {formatTime(next.dose.scheduledAt, timezone)}
                    </p>
                  </div>
                  <DoseStateBadge state={next.state} />
                </div>
                {canRecord(next.state) && (
                  <DoseActions
                    key={`${next.dose.scheduleId}-${next.dose.scheduledAt}`}
                    patientId={patientId}
                    scheduleId={next.dose.scheduleId}
                    scheduledAt={next.dose.scheduledAt}
                    label={next.dose.medicationName}
                  />
                )}
              </>
            ) : (
              <Text variant="subtitle" as="p">
                {taken === doses.length ? 'Todas tomadas hoy' : 'Ya no quedan dosis hoy'}
              </Text>
            )}
          </div>
        </div>
      )}
    </Block>
  );
}

/** Franjas: Mañana, Tarde y Noche con cada dosis y su estado; las pendientes se marcan con «Marcar». */
export function FranjasBlock({ className }: { className?: string }) {
  const { plan, groups, doses, state, patientId, timezone } = useToday();
  return (
    <Block
      icon="agenda"
      title="Franjas"
      question="¿Qué toca en cada momento del día?"
      className={className}
    >
      {plan.isPending && <Skeleton label="Cargando franjas" rows={3} />}
      {plan.isError && (
        <ErrorRetry message={errorMessage(plan.error)} onRetry={() => void plan.refetch()} />
      )}
      {plan.data && doses.length === 0 && <Text tone="muted">Hoy no hay dosis programadas.</Text>}
      {groups && doses.length > 0 && (
        <div className="grid grid-cols-1 gap-5 desktop:grid-cols-3">
          {FRANJAS.map(({ id, label, range }) => (
            <section key={id} aria-labelledby={`franja-${id}`} className="flex flex-col gap-3">
              <SignBar id={`franja-${id}`} as="h3" title={label} meta={range} {...franjaSign[id]} />
              {groups[id].length === 0 ? (
                <Text tone="muted" className="px-1">
                  Nada programado
                </Text>
              ) : (
                <ul className="rows">
                  {groups[id].map((dose) => (
                    <DoseRow
                      key={`${dose.scheduleId}-${dose.scheduledAt}`}
                      dose={dose}
                      state={state(dose)}
                      timeZone={timezone}
                      patientId={patientId}
                      recordable
                      disclose
                    />
                  ))}
                </ul>
              )}
            </section>
          ))}
        </div>
      )}
    </Block>
  );
}
