import { useTranslation } from 'react-i18next';
import { useId, useState } from 'react';

import { useGetPatientPlan } from '../../api/generated';
import { Plate } from '../../components/atoms/Plate/Plate';
import { Ring } from '../../components/atoms/Ring/Ring';
import { Skeleton } from '../../components/atoms/Skeleton/Skeleton';
import { Text } from '../../components/atoms/Text/Text';
import { EmptyState } from '../../components/molecules/EmptyState';
import { ErrorRetry } from '../../components/molecules/ErrorRetry';
import { SignBar } from '../../components/molecules/SignBar';
import { cn } from '../../lib/cn';
import { formatTime, localDate } from '../../lib/dates';
import { errorMessage } from '../../lib/errors';
import { useCountUp } from '../../lib/useCountUp';
import { usePatient } from '../patients/PatientGate';
import { DoseActions } from './DoseActions';
import { DoseRow } from './DoseRow';
import { DoseStateBadge } from './DoseStateBadge';
import { canRecord, doseState, eventOf, nextDose, useDoseHistory } from './doseState';
import { FRANJAS, franjaAt, groupByFranja, type Franja } from './franja';

// Cada franja con su pictograma y su tono: sol, tarde y noche.
const franjaSign: Record<
  Franja,
  { tone: 'sun' | 'dusk' | 'night'; icon: 'morning' | 'afternoon' | 'night' }
> = {
  morning: { tone: 'sun', icon: 'morning' },
  afternoon: { tone: 'dusk', icon: 'afternoon' },
  night: { tone: 'night', icon: 'night' },
};

const panelClass: Record<Franja | 'complete', string> = {
  morning: 'panel-morning',
  afternoon: 'panel-afternoon',
  night: 'panel-night',
  complete: 'panel-complete',
};

/** Las dosis de hoy por franja y su estado. La tarjeta principal y las franjas comparten consultas. */
function useToday() {
  const { patientId, timezone } = usePatient();
  const plan = useGetPatientPlan(patientId);
  const history = useDoseHistory(patientId);
  // Momento de referencia fijo por visita: al volver a la pantalla se recalcula.
  const [now] = useState(() => new Date());
  const groups = plan.data && groupByFranja(plan.data.doses, localDate(now, timezone), timezone);
  const doses = groups ? FRANJAS.flatMap((f) => groups[f]) : [];
  const state = (dose: (typeof doses)[number]) => doseState(dose, history.index, now.getTime());
  const event = (dose: (typeof doses)[number]) => eventOf(dose, history.index);
  return { plan, groups, doses, state, event, patientId, timezone, now };
}

/**
 * La tarjeta principal: en 3 segundos, cuántas dosis van y cuál es la próxima. Se pinta con el
 * panel de la franja actual, o el de Completo cuando ya se tomó todo.
 */
export function TodayHero({ className }: { className?: string }) {
  const { plan, doses, state, patientId, timezone, now } = useToday();
  const { t } = useTranslation();
  const titleId = useId();
  const states = doses.map(state);
  const taken = states.filter((s) => s === 'taken').length;
  const shown = useCountUp(taken);
  const next = nextDose(doses, states);
  const franja = franjaAt(now, timezone);
  const complete = doses.length > 0 && taken === doses.length;

  return (
    <section
      aria-labelledby={titleId}
      className={cn(
        'panel flex min-w-0 flex-col gap-6 rounded-lg p-5 shadow-raised tablet:p-8',
        panelClass[complete ? 'complete' : franja],
        className,
      )}
    >
      <header className="flex flex-wrap items-center gap-3">
        <Plate icon={complete ? 'taken' : franjaSign[franja].icon} tone="onSign" size="sm" />
        <h2 id={titleId} className="text-lg font-medium">
          {complete ? t('today.complete') : t(`franja.${franja}`)}
        </h2>
        {!complete && <span className="text-on-panel-muted">{t(`franja.${franja}Range`)}</span>}
      </header>

      {plan.isPending && <Skeleton label={t('today.loading')} rows={2} />}
      {plan.isError && (
        <ErrorRetry message={errorMessage(plan.error)} onRetry={() => void plan.refetch()} />
      )}
      {plan.data && doses.length === 0 && (
        <EmptyState icon="medications" title={t('today.emptyTitle')} body={t('today.emptyBody')} />
      )}
      {doses.length > 0 && (
        <div className="flex flex-1 flex-col items-center gap-6 tablet:flex-row tablet:items-center tablet:gap-10">
          <Ring value={taken} total={doses.length}>
            <span className="text-2xl leading-none font-medium">
              <span aria-hidden="true">{shown}</span>
              <span className="sr-only">{taken}</span> {t('common.of', { total: doses.length })}
            </span>
            <span className="mt-1 text-caption text-on-panel-muted">{t('today.dosesToday')}</span>
          </Ring>
          <div className="flex w-full min-w-0 flex-1 flex-col gap-4">
            {next ? (
              <>
                <p className="flex flex-wrap items-baseline gap-x-3">
                  <span className="text-lg text-on-panel-muted">
                    {canRecord(next.state) ? t('today.dueNow') : t('today.next')}
                  </span>
                  <span className="text-3xl font-medium tracking-display">
                    {formatTime(next.dose.scheduledAt, timezone)}
                  </span>
                </p>
                <div className="flex flex-wrap items-center gap-3 rounded-md bg-surface-veil p-3">
                  <Plate icon="medications" hex={next.dose.color} />
                  <div className="min-w-0 flex-1 basis-32">
                    <p className="text-xl leading-tight font-medium break-words">
                      {next.dose.medicationName}
                    </p>
                    <p className="text-on-panel-muted">{next.dose.dosage}</p>
                  </div>
                  <DoseStateBadge state={next.state} />
                </div>
                {canRecord(next.state) ? (
                  <DoseActions
                    key={`${next.dose.scheduleId}-${next.dose.scheduledAt}`}
                    patientId={patientId}
                    scheduleId={next.dose.scheduleId}
                    scheduledAt={next.dose.scheduledAt}
                    label={next.dose.medicationName}
                  />
                ) : (
                  <p className="text-on-panel-muted">{t('today.watchWillAlert')}</p>
                )}
              </>
            ) : (
              <p className="text-xl font-medium">
                {complete ? t('today.allTaken') : t('today.noneLeft')}
              </p>
            )}
          </div>
        </div>
      )}
    </section>
  );
}

/** Mañana, Tarde y Noche con cada dosis, su estado y sus acciones visibles. */
export function FranjasSection({ className }: { className?: string }) {
  const { plan, groups, doses, state, event, patientId, timezone } = useToday();
  const { t } = useTranslation();
  const titleId = useId();
  return (
    <section aria-labelledby={titleId} className={cn('flex min-w-0 flex-col gap-4', className)}>
      <Text variant="title" id={titleId}>
        {t('today.title')}
      </Text>
      {plan.isPending && <Skeleton label={t('today.loadingFranjas')} rows={3} />}
      {plan.isError && (
        <ErrorRetry message={errorMessage(plan.error)} onRetry={() => void plan.refetch()} />
      )}
      {plan.data && doses.length === 0 && <Text tone="muted">{t('today.noneScheduled')}</Text>}
      {groups && doses.length > 0 && (
        <div className="grid grid-cols-1 gap-4 desktop:grid-cols-3">
          {FRANJAS.map((id) => (
            <section
              key={id}
              aria-labelledby={`franja-${id}`}
              className="raised flex flex-col gap-2 p-4 tablet:p-5"
            >
              <SignBar
                id={`franja-${id}`}
                as="h3"
                title={t(`franja.${id}`)}
                meta={t(`franja.${id}Range`)}
                {...franjaSign[id]}
              />
              {groups[id].length === 0 ? (
                <Text tone="muted" className="py-3">
                  {t('today.nothing')}
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
                      eventId={event(dose)}
                    />
                  ))}
                </ul>
              )}
            </section>
          ))}
        </div>
      )}
    </section>
  );
}
