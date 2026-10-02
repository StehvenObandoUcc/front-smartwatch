import type { RequestHandler } from 'msw';

import type { NotificationPreferences } from '../api/generated';

import {
  getCreateTelegramLinkMockHandler,
  getForgotPasswordMockHandler,
  getGetMyNotificationPreferencesMockHandler,
  getListMyNotificationChannelsMockHandler,
  getSetMyNotificationPreferencesMockHandler,
  getUnlinkTelegramMockHandler,
  getResendVerificationMockHandler,
  getResetPasswordMockHandler,
  getVerifyEmailMockHandler,
} from '../api/generated.msw';

// Solo los endpoints que el backend aún no implementa. Cada función de generated.msw.ts
// acepta una respuesta fija o una función; se añaden aquí al construir cada pantalla.
// Telegram: se "conecta" a la tercera consulta tras pedir el enlace.
let telegramPolls = -1;
let preferences: NotificationPreferences = {
  missedDose: { email: true, telegram: true },
  weeklyReport: { email: true, telegram: true },
};

export const handlers: RequestHandler[] = [
  getListMyNotificationChannelsMockHandler(() => {
    if (telegramPolls >= 0) telegramPolls += 1;
    const linked = telegramPolls >= 3;
    return {
      items: [
        { channel: 'email', linked: true, verified: true, label: 'tu correo', linkedAt: null },
        {
          channel: 'telegram',
          linked,
          verified: linked,
          label: linked ? '@mock_user' : null,
          linkedAt: linked ? new Date().toISOString() : null,
        },
      ],
    };
  }),
  getCreateTelegramLinkMockHandler(() => {
    telegramPolls = 0;
    return {
      url: 'https://t.me/MockBot?start=mock-token',
      expiresAt: new Date(Date.now() + 15 * 60_000).toISOString(),
    };
  }),
  getUnlinkTelegramMockHandler(() => {
    telegramPolls = -1;
  }),
  getGetMyNotificationPreferencesMockHandler(() => preferences),
  getSetMyNotificationPreferencesMockHandler(async ({ request }) => {
    preferences = (await request.json()) as NotificationPreferences;
    return preferences;
  }),
  getVerifyEmailMockHandler(),
  getResendVerificationMockHandler(),
  getForgotPasswordMockHandler(),
  getResetPasswordMockHandler(),
];
