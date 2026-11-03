import type { Page, Route } from '@playwright/test';

const user = {
  id: 'u1',
  email: 'ana@example.com',
  displayName: 'Ana',
  role: 'patient',
  timezone: 'America/Bogota',
  locale: 'es',
  patientId: 'p1',
  emailVerified: true,
  createdAt: '2026-10-01T00:00:00Z',
};
const patient = {
  id: 'p1',
  displayName: 'Ana',
  timezone: 'America/Bogota',
  managed: false,
  createdAt: '2026-10-01T00:00:00Z',
};
const consent = {
  purpose: 'health_data',
  granted: true,
  version: '1',
  updatedAt: '2026-10-01T00:00:00Z',
  grantedBy: 'u1',
};

const json = (route: Route, body: unknown, status = 200) =>
  route.fulfill({ status, contentType: 'application/json', body: JSON.stringify(body) });

/**
 * Simula el backend: con `signedIn` hay sesión (refresh 200) y datos mínimos de un paciente; sin
 * ella, el refresh responde 401. `onDeleteMe` decide la respuesta de DELETE /users/me.
 */
export async function fakeApi(
  page: Page,
  { signedIn = false, deleteMeStatus = 204 }: { signedIn?: boolean; deleteMeStatus?: number } = {},
) {
  let session = signedIn;
  await page.route(/herokuapp\.com|\/auth\//, async (route) => {
    const { pathname } = new URL(route.request().url());
    const method = route.request().method();
    if (pathname === '/auth/refresh') {
      return session
        ? json(route, { accessToken: 't', tokenType: 'Bearer', expiresIn: 900 })
        : json(route, { code: 'invalid_token' }, 401);
    }
    if (pathname === '/auth/login') {
      session = true;
      return json(route, {
        user,
        tokens: { accessToken: 't', tokenType: 'Bearer', expiresIn: 900 },
      });
    }
    if (pathname === '/users/me' && method === 'DELETE') {
      if (deleteMeStatus === 204) {
        session = false;
        return route.fulfill({ status: 204 });
      }
      return json(route, { code: 'invalid_password', detail: 'Contraseña incorrecta' }, 403);
    }
    if (pathname.endsWith('/me')) return json(route, user);
    if (pathname === '/patients/p1') return json(route, patient);
    if (pathname === '/patients/p1/consents') return json(route, { items: [consent] });
    if (pathname === '/patients') return json(route, { items: [patient], nextCursor: null });
    return json(route, { items: [], nextCursor: null });
  });
}
