import type { RequestHandler } from 'msw';

import {
  getForgotPasswordMockHandler,
  getResendVerificationMockHandler,
  getResetPasswordMockHandler,
  getVerifyEmailMockHandler,
} from '../api/generated.msw';

// Solo los endpoints que el backend aún no implementa. Cada función de generated.msw.ts
// acepta una respuesta fija o una función; se añaden aquí al construir cada pantalla.
export const handlers: RequestHandler[] = [
  getVerifyEmailMockHandler(),
  getResendVerificationMockHandler(),
  getForgotPasswordMockHandler(),
  getResetPasswordMockHandler(),
];
