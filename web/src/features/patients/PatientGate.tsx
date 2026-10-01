import { createContext, use, useState, type ReactNode } from 'react';
import { useQueryClient } from '@tanstack/react-query';

import {
  getListPatientsQueryKey,
  useCreatePatient,
  useGetPatient,
  useListPatientConsents,
  useListPatients,
  useSetPatientConsent,
} from '../../api/generated';
import { Button } from '../../components/atoms/Button/Button';
import { Input } from '../../components/atoms/Input/Input';
import { Spinner } from '../../components/atoms/Spinner/Spinner';
import { Text } from '../../components/atoms/Text/Text';
import { FormField } from '../../components/molecules/FormField';
import { errorMessage } from '../../lib/errors';
import { useAuth } from '../auth/AuthContext';

const CONSENT_VERSION = '1';

const PatientContext = createContext<{ patientId: string; timezone: string } | null>(null);

export function usePatient() {
  const value = use(PatientContext);
  if (!value) throw new Error('usePatient fuera de PatientGate');
  return value;
}

function ConsentCard({ patientId }: { patientId: string }) {
  const queryClient = useQueryClient();
  const setConsent = useSetPatientConsent({
    mutation: { onSuccess: () => queryClient.invalidateQueries() },
  });
  return (
    <section className="flex flex-col gap-4" aria-labelledby="consent-title">
      <Text id="consent-title" variant="title" as="h2">
        Datos de salud
      </Text>
      <Text>
        Para guardar medicamentos y horarios necesitamos permiso para tratar datos de salud. Solo se
        usan para generar los recordatorios del reloj y los reportes.
      </Text>
      {setConsent.error && <Text tone="danger">{errorMessage(setConsent.error)}</Text>}
      <div>
        <Button
          loading={setConsent.isPending}
          onClick={() =>
            setConsent.mutate({
              patientId,
              purpose: 'health_data',
              data: { granted: true, version: CONSENT_VERSION },
            })
          }
        >
          Acepto el tratamiento de datos de salud
        </Button>
      </div>
    </section>
  );
}

function NewPatientForm() {
  const queryClient = useQueryClient();
  const [name, setName] = useState('');
  const create = useCreatePatient({
    mutation: {
      onSuccess: () => queryClient.invalidateQueries({ queryKey: getListPatientsQueryKey() }),
    },
  });
  const timezone = Intl.DateTimeFormat().resolvedOptions().timeZone;
  return (
    <form
      className="flex flex-col gap-4"
      onSubmit={(event) => {
        event.preventDefault();
        if (name.trim()) create.mutate({ data: { displayName: name.trim(), timezone } });
      }}
    >
      <Text variant="title" as="h2">
        Añade a tu primer paciente
      </Text>
      <FormField label="Nombre del paciente">
        {(p) => <Input value={name} onChange={(e) => setName(e.target.value)} {...p} />}
      </FormField>
      {create.error && <Text tone="danger">{errorMessage(create.error)}</Text>}
      <div>
        <Button type="submit" loading={create.isPending}>
          Crear paciente
        </Button>
      </div>
    </form>
  );
}

/** Resuelve el paciente activo (el propio o el primero del cuidador) y exige el consentimiento health_data. */
export function PatientGate({ children }: { children: ReactNode }) {
  const { auth } = useAuth();
  const ownId = auth.status === 'authed' ? auth.user.patientId : null;
  const patients = useListPatients(undefined, { query: { enabled: !ownId } });
  const patientId = ownId ?? patients.data?.items[0]?.id ?? '';
  const patient = useGetPatient(patientId, { query: { enabled: Boolean(patientId) } });
  const consents = useListPatientConsents(patientId, { query: { enabled: Boolean(patientId) } });

  if (!ownId && patients.isPending) return <Spinner label="Cargando pacientes" />;
  if (!patientId) return <NewPatientForm />;
  if (patient.isPending || consents.isPending) return <Spinner label="Cargando datos" />;
  const granted = consents.data?.items.some((c) => c.purpose === 'health_data' && c.granted);
  if (!granted) return <ConsentCard patientId={patientId} />;
  const timezone = patient.data?.timezone ?? 'UTC';
  return <PatientContext value={{ patientId, timezone }}>{children}</PatientContext>;
}
