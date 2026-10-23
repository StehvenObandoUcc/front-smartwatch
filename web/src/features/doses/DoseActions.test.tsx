import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { act, fireEvent, render, screen } from '@testing-library/react';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';

import { DoseActions, UNDO_MS } from './DoseActions';

function show() {
  const client = new QueryClient({ defaultOptions: { mutations: { retry: false } } });
  return render(
    <QueryClientProvider client={client}>
      <DoseActions
        patientId="p1"
        scheduleId="s1"
        scheduledAt="2026-10-22T13:00:00Z"
        label="Losartán"
      />
    </QueryClientProvider>,
  );
}

describe('DoseActions', () => {
  const fetchMock = vi.fn<(input: RequestInfo | URL, init?: RequestInit) => Promise<Response>>(
    async () =>
      new Response(
        JSON.stringify({ results: [{ eventId: 'e1', outcome: 'created', code: null }] }),
        {
          status: 200,
          headers: { 'Content-Type': 'application/json' },
        },
      ),
  );

  beforeEach(() => {
    vi.useFakeTimers();
    fetchMock.mockClear();
    vi.stubGlobal('fetch', fetchMock);
  });

  afterEach(() => {
    vi.useRealTimers();
    vi.unstubAllGlobals();
  });

  it('«Deshacer» dentro del plazo no envía nada', () => {
    show();
    fireEvent.click(screen.getByRole('button', { name: 'Marcar Losartán como tomada' }));

    fireEvent.click(screen.getByRole('button', { name: 'Deshacer' }));
    act(() => vi.advanceTimersByTime(UNDO_MS + 1_000));

    expect(fetchMock).not.toHaveBeenCalled();
    expect(screen.getByRole('button', { name: 'Marcar Losartán como tomada' })).toBeInTheDocument();
  });

  it('sin deshacer, la toma se envía una vez al acabar el plazo', async () => {
    show();
    fireEvent.click(screen.getByRole('button', { name: 'Marcar Losartán como tomada' }));
    expect(fetchMock).not.toHaveBeenCalled();

    await act(() => vi.advanceTimersByTimeAsync(UNDO_MS + 100));

    expect(fetchMock).toHaveBeenCalledTimes(1);
    expect(fetchMock.mock.calls[0]?.[1]?.keepalive).toBe(true);
  });

  it('si se sale de la pantalla con la toma en espera, se envía igual', async () => {
    const view = show();
    fireEvent.click(screen.getByRole('button', { name: 'Marcar Losartán como tomada' }));

    view.unmount();
    await act(() => vi.advanceTimersByTimeAsync(100));

    expect(fetchMock).toHaveBeenCalledTimes(1);
  });
});
