import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { act, fireEvent, render, screen } from '@testing-library/react';
import type { ReactNode } from 'react';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';

import { DoseActions } from './DoseActions';
import { UndoDose } from './UndoDose';

function show(ui: ReactNode) {
  const client = new QueryClient({ defaultOptions: { mutations: { retry: false } } });
  return render(<QueryClientProvider client={client}>{ui}</QueryClientProvider>);
}

const fetchMock = vi.fn<(input: RequestInfo | URL, init?: RequestInit) => Promise<Response>>();

beforeEach(() => {
  fetchMock.mockReset();
  vi.stubGlobal('fetch', fetchMock);
});

afterEach(() => vi.unstubAllGlobals());

describe('DoseActions', () => {
  it('«Tomada» se envía al momento, una sola vez', async () => {
    fetchMock.mockResolvedValue(
      new Response(
        JSON.stringify({ results: [{ eventId: 'e1', outcome: 'created', code: null }] }),
        {
          status: 200,
          headers: { 'Content-Type': 'application/json' },
        },
      ),
    );
    show(
      <DoseActions
        patientId="p1"
        scheduleId="s1"
        scheduledAt="2026-10-22T13:00:00Z"
        label="Losartán"
      />,
    );

    await act(async () => {
      fireEvent.click(screen.getByRole('button', { name: 'Marcar Losartán como tomada' }));
    });

    expect(fetchMock).toHaveBeenCalledTimes(1);
    const body = JSON.parse(String(fetchMock.mock.calls[0]?.[1]?.body));
    expect(body.events[0]).toMatchObject({ scheduleId: 's1', status: 'TAKEN' });
  });
});

describe('UndoDose', () => {
  it('«Deshacer» borra el evento de la dosis', async () => {
    fetchMock.mockResolvedValue(new Response(null, { status: 204 }));
    show(<UndoDose patientId="p1" eventId="e1" label="Losartán" />);

    await act(async () => {
      fireEvent.click(screen.getByRole('button', { name: 'Deshacer Losartán' }));
    });

    expect(fetchMock).toHaveBeenCalledTimes(1);
    const [url, init] = fetchMock.mock.calls[0] ?? [];
    expect(String(url)).toMatch(/\/patients\/p1\/dose-events\/e1$/);
    expect(init?.method).toBe('DELETE');
  });
});
