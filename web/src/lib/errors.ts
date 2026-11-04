import { i18n } from '../i18n';
import { ApiError } from './http';

// Códigos del backend que merecen un texto propio para la persona usuaria. Los errores HTTP
// genéricos traen la frase en inglés del servidor («Not Found»): nunca se muestra.
const friendly = [
  'telegram_not_configured',
  'chat_unavailable',
  'invalid_password',
  'not_found',
  'method_not_allowed',
  'forbidden',
  'conflict',
  'content_too_large',
  'unsupported_media_type',
  'bad_gateway',
  'service_unavailable',
  'gateway_timeout',
] as const;

type FriendlyCode = (typeof friendly)[number];

function isFriendly(code: string | undefined): code is FriendlyCode {
  return friendly.includes(code as FriendlyCode);
}

function rateLimitedMessage(retryAfter: number | undefined): string {
  if (retryAfter === undefined) return i18n.t('errors.rateLimited');
  const count = Math.max(1, Math.ceil(retryAfter / 60));
  return i18n.t('errors.rateLimitedIn', { count });
}

/** Texto del error en el idioma activo. El `detail` de otros códigos llega del backend tal cual. */
export function errorMessage(error: unknown): string {
  if (error instanceof ApiError) {
    if (error.code === 'rate_limited') return rateLimitedMessage(error.retryAfter);
    return isFriendly(error.code) ? i18n.t(`errors.${error.code}`) : error.message;
  }
  return i18n.t('errors.network');
}
