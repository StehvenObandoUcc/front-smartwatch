import { ApiError } from './http';

// Códigos del backend que merecen un texto propio para la persona usuaria.
const friendly: Record<string, string> = {
  telegram_not_configured: 'Telegram aún no está disponible en este servidor.',
  chat_unavailable: 'El asistente no está disponible ahora. Inténtalo más tarde.',
  invalid_password: 'La contraseña no es correcta.',
  // Errores HTTP genéricos: el backend manda la frase en inglés («Not Found»); nunca se muestra.
  not_found: 'No encontramos lo que buscas. Puede que ya no exista.',
  method_not_allowed: 'Esta acción no está disponible.',
  forbidden: 'No tienes permiso para hacer esto.',
  conflict: 'Alguien cambió esto a la vez. Recarga e inténtalo de nuevo.',
  content_too_large: 'Lo que enviaste es demasiado grande.',
  unsupported_media_type: 'No pudimos leer lo que enviaste.',
  bad_gateway: 'El servidor no responde ahora. Inténtalo en un momento.',
  service_unavailable: 'El servidor no responde ahora. Inténtalo en un momento.',
  gateway_timeout: 'El servidor tardó demasiado. Inténtalo en un momento.',
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
