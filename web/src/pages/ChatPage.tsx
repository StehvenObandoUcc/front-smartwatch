import { useState } from 'react';
import { useQueryClient } from '@tanstack/react-query';

import { useListPatientConsents, useSetPatientConsent } from '../api/generated';
import { Button } from '../components/atoms/Button/Button';
import { Input } from '../components/atoms/Input/Input';
import { Spinner } from '../components/atoms/Spinner/Spinner';
import { Text } from '../components/atoms/Text/Text';
import { useChat } from '../features/chat/useChat';
import { usePatient } from '../features/patients/PatientGate';
import { cn } from '../lib/cn';
import { errorMessage } from '../lib/errors';

const CONSENT_VERSION = '1';

function AiConsent({ patientId }: { patientId: string }) {
  const queryClient = useQueryClient();
  const setConsent = useSetPatientConsent({
    mutation: { onSuccess: () => queryClient.invalidateQueries() },
  });
  return (
    <section className="flex flex-col gap-4" aria-labelledby="ai-consent-title">
      <Text id="ai-consent-title" variant="title" as="h2">
        Asistente con IA
      </Text>
      <Text>
        El asistente responde preguntas sobre el plan de medicamentos. Para hacerlo envía a un
        proveedor de IA tu plan (medicamentos, dosis y horarios) sin tu nombre, documento ni datos
        de contacto. No guardamos el texto de la conversación. No cambia dosis ni diagnostica.
      </Text>
      {setConsent.error && <Text tone="danger">{errorMessage(setConsent.error)}</Text>}
      <div>
        <Button
          loading={setConsent.isPending}
          onClick={() =>
            setConsent.mutate({
              patientId,
              purpose: 'ai_chat',
              data: { granted: true, version: CONSENT_VERSION },
            })
          }
        >
          Acepto usar el asistente
        </Button>
      </div>
    </section>
  );
}

function Conversation({ patientId }: { patientId: string }) {
  const { messages, status, error, remaining, send, stop } = useChat(patientId);
  const [text, setText] = useState('');
  const streaming = status === 'streaming';

  return (
    <section className="flex flex-col gap-4" aria-labelledby="chat-title">
      <Text id="chat-title" variant="subtitle" as="h2">
        Pregunta por tu plan
      </Text>
      {messages.length === 0 && (
        <Text tone="muted">
          Por ejemplo: «¿Qué me toca esta noche?» o «¿Cuándo termina la metformina?»
        </Text>
      )}
      <ul className="flex flex-col gap-2" aria-live="polite" aria-label="Conversación">
        {messages.map((message, index) => (
          <li
            key={index}
            className={cn(
              'max-w-prose p-3 whitespace-pre-wrap',
              message.role === 'user' ? 'card self-end border-2 border-primary' : 'well self-start',
            )}
          >
            <span className="sr-only">{message.role === 'user' ? 'Tú: ' : 'Asistente: '}</span>
            {message.content || (streaming && <Spinner size="sm" label="Escribiendo" />)}
          </li>
        ))}
      </ul>
      {error && (
        <Text tone="danger" as="p">
          <span role="alert">{error}</span>
        </Text>
      )}
      <form
        className="flex gap-2"
        onSubmit={(event) => {
          event.preventDefault();
          void send(text);
          setText('');
        }}
      >
        <Input
          aria-label="Tu pregunta"
          value={text}
          maxLength={500}
          onChange={(e) => setText(e.target.value)}
          placeholder="Escribe tu pregunta"
        />
        {streaming ? (
          <Button variant="secondary" onClick={stop}>
            Detener
          </Button>
        ) : (
          <Button type="submit" disabled={!text.trim()}>
            Enviar
          </Button>
        )}
      </form>
      {remaining !== undefined && (
        <Text tone="muted" variant="caption">
          Te quedan {remaining} mensajes hoy.
        </Text>
      )}
    </section>
  );
}

export function ChatPage() {
  const { patientId } = usePatient();
  const consents = useListPatientConsents(patientId);
  if (consents.isPending) return <Spinner label="Cargando" />;
  const granted = consents.data?.items.some((c) => c.purpose === 'ai_chat' && c.granted);
  return granted ? <Conversation patientId={patientId} /> : <AiConsent patientId={patientId} />;
}
