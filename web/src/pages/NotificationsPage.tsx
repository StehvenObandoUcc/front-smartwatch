import { useEffect, useState } from 'react';
import { useQueryClient } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';

import {
  getListMyNotificationChannelsQueryKey,
  useCreateTelegramLink,
  useGetMyNotificationPreferences,
  useListMyConsents,
  useListMyNotificationChannels,
  useSetMyConsent,
  useSetMyNotificationPreferences,
  useUnlinkTelegram,
  type NotificationPreferences,
} from '../api/generated';
import { Badge } from '../components/atoms/Badge/Badge';
import { Button } from '../components/atoms/Button/Button';
import { Spinner } from '../components/atoms/Spinner/Spinner';
import { Text } from '../components/atoms/Text/Text';
import { ErrorRetry } from '../components/molecules/ErrorRetry';
import { errorMessage } from '../lib/errors';

const POLL_MS = 3000;
// Si el backend no manda una fecha válida, el enlace se da por caducado a los 15 min (su vida real).
const LINK_TTL_MS = 15 * 60 * 1000;
// setTimeout dispara de inmediato con retardos mayores a 2^31-1 ms.
const MAX_TIMEOUT_MS = 2 ** 31 - 1;
const CONSENT_VERSION = '1';

/** `/start <token>` sacado del enlace: respaldo para Telegram Web, que a veces abre el chat sin enviarlo. */
function startCommand(url: string): string | undefined {
  const token = /[?&]start=([^&#]+)/.exec(url)?.[1];
  return token ? `/start ${decodeURIComponent(token)}` : undefined;
}

function TelegramCard() {
  const queryClient = useQueryClient();
  const [waiting, setWaiting] = useState(false);
  const [expiresAt, setExpiresAt] = useState<number>();
  const channels = useListMyNotificationChannels({
    query: {
      refetchInterval: (query) =>
        waiting && !query.state.data?.items.some((c) => c.channel === 'telegram' && c.linked)
          ? POLL_MS
          : false,
    },
  });
  const refresh = () =>
    queryClient.invalidateQueries({ queryKey: getListMyNotificationChannelsQueryKey() });
  const link = useCreateTelegramLink({
    mutation: {
      onSuccess: (data) => {
        const expiry = Date.parse(data.expiresAt);
        setExpiresAt(Number.isNaN(expiry) ? Date.now() + LINK_TTL_MS : expiry);
        setWaiting(true);
      },
    },
  });
  // El enlace caduca: se deja de consultar y se ofrece pedir otro. El temporizador se limpia
  // al desmontar o al generar otro enlace, para que uno viejo no cierre la espera del nuevo.
  useEffect(() => {
    if (!waiting || expiresAt === undefined) return;
    const id = setTimeout(
      () => setWaiting(false),
      Math.min(MAX_TIMEOUT_MS, Math.max(0, expiresAt - Date.now())),
    );
    return () => clearTimeout(id);
  }, [waiting, expiresAt]);
  const unlink = useUnlinkTelegram({ mutation: { onSuccess: refresh } });
  const command = link.data ? startCommand(link.data.url) : undefined;
  const [copied, setCopied] = useState(false);
  async function copy(text: string) {
    try {
      await navigator.clipboard.writeText(text);
      setCopied(true);
    } catch {
      // Sin permiso de portapapeles: el mensaje sigue visible para copiarlo a mano.
    }
  }

  const telegram = channels.data?.items.find((c) => c.channel === 'telegram');
  const email = channels.data?.items.find((c) => c.channel === 'email');
  const linked = telegram?.linked === true;

  return (
    <section aria-labelledby="channels-title" className="flex flex-col gap-3">
      <Text id="channels-title" variant="subtitle" as="h2">
        Canales
      </Text>
      {channels.isPending && <Spinner label="Cargando canales" />}
      {channels.isError && (
        <div role="alert" className="flex flex-col items-start gap-2">
          <Text tone="danger">{errorMessage(channels.error)}</Text>
          <Button variant="secondary" onClick={() => void channels.refetch()}>
            Reintentar
          </Button>
        </div>
      )}
      {email && (
        <div className="flex items-center justify-between gap-2 rounded-md border-2 border-border bg-surface-raised p-4">
          <div>
            <Text as="p">Correo</Text>
            <Text tone="muted" variant="caption">
              {email.label}
            </Text>
          </div>
          <Badge tone={email.verified ? 'success' : 'warning'}>
            {email.verified ? 'Verificado' : 'Sin verificar'}
          </Badge>
        </div>
      )}
      {telegram && (
        <div className="flex flex-col gap-3 rounded-md border-2 border-border bg-surface-raised p-4">
          <div className="flex items-center justify-between gap-2">
            <Text as="p">Telegram</Text>
            <Badge tone={linked ? 'success' : 'neutral'}>
              {linked ? 'Conectado' : 'No conectado'}
            </Badge>
          </div>
          {linked ? (
            <div>
              <Button
                variant="secondary"
                loading={unlink.isPending}
                onClick={() => unlink.mutate()}
              >
                Desconectar Telegram
              </Button>
            </div>
          ) : (
            <>
              {link.data && waiting ? (
                <div className="flex flex-col gap-3" role="status">
                  <Text>Esperando la conexión con Telegram…</Text>
                  <ol className="flex list-decimal flex-col gap-3 pl-6">
                    <li className="flex flex-col items-start gap-2">
                      <Text>Abre Telegram y pulsa «Iniciar» en el chat del bot.</Text>
                      <a
                        href={link.data.url}
                        target="_blank"
                        rel="noreferrer"
                        className="inline-flex min-h-touch items-center rounded-md bg-primary px-5 font-semibold text-on-primary"
                      >
                        Abrir Telegram
                      </a>
                    </li>
                    {command && (
                      <li className="flex flex-col items-start gap-2">
                        <Text>
                          Si el bot te pide la clave (pasa en Telegram para navegador), pégale este
                          mensaje:
                        </Text>
                        <code className="rounded-md border-2 border-border bg-surface px-3 py-2 break-all">
                          {command}
                        </code>
                        <Button variant="secondary" onClick={() => void copy(command)}>
                          {copied ? 'Copiado' : 'Copiar mensaje'}
                        </Button>
                      </li>
                    )}
                  </ol>
                </div>
              ) : (
                <>
                  {link.data && !waiting && (
                    <Text tone="danger">El enlace caducó. Pide uno nuevo.</Text>
                  )}
                  <div>
                    <Button loading={link.isPending} onClick={() => link.mutate()}>
                      Conectar Telegram
                    </Button>
                  </div>
                </>
              )}
              {link.error && <Text tone="danger">{errorMessage(link.error)}</Text>}
            </>
          )}
        </div>
      )}
    </section>
  );
}

const rows = [
  { key: 'missedDose', label: 'Dosis omitida' },
  { key: 'weeklyReport', label: 'Reporte semanal' },
] as const;

function PreferencesForm({ initial }: { initial: NotificationPreferences }) {
  const save = useSetMyNotificationPreferences();
  const { register, handleSubmit } = useForm<NotificationPreferences>({ defaultValues: initial });
  return (
    <form
      onSubmit={handleSubmit((data) => save.mutate({ data }))}
      className="flex flex-col gap-3"
      aria-labelledby="prefs-title"
    >
      <Text id="prefs-title" variant="subtitle" as="h2">
        Qué avisos recibir
      </Text>
      <table className="w-full text-left">
        <thead>
          <tr>
            <th scope="col" className="py-2">
              Aviso
            </th>
            <th scope="col" className="py-2">
              Correo
            </th>
            <th scope="col" className="py-2">
              Telegram
            </th>
          </tr>
        </thead>
        <tbody>
          {rows.map((row) => (
            <tr key={row.key} className="border-t-2 border-border">
              <th scope="row" className="py-3 font-normal">
                {row.label}
              </th>
              {(['email', 'telegram'] as const).map((channel) => (
                <td key={channel} className="py-3">
                  <input
                    type="checkbox"
                    className="size-6"
                    aria-label={`${row.label} por ${channel === 'email' ? 'correo' : 'Telegram'}`}
                    {...register(`${row.key}.${channel}`)}
                  />
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
      {save.error && <Text tone="danger">{errorMessage(save.error)}</Text>}
      {save.isSuccess && <Text tone="success">Preferencias guardadas.</Text>}
      <div>
        <Button type="submit" loading={save.isPending}>
          Guardar preferencias
        </Button>
      </div>
    </form>
  );
}

function ConsentToggle() {
  const queryClient = useQueryClient();
  const consents = useListMyConsents();
  const setConsent = useSetMyConsent({
    mutation: { onSuccess: () => queryClient.invalidateQueries() },
  });
  const granted = consents.data?.items.some((c) => c.purpose === 'notifications' && c.granted);
  if (consents.isPending || granted) return null;
  return (
    <div className="flex flex-col gap-2 rounded-md border-2 border-warning p-4" role="status">
      <Text>Para enviarte avisos necesitamos tu permiso.</Text>
      <div>
        <Button
          loading={setConsent.isPending}
          onClick={() =>
            setConsent.mutate({
              purpose: 'notifications',
              data: { granted: true, version: CONSENT_VERSION },
            })
          }
        >
          Permitir avisos
        </Button>
      </div>
    </div>
  );
}

export function NotificationsPage() {
  const preferences = useGetMyNotificationPreferences();
  return (
    <div className="flex flex-col gap-6">
      <ConsentToggle />
      <TelegramCard />
      {preferences.isPending && <Spinner label="Cargando preferencias" />}
      {preferences.isError && (
        <ErrorRetry
          message={errorMessage(preferences.error)}
          onRetry={() => void preferences.refetch()}
        />
      )}
      {preferences.data && <PreferencesForm initial={preferences.data} />}
    </div>
  );
}
