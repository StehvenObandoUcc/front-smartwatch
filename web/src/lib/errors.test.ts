import { describe, expect, it } from 'vitest';

import { errorMessage } from './errors';
import { ApiError } from './http';

describe('errorMessage', () => {
  it('traduce invalid_password', () => {
    expect(errorMessage(new ApiError(403, 'invalid_password', 'Forbidden'))).toBe(
      'La contraseña no es correcta.',
    );
  });

  it('usa el mensaje del servidor si el código no tiene texto propio', () => {
    expect(errorMessage(new ApiError(500, 'otro', 'Algo falló'))).toBe('Algo falló');
  });

  it('indica el tiempo de espera de rate_limited, redondeando hacia arriba a minutos', () => {
    const limited = (seconds?: number) => new ApiError(429, 'rate_limited', 'x', seconds);
    expect(errorMessage(limited(30))).toBe('Demasiados intentos. Vuelve a intentarlo en 1 minuto.');
    expect(errorMessage(limited(60))).toBe('Demasiados intentos. Vuelve a intentarlo en 1 minuto.');
    expect(errorMessage(limited(61))).toBe(
      'Demasiados intentos. Vuelve a intentarlo en 2 minutos.',
    );
    expect(errorMessage(limited(900))).toBe(
      'Demasiados intentos. Vuelve a intentarlo en 15 minutos.',
    );
  });

  it('rate_limited sin Retry-After usa el texto genérico', () => {
    expect(errorMessage(new ApiError(429, 'rate_limited', 'x'))).toBe(
      'Demasiados intentos. Espera un momento y vuelve a intentarlo.',
    );
  });

  it('un error que no es de la API se trata como fallo de conexión', () => {
    expect(errorMessage(new Error('boom'))).toBe(
      'No pudimos conectar con el servidor. Inténtalo de nuevo.',
    );
  });

  it('los errores HTTP genéricos salen en español, no con la frase en inglés del servidor', () => {
    expect(errorMessage(new ApiError(404, 'not_found', 'Not Found'))).toBe(
      'No encontramos lo que buscas. Puede que ya no exista.',
    );
    expect(errorMessage(new ApiError(405, 'method_not_allowed', 'Method Not Allowed'))).toBe(
      'Esta acción no está disponible.',
    );
  });
});
