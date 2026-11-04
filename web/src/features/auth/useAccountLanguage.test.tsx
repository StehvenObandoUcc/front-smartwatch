import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { act, renderHook, waitFor } from '@testing-library/react';
import type { ReactNode } from 'react';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';

import type { User } from '../../api/generated';
import { i18n, LANGUAGE_KEY, setLanguage } from '../../i18n';
import { useAccountLanguage } from './useAccountLanguage';

const mocks = vi.hoisted(() => ({ http: vi.fn() }));

// Se sustituye solo el transporte: el hook generado useUpdateMe es el real.
vi.mock('../../lib/http', async (importOriginal) => ({
  ...(await importOriginal<typeof import('../../lib/http')>()),
  http: mocks.http,
}));

const user = (locale: 'es' | 'en'): User =>
  ({ id: 'u1', email: 'ana@example.com', role: 'patient', locale }) as User;

function wrapper({ children }: { children: ReactNode }) {
  const client = new QueryClient({ defaultOptions: { mutations: { retry: false } } });
  return <QueryClientProvider client={client}>{children}</QueryClientProvider>;
}

const patches = () =>
  mocks.http.mock.calls
    .filter(([config]) => config.method === 'PATCH')
    .map(([config]) => config.data);

beforeEach(() => {
  mocks.http.mockReset();
  mocks.http.mockResolvedValue({});
  localStorage.clear();
  void i18n.changeLanguage('es');
});

afterEach(() => localStorage.clear());

describe('useAccountLanguage', () => {
  it('sin idioma elegido en el dispositivo, adopta el de la cuenta y no la cambia', async () => {
    renderHook(() => useAccountLanguage(user('en')), { wrapper });

    await waitFor(() => expect(i18n.language).toBe('en'));
    expect(patches()).toEqual([]);
  });

  it('con otro idioma elegido aquí, lo guarda en la cuenta una sola vez', async () => {
    localStorage.setItem(LANGUAGE_KEY, 'en');
    void i18n.changeLanguage('en');

    const { rerender } = renderHook(() => useAccountLanguage(user('es')), { wrapper });
    rerender();

    await waitFor(() => expect(patches()).toEqual([{ locale: 'en' }]));
  });

  it('al cambiar de idioma y volver, guarda los dos cambios aunque la sesión siga con el viejo', async () => {
    localStorage.setItem(LANGUAGE_KEY, 'es');
    renderHook(() => useAccountLanguage(user('es')), { wrapper });

    act(() => setLanguage('en'));
    await waitFor(() => expect(patches()).toEqual([{ locale: 'en' }]));

    act(() => setLanguage('es'));
    await waitFor(() => expect(patches()).toEqual([{ locale: 'en' }, { locale: 'es' }]));
  });

  it('sin sesión no hace nada', () => {
    renderHook(() => useAccountLanguage(null), { wrapper });
    expect(mocks.http).not.toHaveBeenCalled();
  });
});
