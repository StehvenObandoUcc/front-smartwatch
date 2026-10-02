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
        Tengo un código de invitación
      </Text>
      <Text tone="muted">Si una persona con cuenta te invitó, escribe aquí su código.</Text>
      <FormField
        label="Código de invitación"
        error={text && !parsed.success ? 'El código tiene 10 caracteres' : undefined}
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
          Unirme
        </Button>
      </div>
    </form>
  );
}
