import { ApiError } from './http';

export function errorMessage(error: unknown): string {
  if (error instanceof ApiError) return error.message;
  return 'No pudimos conectar con el servidor. Inténtalo de nuevo.';
}
