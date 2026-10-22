import { Link } from 'react-router';

import {
  useGetAdherence,
  useListMyNotificationChannels,
  useListPatientDevices,
} from '../api/generated';
import { Badge } from '../components/atoms/Badge/Badge';
import { Skeleton } from '../components/atoms/Skeleton/Skeleton';
import { Text } from '../components/atoms/Text/Text';
import { ErrorRetry } from '../components/molecules/ErrorRetry';
import { SummaryRow } from '../components/molecules/SummaryRow';
import { TodayPillbox } from '../features/doses/TodayPillbox';
import { usePatient } from '../features/patients/PatientGate';
import { countText, percentText } from '../features/reports/ReportView';
import { formatDay, formatTime } from '../lib/dates';
import { errorMessage } from '../lib/errors';

const linkClass =
  'inline-flex min-h-touch items-center font-bold text-primary underline underline-offset-4';

function WatchRow() {
  const { patientId, timezone } = usePatient();
  const devices = useListPatientDevices(patientId);
  const device = devices.data?.items[0];
  return (
    <SummaryRow
      title="Reloj"
      icon="watch"
      action={
        devices.data &&
        !device && (
          <Link to="/ajustes" className={linkClass}>
            Vincular reloj
          </Link>
        )
      }
    >
      {devices.isPending && <Skeleton label="Cargando" rows={1} />}
      {devices.isError && (
        <ErrorRetry message={errorMessage(devices.error)} onRetry={() => void devices.refetch()} />
      )}
      {devices.data && !device && <Text tone="muted">Aún no hay un reloj vinculado.</Text>}
      {device && (
        <Text tone="muted">
          {device.model} ·{' '}
          {device.lastSeenAt
            ? `conectado ${formatDay(device.lastSeenAt, timezone)}, ${formatTime(device.lastSeenAt, timezone)}`
            : 'aún sin conexión'}
        </Text>
      )}
    </SummaryRow>
  );
}

function AdherenceRow() {
  const { patientId } = usePatient();
  const adherence = useGetAdherence(patientId);
  return (
    <SummaryRow
      title="Últimos 7 días"
      icon="history"
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
        <Text tone="muted">
          <span className="font-extrabold text-text">{percentText(adherence.data.percentage)}</span>{' '}
          · {countText(adherence.data.taken, 'tomada', 'tomadas')} ·{' '}
          {countText(adherence.data.missed, 'no tomada', 'no tomadas')}
        </Text>
      )}
    </SummaryRow>
  );
}

function NotificationsRow() {
  const channels = useListMyNotificationChannels();
  const items = channels.data?.items ?? [];
  const telegram = items.find((c) => c.channel === 'telegram');
  const email = items.find((c) => c.channel === 'email');
  return (
    <SummaryRow
      title="Avisos"
      icon="notifications"
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
    </SummaryRow>
  );
}

/** Hoy: el pastillero manda; debajo, una línea para el reloj, la semana y los avisos. */
export function HomePage() {
  return (
    <div className="flex flex-col gap-8">
      <TodayPillbox />
      <ul aria-label="Resumen" className="rows border-y-2 border-border">
        <WatchRow />
        <AdherenceRow />
        <NotificationsRow />
      </ul>
    </div>
  );
}
