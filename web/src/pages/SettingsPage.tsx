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
import { Text } from '../components/atoms/Text/Text';
import { FormField } from '../components/molecules/FormField';
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
        {devices.data?.items.length === 0 && <Text tone="muted">Ningún reloj vinculado.</Text>}
        <ul className="flex flex-col gap-2">
          {devices.data?.items.map((device) => (
            <li
              key={device.id}
              className="flex items-center justify-between gap-4 rounded-md border-2 border-border bg-surface-raised p-4"
            >
              <Text as="p">{device.model}</Text>
              <Button
                variant="danger"
                aria-label={`Desvincular ${device.model}`}
                onClick={() => unpair.mutate({ deviceId: device.id })}
              >
                Desvincular
              </Button>
            </li>
          ))}
        </ul>
      </section>
    </div>
  );
}
