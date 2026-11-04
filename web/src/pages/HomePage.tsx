import { useTranslation } from 'react-i18next';
import { useState } from 'react';
import { Link } from 'react-router';

import { useListPatientDevices } from '../api/generated';
import { Icon } from '../components/atoms/Icon/Icon';
import { Skeleton } from '../components/atoms/Skeleton/Skeleton';
import { Text } from '../components/atoms/Text/Text';
import { Block } from '../components/molecules/Block';
import { ErrorRetry } from '../components/molecules/ErrorRetry';
import { timeAgo, watchAlert } from '../features/devices/watchStatus';
import { FranjasSection, TodayHero } from '../features/doses/TodayBlocks';
import { WeekCard } from '../features/doses/WeekCard';
import { usePatient } from '../features/patients/PatientGate';
import { formatTime } from '../lib/dates';
import { errorMessage } from '../lib/errors';

const linkClass =
  'inline-flex min-h-touch items-center font-medium text-accent underline underline-offset-4';

function WatchCard({ className }: { className?: string }) {
  const { t } = useTranslation();
  const { patientId, timezone } = usePatient();
  const devices = useListPatientDevices(patientId);
  const [now] = useState(() => Date.now());
  const device = devices.data?.items[0];
  const alert = device ? watchAlert(device, now) : null;
  const lowBattery = device?.batteryLevel != null && device.batteryLevel < 20;
  return (
    <Block
      icon="watch"
      title={t('watchCard.title')}
      question={t('watchCard.question')}
      className={className}
      action={
        devices.data &&
        !device && (
          <Link to="/ajustes" className={linkClass}>
            {t('watchCard.pair')}
          </Link>
        )
      }
    >
      {devices.isPending && <Skeleton label={t('watchCard.loading')} rows={1} />}
      {devices.isError && (
        <ErrorRetry message={errorMessage(devices.error)} onRetry={() => void devices.refetch()} />
      )}
      {devices.data && !device && <Text tone="muted">{t('watchCard.none')}</Text>}
      {device && (
        <>
          {alert && (
            <p
              role="status"
              className="flex items-start gap-2 rounded-md border border-warning bg-warning/5 p-3"
            >
              <Icon
                name={alert === 'battery' ? 'batteryLow' : 'offline'}
                className="text-warning"
              />
              {t(`watchCard.${alert}`)}
            </p>
          )}
          <dl className="grid grid-cols-2 gap-4">
            <div className="flex flex-col gap-1">
              <dt className="text-caption text-text-muted">{t('watchCard.batteryLabel')}</dt>
              <dd className="flex items-center gap-2 text-2xl font-medium">
                <Icon
                  name={lowBattery ? 'batteryLow' : 'battery'}
                  className={lowBattery ? 'text-warning' : 'text-text-muted'}
                />
                {device.batteryLevel === null ? (
                  <span className="text-body font-normal text-text-muted">
                    {t('watchCard.noData')}
                  </span>
                ) : (
                  t('common.percent', { value: device.batteryLevel })
                )}
              </dd>
            </div>
            <div className="flex flex-col gap-1">
              <dt className="text-caption text-text-muted">{t('watchCard.lastSeen')}</dt>
              <dd className="flex flex-col">
                {device.lastSeenAt ? (
                  <>
                    <span className="text-lg font-medium first-letter:uppercase">
                      {timeAgo(device.lastSeenAt, now)}
                    </span>
                    <span className="text-caption text-text-muted">
                      {t('watchCard.at', { time: formatTime(device.lastSeenAt, timezone) })}
                    </span>
                  </>
                ) : (
                  <span className="text-text-muted">{t('watchCard.neverSeen')}</span>
                )}
              </dd>
            </div>
          </dl>
          <p className="text-caption text-text-muted">{device.model}</p>
        </>
      )}
    </Block>
  );
}

/**
 * Hoy. Móvil: tarjeta principal, reloj, franjas y semana. Escritorio: la tarjeta principal (8
 * columnas) con el reloj y la semana al lado (4) y las tres franjas debajo, a todo el ancho.
 */
export function HomePage() {
  return (
    <div className="grid grid-cols-1 gap-4 tablet:gap-6 desktop:grid-cols-12">
      <TodayHero className="desktop:col-span-8 desktop:row-span-2" />
      <WatchCard className="desktop:col-span-4" />
      <WeekCard className="order-last desktop:order-none desktop:col-span-4" />
      <FranjasSection className="desktop:col-span-12" />
    </div>
  );
}
