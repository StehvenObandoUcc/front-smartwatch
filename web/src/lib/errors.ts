import { ApiError } from './http';

// Códigos del backend que merecen un texto propio para la persona usuaria.
const friendly: Record<string, string> = {
  telegram_not_configured: 'Telegram aún no está disponible en este servidor.',
  chat_unavailable: 'El asistente no está disponible ahora. Inténtalo más tarde.',
  invalid_password: 'La contraseña no es correcta.',
};

function rateLimitedMessage(retryAfter: number | undefined): string {
  if (retryAfter === undefined) {
    return 'Demasiados intentos. Espera un momento y vuelve a intentarlo.';
  }
  const minutes = Math.max(1, Math.ceil(retryAfter / 60));
  return `Demasiados intentos. Vuelve a intentarlo en ${minutes} ${minutes === 1 ? 'minuto' : 'minutos'}.`;
}

export function errorMessage(error: unknown): string {
  if (error instanceof ApiError) {
    if (error.code === 'rate_limited') return rateLimitedMessage(error.retryAfter);
    return (error.code && friendly[error.code]) || error.message;
  }
  return 'No pudimos conectar con el servidor. Inténtalo de nuevo.';
}
