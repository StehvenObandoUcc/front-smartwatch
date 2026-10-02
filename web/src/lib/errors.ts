import { ApiError } from './http';

// Códigos del backend que merecen un texto propio para la persona usuaria.
const friendly: Record<string, string> = {
  telegram_not_configured: 'Telegram aún no está disponible en este servidor.',
  chat_unavailable: 'El asistente no está disponible ahora. Inténtalo más tarde.',
};

export function errorMessage(error: unknown): string {
  if (error instanceof ApiError) return (error.code && friendly[error.code]) || error.message;
  return 'No pudimos conectar con el servidor. Inténtalo de nuevo.';
}
