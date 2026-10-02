import type { RequestHandler } from 'msw';

// Solo los endpoints que el backend aún no implementa. Cada función de generated.msw.ts
// acepta una respuesta fija o una función; se añaden aquí al construir cada pantalla.
export const handlers: RequestHandler[] = [];
