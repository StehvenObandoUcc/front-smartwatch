import { useTranslation } from 'react-i18next';
import { Link } from 'react-router';

import { useGetAdherence, useListDoseHistory } from '../../api/generated';
import { Icon, type IconName } from '../../components/atoms/Icon/Icon';
import { Skeleton } from '../../components/atoms/Skeleton/Skeleton';
import { Text } from '../../components/atoms/Text/Text';
import { Block } from '../../components/molecules/Block';
import { ErrorRetry } from '../../components/molecules/ErrorRetry';
import { cn } from '../../lib/cn';
import { i18n } from '../../i18n';
import { daysAgo } from '../../lib/dates';
import { errorMessage } from '../../lib/errors';
import { percentText } from '../reports/ReportView';
import { usePatient } from '../patients/PatientGate';
import { dailyAdherence, type DayAdherence } from './dailyAdherence';

const DAYS = 7;

// El día llega como YYYY-MM-DD local del paciente; a mediodía UTC no cambia de fecha al formatear.
function weekday(day: string, style: 'short' | 'long') {
  return new Date(`${day}T12:00:00Z`).toLocaleDateString(i18n.language, {
    timeZone: 'UTC',
    weekday: style,
    ...(style === 'long' && { day: 'numeric', month: 'long' }),
  });
}

function dayText(d: DayAdherence) {
  if (d.total === 0) return i18n.t('week.noDoses');
  return i18n.t('week.dayCount', { taken: d.taken, total: d.total });
}

// Cada día es un punto con icono además del color: todas tomadas, alguna sin tomar o sin dosis.
function dayDot(d: DayAdherence): { icon: IconName; className: string } {
  if (d.total === 0) return { icon: 'skipped', className: 'border border-border text-text-muted' };
  if (d.taken === d.total) return { icon: 'taken', className: 'bg-state-taken text-on-state' };
  return { icon: 'missed', className: 'bg-state-missed text-on-state' };
}

/** La semana: el porcentaje de dosis tomadas y un punto por día. */
export function WeekCard({ className }: { className?: string }) {
  const { t } = useTranslation();
  const { patientId, timezone } = usePatient();
  const adherence = useGetAdherence(patientId);
  const days = Array.from({ length: DAYS }, (_, i) => daysAgo(DAYS - 1 - i, timezone));
  const history = useListDoseHistory(patientId, { from: days[0], to: days[DAYS - 1], limit: 100 });
  const pending = adherence.isPending || history.isPending;
  const error = adherence.error ?? history.error;
  return (
    <Block
      icon="history"
      title={t('week.title')}
      question={t('week.question')}
      className={className}
      action={
        <Link
          to="/adherencia"
          className="inline-flex min-h-touch items-center font-medium text-accent underline"
        >
          {t('week.viewHistory')}
        </Link>
      }
    >
      {pending && <Skeleton label={t('week.loading')} rows={1} />}
      {!pending && error && (
        <ErrorRetry
          message={errorMessage(error)}
          onRetry={() => {
            void adherence.refetch();
            void history.refetch();
          }}
        />
      )}
      {adherence.data && history.data && (
        <>
          <p className="flex flex-wrap items-baseline gap-x-3">
            <span className="text-3xl font-medium tracking-display">
              {percentText(adherence.data.percentage)}
            </span>
            {adherence.data.percentage !== null && (
              <span className="text-text-muted">{t('week.taken')}</span>
            )}
          </p>
          <ol className="grid grid-cols-7 gap-1">
            {dailyAdherence(history.data.items, days, timezone).map((d) => {
              const dot = dayDot(d);
              return (
                <li key={d.day} className="flex flex-col items-center gap-1">
                  <span
                    aria-hidden="true"
                    className={cn('grid size-9 place-items-center rounded-full', dot.className)}
                  >
                    <Icon name={dot.icon} className="size-5" />
                  </span>
                  <span aria-hidden="true" className="text-label text-text-muted">
                    {weekday(d.day, 'short').replace('.', '')}
                  </span>
                  <span className="sr-only">
                    {weekday(d.day, 'long')}: {dayText(d)}
                  </span>
                </li>
              );
            })}
          </ol>
          {history.data.items.length === 0 && <Text tone="muted">{t('week.noneYet')}</Text>}
        </>
      )}
    </Block>
  );
}
