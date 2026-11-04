import { useTranslation } from 'react-i18next';
import { createContext, use, useState, type ReactNode } from 'react';
import { useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router';

import {
  getListPatientsQueryKey,
  useCreatePatient,
  useGetPatient,
  useListPatientConsents,
  useListPatients,
  useSetPatientConsent,
  type Patient,
} from '../../api/generated';
import { Button } from '../../components/atoms/Button/Button';
import { Input } from '../../components/atoms/Input/Input';
import { Spinner } from '../../components/atoms/Spinner/Spinner';
import { Text } from '../../components/atoms/Text/Text';
import { FormField } from '../../components/molecules/FormField';
import { ErrorRetry } from '../../components/molecules/ErrorRetry';
import { errorMessage } from '../../lib/errors';
import { useAuth } from '../auth/AuthContext';
import { AcceptInvitationForm } from '../caregivers/AcceptInvitationForm';

const CONSENT_VERSION = '1';

const PatientContext = createContext<{ patientId: string; timezone: string } | null>(null);

export function usePatient() {
  const value = use(PatientContext);
  if (!value) throw new Error('usePatient fuera de PatientGate');
  return value;
}

function ConsentCard({ patientId, name }: { patientId: string; name: string }) {
  const { t } = useTranslation();
  const queryClient = useQueryClient();
  const setConsent = useSetPatientConsent({
    mutation: { onSuccess: () => queryClient.invalidateQueries() },
  });
  return (
    <section className="flex flex-col gap-4" aria-labelledby="consent-title">
      <Text id="consent-title" variant="title" as="h2">
        {t('patients.consentTitle', { name })}
      </Text>
      <Text>{t('patients.consentBody')}</Text>
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
          {t('patients.consentAccept')}
        </Button>
      </div>
    </section>
  );
}

function NewPersonForm({
  first,
  onCreated,
  onCancel,
}: {
  first: boolean;
  onCreated: (patient: Patient) => void;
  onCancel?: () => void;
}) {
  const { t } = useTranslation();
  const queryClient = useQueryClient();
  const [name, setName] = useState('');
  const create = useCreatePatient({
    mutation: {
      onSuccess: async (patient) => {
        await queryClient.invalidateQueries({ queryKey: getListPatientsQueryKey() });
        onCreated(patient);
      },
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
        {first ? t('patients.firstPerson') : t('patients.addPersonTitle')}
      </Text>
      <Text tone="muted">{t('patients.newPersonHelp')}</Text>
      <FormField label={t('patients.personName')}>
        {(p) => <Input value={name} onChange={(e) => setName(e.target.value)} {...p} />}
      </FormField>
      {create.error && <Text tone="danger">{errorMessage(create.error)}</Text>}
      <div className="flex flex-wrap gap-2">
        <Button type="submit" loading={create.isPending}>
          {t('patients.create')}
        </Button>
        {onCancel && (
          <Button variant="ghost" onClick={onCancel}>
            {t('common.cancel')}
          </Button>
        )}
      </div>
    </form>
  );
}

/** Barra del cuidador: a quién está gestionando y cómo cambiar o añadir personas. */
function CaregiverBar({
  patients,
  activeId,
  onSelect,
  onAdd,
}: {
  patients: Patient[];
  activeId: string;
  onSelect: (id: string) => void;
  onAdd: () => void;
}) {
  const { t } = useTranslation();
  return (
    <div className="mb-4 flex flex-wrap items-end gap-2 well p-3">
      <label className="flex min-w-0 flex-1 flex-col gap-1 text-caption font-bold">
        {t('patients.managing')}
        <select
          value={activeId}
          onChange={(event) => onSelect(event.target.value)}
          className="min-h-touch w-full card px-3 text-body font-normal"
        >
          {patients.map((patient) => (
            <option key={patient.id} value={patient.id}>
              {patient.displayName}
            </option>
          ))}
        </select>
      </label>
      <Button variant="secondary" onClick={onAdd}>
        {t('patients.addPerson')}
      </Button>
    </div>
  );
}

/**
 * Resuelve la persona activa y exige su consentimiento health_data.
 * Particular: es él mismo. Cuidador: elige entre las personas a su cargo.
 */
export function PatientGate({ children }: { children: ReactNode }) {
  const { t } = useTranslation();
  const { auth } = useAuth();
  const navigate = useNavigate();
  const isCaregiver = auth.status === 'authed' && auth.user.role === 'caregiver';
  const ownId = auth.status === 'authed' ? auth.user.patientId : null;
  const [selected, setSelected] = useState<string | null>(null);
  const [adding, setAdding] = useState(false);

  const list = useListPatients(undefined, { query: { enabled: isCaregiver } });
  const patients = list.data?.items ?? [];
  const patientId = ownId ?? selected ?? patients[0]?.id ?? '';
  const patient = useGetPatient(patientId, { query: { enabled: Boolean(patientId) } });
  const consents = useListPatientConsents(patientId, { query: { enabled: Boolean(patientId) } });

  function created(newPatient: Patient) {
    setSelected(newPatient.id);
    setAdding(false);
    navigate('/ajustes');
  }

  function joined(id: string) {
    setSelected(id);
    setAdding(false);
    navigate('/');
  }

  if (isCaregiver && list.isError) {
    return <ErrorRetry message={errorMessage(list.error)} onRetry={() => void list.refetch()} />;
  }
  if (isCaregiver && list.isPending) return <Spinner label={t('patients.loadingPeople')} />;
  if (isCaregiver && (patients.length === 0 || adding)) {
    return (
      <div className="flex flex-col gap-8">
        <NewPersonForm
          first={patients.length === 0}
          onCreated={created}
          onCancel={patients.length > 0 ? () => setAdding(false) : undefined}
        />
        <AcceptInvitationForm onJoined={joined} />
      </div>
    );
  }
  if (!patientId) return <Spinner label={t('patients.loadingData')} />;

  let content: ReactNode;
  if (patient.isError || consents.isError) {
    content = (
      <ErrorRetry
        message={errorMessage(patient.error ?? consents.error)}
        onRetry={() => {
          void patient.refetch();
          void consents.refetch();
        }}
      />
    );
  } else if (patient.isPending || consents.isPending) {
    content = <Spinner label={t('patients.loadingData')} />;
  } else if (!consents.data?.items.some((c) => c.purpose === 'health_data' && c.granted)) {
    content = (
      <ConsentCard
        patientId={patientId}
        name={patient.data?.displayName ?? t('patients.thePerson')}
      />
    );
  } else {
    content = (
      <PatientContext value={{ patientId, timezone: patient.data?.timezone ?? 'UTC' }}>
        {children}
      </PatientContext>
    );
  }

  return (
    <>
      {isCaregiver && (
        <CaregiverBar
          patients={patients}
          activeId={patientId}
          onSelect={setSelected}
          onAdd={() => setAdding(true)}
        />
      )}
      {content}
    </>
  );
}
