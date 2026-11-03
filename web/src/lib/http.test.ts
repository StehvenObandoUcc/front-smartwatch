import { describe, expect, it } from 'vitest';

import { ApiError, toApiError } from './http';

const problem = (headers: Record<string, string> = {}) =>
  new Response(JSON.stringify({ code: 'rate_limited', detail: 'Demasiados intentos' }), {
    status: 429,
    headers: { 'Content-Type': 'application/problem+json', ...headers },
  });

describe('toApiError', () => {
  it('toma status, code y mensaje del problema', async () => {
    const error = await toApiError(problem());
    expect(error).toBeInstanceOf(ApiError);
    expect(error.status).toBe(429);
    expect(error.code).toBe('rate_limited');
    expect(error.message).toBe('Demasiados intentos');
  });

  it('lee Retry-After en segundos', async () => {
    expect((await toApiError(problem({ 'Retry-After': '120' }))).retryAfter).toBe(120);
  });

  it('ignora Retry-After ausente o no válido', async () => {
    expect((await toApiError(problem())).retryAfter).toBeUndefined();
    expect((await toApiError(problem({ 'Retry-After': 'pronto' }))).retryAfter).toBeUndefined();
    expect((await toApiError(problem({ 'Retry-After': '1.5' }))).retryAfter).toBeUndefined();
    expect((await toApiError(problem({ 'Retry-After': '-3' }))).retryAfter).toBeUndefined();
    expect(
      (await toApiError(problem({ 'Retry-After': 'Wed, 21 Oct 2026 07:28:00 GMT' }))).retryAfter,
    ).toBeUndefined();
  });
});
