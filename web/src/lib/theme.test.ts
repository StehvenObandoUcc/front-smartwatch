import { afterEach, describe, expect, it, vi } from 'vitest';

import { readChoice, resolveTheme, THEME_KEY } from './theme';

afterEach(() => {
  localStorage.clear();
  vi.restoreAllMocks();
});

describe('tema', () => {
  it('sin elección guardada sigue al sistema', () => {
    expect(readChoice()).toBe('system');
    expect(resolveTheme('system', 'dark')).toBe('dark');
  });

  it('la elección guardada manda sobre el sistema', () => {
    localStorage.setItem(THEME_KEY, 'light');
    expect(resolveTheme(readChoice(), 'dark')).toBe('light');
  });

  it('ignora valores desconocidos y un almacenamiento que falla', () => {
    localStorage.setItem(THEME_KEY, 'azul');
    expect(readChoice()).toBe('system');
    vi.spyOn(Storage.prototype, 'getItem').mockImplementation(() => {
      throw new Error('bloqueado');
    });
    expect(readChoice()).toBe('system');
  });
});
