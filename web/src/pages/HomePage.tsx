import { Link } from 'react-router';

import {
  useGetAdherence,
  useListMyNotificationChannels,
  useListPatientDevices,
} from '../api/generated';
import { Badge } from '../components/atoms/Badge/Badge';
import { Skeleton } from '../components/atoms/Skeleton/Skeleton';
import { Text } from '../components/atoms/Text/Text';
import { Block } from '../components/molecules/Block';
import { ErrorRetry } from '../components/molecules/ErrorRetry';
import { AdherenceChart } from '../features/doses/AdherenceChart';
import { FranjasBlock, TodayBlock } from '../features/doses/TodayBlocks';
import { usePatient } from '../features/patients/PatientGate';
import { countText, percentText } from '../features/reports/ReportView';
import { formatDay, formatTime } from '../lib/dates';
import { errorMessage } from '../lib/errors';
import { useCountUp } from '../lib/useCountUp';

const linkClass =
  'inline-flex min-h-touch items-center font-bold text-primary underline underline-offset-4';

function WatchBlock({ className }: { className: string }) {
  const { patientId, timezone } = usePatient();
  const devices = useListPatientDevices(patientId);
  const device = devices.data?.items[0];
  return (
    <Block
      icon="watch"
      title="Reloj"
      question="¿Está vinculado y al día?"
      className={className}
      action={
        devices.data &&
        !device && (
          <Link to="/ajustes" className={linkClass}>
            Vincular reloj
          </Link>
        )
      }
    >
      {devices.isPending && <Skeleton label="Cargando" rows={2} />}
      {devices.isError && (
        <ErrorRetry message={errorMessage(devices.error)} onRetry={() => void devices.refetch()} />
      )}
      {devices.data && !device && <Text tone="muted">Aún no hay un reloj vinculado.</Text>}
      {device && (
        <dl className="rows">
          <div className="flex flex-wrap justify-between gap-x-4">
            <dt className="text-text-muted">Modelo</dt>
            <dd className="font-bold">{device.model}</dd>
          </div>
          <div className="flex flex-wrap justify-between gap-x-4">
            <dt className="text-text-muted">Última sincronización</dt>
            <dd className="font-bold">
              {device.lastSeenAt
                ? `${formatDay(device.lastSeenAt, timezone)}, ${formatTime(device.lastSeenAt, timezone)}`
                : 'Aún sin conexión'}
            </dd>
          </div>
          <div className="flex flex-wrap justify-between gap-x-4">
            <dt className="text-text-muted">Batería</dt>
            <dd className="font-bold">
              {device.batteryLevel === null || !device.batteryReportedAt
                ? 'Aún sin dato'
                : `${device.batteryLevel} % · ${formatTime(device.batteryReportedAt, timezone)}`}
            </dd>
          </div>
        </dl>
      )}
    </Block>
  );
}

function AdherenceBlock({ className }: { className: string }) {
  const { patientId } = usePatient();
  const adherence = useGetAdherence(patientId);
  const percentage = adherence.data?.percentage ?? null;
  const shown = useCountUp(percentage ?? 0);
  return (
    <Block
      icon="history"
      title="Adherencia"
      question="¿Cómo fue la semana?"
      className={className}
      action={
        <Link to="/adherencia" className={linkClass}>
          Ver historial
        </Link>
      }
    >
      {adherence.isPending && <Skeleton label="Cargando" rows={1} />}
      {adherence.isError && (
        <ErrorRetry
          message={errorMessage(adherence.error)}
          onRetry={() => void adherence.refetch()}
        />
      )}
      {adherence.data && (
        <p className="flex flex-wrap items-baseline gap-x-3">
          <span className="text-2xl font-extrabold">
            <span aria-hidden="true">{percentText(percentage === null ? null : shown)}</span>
            <span className="sr-only">{percentText(percentage)}</span>
          </span>
          <span className="text-text-muted">
            {countText(adherence.data.taken, 'tomada', 'tomadas')} ·{' '}
            {countText(adherence.data.missed, 'no tomada', 'no tomadas')}
          </span>
        </p>
      )}
      <AdherenceChart />
    </Block>
  );
}

function NotificationsBlock({ className }: { className: string }) {
  const channels = useListMyNotificationChannels();
  const items = channels.data?.items ?? [];
  const telegram = items.find((c) => c.channel === 'telegram');
  const email = items.find((c) => c.channel === 'email');
  return (
    <Block
      icon="notifications"
      title="Avisos"
      question="¿Por dónde te avisamos si falta una dosis?"
      className={className}
      action={
        channels.data && (
          <Link to="/avisos" className={linkClass}>
            {telegram?.linked ? 'Configurar' : 'Conectar Telegram'}
          </Link>
        )
      }
    >
      {channels.isPending && <Skeleton label="Cargando" rows={1} />}
      {channels.isError && (
        <ErrorRetry
          message={errorMessage(channels.error)}
          onRetry={() => void channels.refetch()}
        />
      )}
      {channels.data && (
        <div className="flex flex-wrap gap-2">
          <Badge tone={telegram?.linked ? 'success' : 'outline'} icon="telegram">
            {telegram?.linked ? 'Telegram conectado' : 'Sin Telegram'}
          </Badge>
          {email && (
            <Badge tone={email.verified ? 'success' : 'outline'} icon="email">
              {email.verified ? 'Correo verificado' : 'Correo sin verificar'}
            </Badge>
          )}
        </div>
      )}
    </Block>
  );
}

/**
 * Hoy como bento. En el móvil, en orden de uso: Hoy, Franjas, Avisos, Adherencia y Reloj. En
 * escritorio, 12 columnas: Hoy (7) y Adherencia (5); Franjas (12); Reloj (5) y Avisos (7).
 */
export function HomePage() {
  return (
    <div className="grid grid-cols-1 gap-5 tablet:grid-cols-2 desktop:grid-cols-12">
      <TodayBlock className="tablet:col-span-2 desktop:col-span-7 desktop:row-start-1" />
      <FranjasBlock className="tablet:col-span-2 desktop:col-span-12 desktop:row-start-2" />
      <NotificationsBlock className="desktop:col-span-7 desktop:col-start-6 desktop:row-start-3" />
      <AdherenceBlock className="desktop:col-span-5 desktop:col-start-8 desktop:row-start-1" />
      <WatchBlock className="tablet:col-span-2 desktop:col-span-5 desktop:col-start-1 desktop:row-start-3" />
    </div>
  );
}
