import { useQueryClient } from '@tanstack/react-query';

import { useDeletePatientDoseEvent } from '../../api/generated';
import { Button } from '../../components/atoms/Button/Button';
import { Text } from '../../components/atoms/Text/Text';
import { errorMessage } from '../../lib/errors';

type Props = {
  patientId: string;
  /** Evento registrado de la dosis. */
  eventId: string;
  /** Nombre del medicamento, para que el botón se distinga con lector de pantalla. */
  label: string;
};

/**
 * «Deshacer» una toma ya registrada: la dosis vuelve a pendiente y se puede marcar de nuevo (con
 * otro evento). El backend responde 204 aunque ya estuviera deshecha.
 */
export function UndoDose({ patientId, eventId, label }: Props) {
  const queryClient = useQueryClient();
  const undo = useDeletePatientDoseEvent({
    mutation: { onSuccess: () => queryClient.invalidateQueries() },
  });
  return (
    <>
      <Button
        variant="secondary"
        icon="undo"
        loading={undo.isPending}
        aria-label={`Deshacer ${label}`}
        onClick={() => undo.mutate({ patientId, eventId })}
      >
        Deshacer
      </Button>
      {undo.error && (
        <div role="alert">
          <Text tone="danger">{errorMessage(undo.error)}</Text>
        </div>
      )}
    </>
  );
}
