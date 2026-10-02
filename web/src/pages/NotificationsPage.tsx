import { useState } from 'react';
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
import { errorMessage } from '../lib/errors';

const POLL_MS = 3000;
const CONSENT_VERSION = '1';

function TelegramCard() {
  const queryClient = useQueryClient();
  const [waiting, setWaiting] = useState(false);
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
        setWaiting(true);
        // El enlace caduca: se deja de consultar y se ofrece pedir otro.
        setTimeout(
          () => setWaiting(false),
          Math.max(0, new Date(data.expiresAt).getTime() - Date.now()),
        );
      },
    },
  });
  const unlink = useUnlinkTelegram({ mutation: { onSuccess: refresh } });

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
                <div className="flex flex-col gap-2" role="status">
                  <Text>
                    Abre el enlace, pulsa «Iniciar» en Telegram y vuelve aquí. Esperando la
                    conexión…
                  </Text>
                  <a
                    href={link.data.url}
                    target="_blank"
                    rel="noreferrer"
                    className="inline-flex min-h-touch items-center self-start rounded-md bg-primary px-5 font-semibold text-on-primary"
                  >
                    Abrir Telegram
                  </a>
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
      {preferences.isError && <Text tone="danger">{errorMessage(preferences.error)}</Text>}
      {preferences.data && <PreferencesForm initial={preferences.data} />}
    </div>
  );
}
