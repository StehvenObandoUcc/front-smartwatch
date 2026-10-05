import { useQueryClient } from '@tanstack/react-query';

import {
  getListPatientCaregiversQueryKey,
  useCreateCaregiverInvitation,
  useListPatientCaregivers,
  useRevokePatientCaregiver,
} from '../../api/generated';
import { Badge } from '../../components/atoms/Badge/Badge';
import { Button } from '../../components/atoms/Button/Button';
import { Spinner } from '../../components/atoms/Spinner/Spinner';
import { Text } from '../../components/atoms/Text/Text';
import { ErrorRetry } from '../../components/molecules/ErrorRetry';
import { formatDay, formatTime } from '../../lib/dates';
import { errorMessage } from '../../lib/errors';
import { usePatient } from '../patients/PatientGate';

/** Solo para el particular: invita a un familiar o cuidador con un código de un solo uso. */
export function CaregiversSection() {
  const { patientId, timezone } = usePatient();
  const queryClient = useQueryClient();
  const caregivers = useListPatientCaregivers(patientId);
  const refresh = () =>
    queryClient.invalidateQueries({ queryKey: getListPatientCaregiversQueryKey(patientId) });
  const invite = useCreateCaregiverInvitation();
  const revoke = useRevokePatientCaregiver({ mutation: { onSuccess: refresh } });

  return (
    <section aria-labelledby="care-title" className="flex flex-col gap-3">
      <Text id="care-title" variant="subtitle" as="h2">
        Cuidadores
      </Text>
      <Text tone="muted">
        Un cuidador puede ver tus medicamentos, agenda y reportes. Invítalo con un código; él lo
        escribe al entrar como cuidador.
      </Text>
      {caregivers.isPending && <Spinner label="Cargando cuidadores" />}
      {caregivers.isError && (
        <ErrorRetry
          message={errorMessage(caregivers.error)}
          onRetry={() => void caregivers.refetch()}
        />
      )}
      {caregivers.data?.items.length === 0 && <Text tone="muted">Aún no tienes cuidadores.</Text>}
      <ul className="flex flex-col gap-2">
        {caregivers.data?.items.map((link) => (
          <li
            key={link.caregiverId}
            className="flex items-center justify-between gap-2 rounded-md border-2 border-border bg-surface-raised p-4"
          >
            <div className="flex items-center gap-2">
              <Text as="p">{link.caregiverDisplayName}</Text>
              {!link.active && <Badge tone="warning">Revocado</Badge>}
            </div>
            {link.active && (
              <Button
                variant="danger"
                aria-label={`Quitar a ${link.caregiverDisplayName}`}
                onClick={() => revoke.mutate({ patientId, userId: link.caregiverId })}
              >
                Quitar
              </Button>
            )}
          </li>
        ))}
      </ul>
      {invite.data && (
        <div role="status" className="flex flex-col gap-1 rounded-md border-2 border-primary p-4">
          <Text>Comparte este código con tu cuidador:</Text>
          <Text variant="display" as="p">
            {invite.data.code}
          </Text>
          <Text tone="muted" variant="caption">
            Un solo uso. Caduca el {formatDay(invite.data.expiresAt, timezone)} a las{' '}
            {formatTime(invite.data.expiresAt, timezone)}.
          </Text>
        </div>
      )}
      {(invite.error || revoke.error) && (
        <Text tone="danger">{errorMessage(invite.error ?? revoke.error)}</Text>
      )}
      <div>
        <Button
          variant="secondary"
          loading={invite.isPending}
          onClick={() => invite.mutate({ patientId })}
        >
          Invitar cuidador
        </Button>
      </div>
    </section>
  );
}
