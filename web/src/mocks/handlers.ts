import { http, HttpResponse, type RequestHandler } from 'msw';

import type { NotificationPreferences, Report } from '../api/generated';

import {
  getCreateReportMockHandler,
  getCreateTelegramLinkMockHandler,
  getGetReportMockHandler,
  getListReportsMockHandler,
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

// Reportes: uno pedido pasa de pending a ready en la tercera consulta.
const reports: Report[] = [];
const reportPolls = new Map<string, number>();
const readySummary = {
  taken: 18,
  skipped: 1,
  missed: 2,
  percentage: 85.7,
  byMedication: [
    {
      medicationId: '00000000-0000-4000-8000-000000000001',
      medicationName: 'Losartán',
      taken: 10,
      skipped: 0,
      missed: 1,
      percentage: 90.9,
    },
    {
      medicationId: '00000000-0000-4000-8000-000000000002',
      medicationName: 'Metformina',
      taken: 8,
      skipped: 1,
      missed: 1,
      percentage: 80,
    },
  ],
};

export const handlers: RequestHandler[] = [
  getListReportsMockHandler(() => ({ items: reports, nextCursor: null })),
  getCreateReportMockHandler(({ params }) => {
    const id = crypto.randomUUID();
    const today = new Date().toISOString().slice(0, 10);
    const report: Report = {
      id,
      patientId: String(params.patientId),
      periodStart: today,
      periodEnd: today,
      trigger: 'manual',
      status: 'pending',
      createdAt: new Date().toISOString(),
      generatedAt: null,
      summary: null,
    };
    reports.unshift(report);
    reportPolls.set(id, 0);
    return report;
  }),
  getGetReportMockHandler(({ params }) => {
    const report = reports.find((r) => r.id === params.reportId);
    if (!report) return { ...(reports[0] as Report) };
    const polls = (reportPolls.get(report.id) ?? 0) + 1;
    reportPolls.set(report.id, polls);
    if (polls >= 3 && report.status === 'pending') {
      Object.assign(report, {
        status: 'ready',
        generatedAt: new Date().toISOString(),
        summary: readySummary,
      });
    }
    return report;
  }),
  http.get('*/patients/:patientId/reports/:reportId/pdf', () =>
    HttpResponse.arrayBuffer(new TextEncoder().encode('%PDF-1.4 mock').buffer, {
      headers: { 'Content-Type': 'application/pdf' },
    }),
  ),
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
