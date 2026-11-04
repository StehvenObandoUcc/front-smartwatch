import { useTranslation } from 'react-i18next';
import { useQueryClient } from '@tanstack/react-query';

import {
  getListPatientCaregiversQueryKey,
  useCreateCaregiverInvitation,
  useListPatientCaregivers,
  useRevokePatientCaregiver,
} from '../../api/generated';
import { Badge } from '../../components/atoms/Badge/Badge';
import { Button } from '../../components/atoms/Button/Button';
import { Skeleton } from '../../components/atoms/Skeleton/Skeleton';
import { Text } from '../../components/atoms/Text/Text';
import { ErrorRetry } from '../../components/molecules/ErrorRetry';
import { formatDay, formatTime } from '../../lib/dates';
import { errorMessage } from '../../lib/errors';
import { usePatient } from '../patients/PatientGate';

/** Solo para el particular: invita a un familiar o cuidador con un código de un solo uso. */
export function CaregiversSection() {
  const { t } = useTranslation();
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
        {t('caregivers.title')}
      </Text>
      <Text tone="muted">{t('caregivers.intro')}</Text>
      {caregivers.isPending && <Skeleton label={t('caregivers.loading')} />}
      {caregivers.isError && (
        <ErrorRetry
          message={errorMessage(caregivers.error)}
          onRetry={() => void caregivers.refetch()}
        />
      )}
      {caregivers.data?.items.length === 0 && <Text tone="muted">{t('caregivers.none')}</Text>}
      <ul className="flex flex-col gap-2">
        {caregivers.data?.items.map((link) => (
          <li key={link.caregiverId} className="flex items-center justify-between gap-2 card p-4">
            <div className="flex items-center gap-2">
              <Text as="p">{link.caregiverDisplayName}</Text>
              {!link.active && <Badge tone="warning">{t('caregivers.revoked')}</Badge>}
            </div>
            {link.active && (
              <Button
                variant="danger"
                aria-label={t('caregivers.removeLabel', { name: link.caregiverDisplayName })}
                onClick={() => revoke.mutate({ patientId, userId: link.caregiverId })}
              >
                {t('caregivers.remove')}
              </Button>
            )}
          </li>
        ))}
      </ul>
      {invite.data && (
        <div role="status" className="flex flex-col gap-1 rounded-md border-2 border-primary p-4">
          <Text>{t('caregivers.share')}</Text>
          <Text variant="display" as="p">
            {invite.data.code}
          </Text>
          <Text tone="muted" variant="caption">
            {t('caregivers.expires', {
              day: formatDay(invite.data.expiresAt, timezone),
              time: formatTime(invite.data.expiresAt, timezone),
            })}
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
          {t('caregivers.invite')}
        </Button>
      </div>
    </section>
  );
}
