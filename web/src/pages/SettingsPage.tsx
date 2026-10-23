import { useState } from 'react';
import { useQueryClient } from '@tanstack/react-query';

import {
  getListPatientDevicesQueryKey,
  useConfirmPairingCode,
  useListPatientDevices,
  useUnpairDevice,
} from '../api/generated';
import { Button } from '../components/atoms/Button/Button';
import { Input } from '../components/atoms/Input/Input';
import { Skeleton } from '../components/atoms/Skeleton/Skeleton';
import { Plate } from '../components/atoms/Plate/Plate';
import { Text } from '../components/atoms/Text/Text';
import { ErrorRetry } from '../components/molecules/ErrorRetry';
import { FormField } from '../components/molecules/FormField';
import { AccountSection } from '../features/auth/AccountSection';
import { useAuth } from '../features/auth/AuthContext';
import { CaregiversSection } from '../features/caregivers/CaregiversSection';
import { usePatient } from '../features/patients/PatientGate';
import { errorMessage } from '../lib/errors';

const CODE_PATTERN = /^[BCDFGHJKLMNPQRSTVWXZ]{4}-[BCDFGHJKLMNPQRSTVWXZ]{4}$/;

export function SettingsPage() {
  const { patientId } = usePatient();
  const { auth } = useAuth();
  const queryClient = useQueryClient();
  const [code, setCode] = useState('');
  const devices = useListPatientDevices(patientId);
  const refresh = () =>
    queryClient.invalidateQueries({ queryKey: getListPatientDevicesQueryKey(patientId) });
  const confirm = useConfirmPairingCode({
    mutation: {
      onSuccess: () => {
        setCode('');
        return refresh();
      },
    },
  });
  const unpair = useUnpairDevice({ mutation: { onSuccess: refresh } });
  const normalized = code.trim().toUpperCase();
  const valid = CODE_PATTERN.test(normalized);

  return (
    <div className="flex flex-col gap-6">
      <AccountSection />

      <section aria-labelledby="pair-title" className="flex flex-col gap-4">
        <Text id="pair-title" variant="subtitle" as="h2">
          Vincular reloj
        </Text>
        <form
          className="flex flex-col gap-4"
          onSubmit={(event) => {
            event.preventDefault();
            if (valid) confirm.mutate({ code: normalized, data: { patientId } });
          }}
        >
          <FormField
            label="Código que muestra el reloj"
            hint="Formato XXXX-XXXX"
            error={code && !valid ? 'El código no tiene el formato correcto' : undefined}
          >
            {(p) => (
              <Input
                value={code}
                onChange={(e) => setCode(e.target.value)}
                autoCapitalize="characters"
                autoComplete="off"
                {...p}
              />
            )}
          </FormField>
          {confirm.error && <Text tone="danger">{errorMessage(confirm.error)}</Text>}
          {confirm.isSuccess && <Text tone="success">Reloj vinculado.</Text>}
          <div>
            <Button type="submit" disabled={!valid} loading={confirm.isPending}>
              Vincular
            </Button>
          </div>
        </form>
      </section>

      {auth.status === 'authed' && auth.user.role === 'patient' && <CaregiversSection />}

      <section aria-labelledby="dev-title" className="flex flex-col gap-2">
        <Text id="dev-title" variant="subtitle" as="h2">
          Relojes vinculados
        </Text>
        {devices.isPending && <Skeleton label="Cargando relojes" />}
        {devices.isError && (
          <ErrorRetry
            message={errorMessage(devices.error)}
            onRetry={() => void devices.refetch()}
          />
        )}
        {unpair.error && <Text tone="danger">{errorMessage(unpair.error)}</Text>}
        {devices.data?.items.length === 0 && <Text tone="muted">Ningún reloj vinculado.</Text>}
        <ul className="rows border-y-2 border-border">
          {devices.data?.items.map((device) => (
            <DeviceRow
              key={device.id}
              model={device.model}
              unpairing={unpair.isPending && unpair.variables.deviceId === device.id}
              onUnpair={() => unpair.mutate({ deviceId: device.id })}
            />
          ))}
        </ul>
      </section>
    </div>
  );
}

/**
 * Un reloj vinculado. Desvincular pide confirmación, con «No» primero: sin vínculo el reloj deja de
 * recibir cambios del plan y no sube las tomas.
 */
function DeviceRow({
  model,
  unpairing,
  onUnpair,
}: {
  model: string;
  unpairing: boolean;
  onUnpair: () => void;
}) {
  const [confirming, setConfirming] = useState(false);
  return (
    <li className="flex flex-wrap items-center justify-between gap-3">
      <span className="flex items-center gap-3">
        <Plate icon="watch" size="sm" />
        <Text as="p" className="font-bold">
          {model}
        </Text>
      </span>
      {confirming ? (
        <div
          role="group"
          aria-label={`Desvincular ${model}`}
          className="flex w-full flex-col gap-2"
        >
          <Text className="font-bold">
            ¿Desvincular {model}? Dejará de recibir cambios y no subirá las tomas.
          </Text>
          <div className="flex flex-wrap gap-2">
            <Button variant="secondary" icon="back" onClick={() => setConfirming(false)}>
              No
            </Button>
            <Button variant="danger" icon="link" loading={unpairing} onClick={onUnpair}>
              Sí, desvincular
            </Button>
          </div>
        </div>
      ) : (
        <Button
          variant="secondary"
          icon="link"
          aria-label={`Desvincular ${model}`}
          onClick={() => setConfirming(true)}
        >
          Desvincular
        </Button>
      )}
    </li>
  );
}
