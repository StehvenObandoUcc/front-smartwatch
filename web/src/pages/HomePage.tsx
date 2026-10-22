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
import { InfoCard } from '../components/molecules/InfoCard';
import { AdherenceChart } from '../features/doses/AdherenceChart';
import { TodayPillbox } from '../features/doses/TodayPillbox';
import { usePatient } from '../features/patients/PatientGate';
import { percentText } from '../features/reports/ReportView';
import { formatDay, formatTime } from '../lib/dates';
import { errorMessage } from '../lib/errors';

const linkClass =
  'inline-flex min-h-touch items-center font-bold text-primary underline underline-offset-4';

function WatchCard() {
  const { patientId, timezone } = usePatient();
  const devices = useListPatientDevices(patientId);
  const device = devices.data?.items[0];
  return (
    <InfoCard title="Reloj" icon="watch">
      {devices.isPending && <Skeleton label="Cargando" rows={1} />}
      {devices.isError && (
        <ErrorRetry message={errorMessage(devices.error)} onRetry={() => void devices.refetch()} />
      )}
      {devices.data && !device && (
        <>
          <Text>Aún no hay un reloj vinculado.</Text>
          <Link to="/ajustes" className={linkClass}>
            Vincular reloj
          </Link>
        </>
      )}
      {device && (
        <>
          <Text variant="subtitle" as="p">
            {device.model}
          </Text>
          <Text tone="muted">
            {device.lastSeenAt
              ? `Conectado ${formatDay(device.lastSeenAt, timezone)}, ${formatTime(device.lastSeenAt, timezone)}`
              : 'Aún sin conexión'}
          </Text>
        </>
      )}
    </InfoCard>
  );
}

function AdherenceCard() {
  const { patientId } = usePatient();
  const adherence = useGetAdherence(patientId);
  return (
    <InfoCard title="Últimos 7 días" icon="history">
      {adherence.isPending && <Skeleton label="Cargando" rows={1} />}
      {adherence.isError && (
        <ErrorRetry
          message={errorMessage(adherence.error)}
          onRetry={() => void adherence.refetch()}
        />
      )}
      {adherence.data && (
        <>
          <Text variant="display" as="p">
            {percentText(adherence.data.percentage)}
          </Text>
          <Text tone="muted">
            {adherence.data.taken} tomadas · {adherence.data.missed} no tomadas
          </Text>
        </>
      )}
    </InfoCard>
  );
}

function NotificationsCard() {
  const channels = useListMyNotificationChannels();
  const items = channels.data?.items ?? [];
  const telegram = items.find((c) => c.channel === 'telegram');
  const email = items.find((c) => c.channel === 'email');
  return (
    <InfoCard title="Avisos" icon="notifications">
      {channels.isPending && <Skeleton label="Cargando" rows={1} />}
      {channels.isError && (
        <ErrorRetry
          message={errorMessage(channels.error)}
          onRetry={() => void channels.refetch()}
        />
      )}
      {channels.data && (
        <>
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
          <Link to="/avisos" className={linkClass}>
            {telegram?.linked ? 'Configurar avisos' : 'Conectar Telegram'}
          </Link>
        </>
      )}
    </InfoCard>
  );
}

/** Hoy: el pastillero manda; debajo, el estado del reloj, la semana y los avisos. */
export function HomePage() {
  return (
    <div className="flex flex-col gap-8">
      <TodayPillbox />
      <div className="grid grid-cols-1 gap-4 tablet:grid-cols-3">
        <WatchCard />
        <AdherenceCard />
        <NotificationsCard />
        <InfoCard title="Dosis tomadas por día" icon="history" className="tablet:col-span-3">
          <AdherenceChart />
        </InfoCard>
      </div>
    </div>
  );
}
