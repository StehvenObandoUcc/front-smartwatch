import { useCallback, useEffect, useRef, useState } from 'react';

import type { ChatRequest, ChatTurn } from '../../api/generated';
import { ApiError, httpStream } from '../../lib/http';
import { parseSse } from './sse';

const MAX_HISTORY = 10;
const MAX_TURN_CHARS = 1000;

export type ChatStatus = 'idle' | 'streaming';

function failureText(error: unknown): string {
  if (error instanceof ApiError && error.code === 'chat_limit_reached') {
    return 'Llegaste al límite de mensajes de hoy. Vuelve mañana.';
  }
  if (error instanceof ApiError) return error.message;
  return 'No pudimos conectar con el asistente. Inténtalo de nuevo.';
}

export function useChat(patientId: string) {
  const [messages, setMessages] = useState<ChatTurn[]>([]);
  const [status, setStatus] = useState<ChatStatus>('idle');
  const [error, setError] = useState<string>();
  const [remaining, setRemaining] = useState<number>();
  const abort = useRef<AbortController | null>(null);

  useEffect(() => () => abort.current?.abort(), []);

  const send = useCallback(
    async (text: string) => {
      const message = text.trim();
      if (!message || status === 'streaming') return;
      // Sin estado en el servidor: se reenvían los últimos turnos.
      const history = messages
        .slice(-MAX_HISTORY)
        .map((turn) => ({ ...turn, content: turn.content.slice(0, MAX_TURN_CHARS) }));
      const body: ChatRequest = { message, history };
      setMessages((prev) => [
        ...prev,
        { role: 'user', content: message },
        { role: 'assistant', content: '' },
      ]);
      setStatus('streaming');
      setError(undefined);
      const controller = new AbortController();
      abort.current = controller;
      try {
        const res = await httpStream({
          url: `/patients/${patientId}/chat/messages`,
          method: 'POST',
          data: body,
          headers: { Accept: 'text/event-stream' },
          signal: controller.signal,
        });
        if (!res.body) throw new Error('Respuesta sin cuerpo');
        for await (const event of parseSse(res.body)) {
          const payload = JSON.parse(event.data) as {
            text?: string;
            remainingMessages?: number;
            code?: string;
          };
          if (event.event === 'delta' && payload.text) {
            const chunk = payload.text;
            setMessages((prev) => {
              const last = prev[prev.length - 1];
              return last
                ? [...prev.slice(0, -1), { ...last, content: last.content + chunk }]
                : prev;
            });
          } else if (event.event === 'done') {
            setRemaining(payload.remainingMessages);
          } else if (event.event === 'error') {
            setError('El asistente no está disponible ahora. Inténtalo de nuevo.');
          }
        }
      } catch (caught) {
        if (!controller.signal.aborted) setError(failureText(caught));
      } finally {
        // Un turno vacío del asistente (falló antes de responder) no se conserva.
        setMessages((prev) => (prev[prev.length - 1]?.content === '' ? prev.slice(0, -1) : prev));
        setStatus('idle');
      }
    },
    [messages, patientId, status],
  );

  const stop = useCallback(() => abort.current?.abort(), []);

  return { messages, status, error, remaining, send, stop };
}
