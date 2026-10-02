import { useState } from 'react';
import { Link } from 'react-router';

import { useGetAdherence, useGetPatientPlan, useListPatientDevices } from '../api/generated';
import { ColorDot } from '../components/atoms/ColorDot/ColorDot';
import { Spinner } from '../components/atoms/Spinner/Spinner';
import { Text } from '../components/atoms/Text/Text';
import { InfoCard } from '../components/molecules/InfoCard';
import { useAuth } from '../features/auth/AuthContext';
import { usePatient } from '../features/patients/PatientGate';
import { percentText } from '../features/reports/ReportView';
import { formatDay, formatTime } from '../lib/dates';

const links = [
  { to: '/medicamentos', label: 'Medicamentos', help: 'Cargar y editar' },
  { to: '/agenda', label: 'Agenda', help: 'Hoy y la semana' },
  { to: '/adherencia', label: 'Adherencia', help: 'Historial' },
  { to: '/reportes', label: 'Reportes', help: 'Semanales y PDF' },
  { to: '/asistente', label: 'Asistente', help: 'Pregunta por tu plan' },
  { to: '/ajustes', label: 'Ajustes', help: 'Reloj, avisos y cuidadores' },
];

function NextDoseCard() {
  const { patientId } = usePatient();
  const plan = useGetPatientPlan(patientId);
  const [now] = useState(() => Date.now());
  const next = plan.data?.doses.find((dose) => new Date(dose.scheduledAt).getTime() >= now);
  const tz = plan.data?.patientTimezone ?? 'UTC';
  return (
    <InfoCard title="Próxima dosis">
      {plan.isPending && <Spinner label="Cargando" />}
      {plan.isError && <Text tone="danger">No se pudo cargar el plan.</Text>}
      {plan.data && !next && <Text tone="muted">Sin dosis en los próximos 7 días.</Text>}
      {next && (
        <>
          <div className="flex items-center gap-2">
            <ColorDot hex={next.color} />
            <Text variant="subtitle" as="p">
              {next.medicationName}
            </Text>
          </div>
          <Text>
            {formatDay(next.scheduledAt, tz)}, {formatTime(next.scheduledAt, tz)} · {next.dosage}
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
    <InfoCard title="Adherencia (7 días)">
      {adherence.isPending && <Spinner label="Cargando" />}
      {adherence.isError && <Text tone="danger">No se pudo cargar.</Text>}
      {adherence.data && (
        <>
          <Text variant="display" as="p">
            {percentText(adherence.data.percentage)}
          </Text>
          <Text tone="muted" variant="caption">
            {adherence.data.taken} tomadas · {adherence.data.missed} no tomadas
          </Text>
        </>
      )}
    </InfoCard>
  );
}

function WatchCard() {
  const { patientId, timezone } = usePatient();
  const devices = useListPatientDevices(patientId);
  const device = devices.data?.items[0];
  return (
    <InfoCard title="Reloj">
      {devices.isPending && <Spinner label="Cargando" />}
      {devices.isError && <Text tone="danger">No se pudo cargar.</Text>}
      {devices.data && !device && (
        <>
          <Text>Aún no hay un reloj vinculado.</Text>
          <Link to="/ajustes" className="min-h-touch underline">
            Vincular reloj
          </Link>
        </>
      )}
      {device && (
        <>
          <Text variant="subtitle" as="p">
            {device.model}
          </Text>
          <Text tone="muted" variant="caption">
            {device.lastSeenAt
              ? `Visto ${formatDay(device.lastSeenAt, timezone)}, ${formatTime(device.lastSeenAt, timezone)}`
              : 'Aún sin conexión'}
          </Text>
        </>
      )}
    </InfoCard>
  );
}

export function HomePage() {
  const { auth } = useAuth();
  const name = auth.status === 'authed' ? auth.user.displayName : '';
  return (
    <div className="flex flex-col gap-6">
      <Text variant="subtitle" as="h2">
        Hola, {name}
      </Text>
      <div className="grid gap-4 tablet:grid-cols-2 desktop:grid-cols-3">
        <NextDoseCard />
        <AdherenceCard />
        <WatchCard />
      </div>
      <nav aria-label="Accesos rápidos">
        <ul className="grid gap-2 tablet:grid-cols-2 desktop:grid-cols-3">
          {links.map((link) => (
            <li key={link.to}>
              <Link
                to={link.to}
                className="flex min-h-touch flex-col justify-center rounded-md border-2 border-border bg-surface p-3 hover:bg-surface-raised"
              >
                <span className="text-body font-semibold">{link.label}</span>
                <span className="text-caption text-text-muted">{link.help}</span>
              </Link>
            </li>
          ))}
        </ul>
      </nav>
    </div>
  );
}
