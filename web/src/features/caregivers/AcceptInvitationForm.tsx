import { useTranslation } from 'react-i18next';
import { useState } from 'react';
import { useQueryClient } from '@tanstack/react-query';

import { getListPatientsQueryKey, useAcceptCaregiverInvitation } from '../../api/generated';
import { Button } from '../../components/atoms/Button/Button';
import { Input } from '../../components/atoms/Input/Input';
import { Text } from '../../components/atoms/Text/Text';
import { FormField } from '../../components/molecules/FormField';
import { errorMessage } from '../../lib/errors';
import { invitationCodeSchema } from './invitation';

/** El cuidador entra a la cuenta de un particular con el código que este le dio. */
export function AcceptInvitationForm({ onJoined }: { onJoined: (patientId: string) => void }) {
  const { t } = useTranslation();
  const queryClient = useQueryClient();
  const [text, setText] = useState('');
  const parsed = invitationCodeSchema.safeParse(text);
  const accept = useAcceptCaregiverInvitation({
    mutation: {
      onSuccess: async (link) => {
        await queryClient.invalidateQueries({ queryKey: getListPatientsQueryKey() });
        onJoined(link.patientId);
      },
    },
  });
  return (
    <form
      className="flex flex-col gap-4"
      onSubmit={(event) => {
        event.preventDefault();
        if (parsed.success) accept.mutate({ code: parsed.data });
      }}
    >
      <Text variant="title" as="h2">
        {t('caregivers.haveCode')}
      </Text>
      <Text tone="muted">{t('caregivers.haveCodeHelp')}</Text>
      <FormField
        label={t('caregivers.code')}
        error={text && !parsed.success ? t('caregivers.codeInvalid') : undefined}
      >
        {(p) => (
          <Input
            value={text}
            onChange={(e) => setText(e.target.value)}
            autoCapitalize="characters"
            autoComplete="off"
            {...p}
          />
        )}
      </FormField>
      {accept.error && <Text tone="danger">{errorMessage(accept.error)}</Text>}
      <div>
        <Button type="submit" disabled={!parsed.success} loading={accept.isPending}>
          {t('caregivers.join')}
        </Button>
      </div>
    </form>
  );
}
