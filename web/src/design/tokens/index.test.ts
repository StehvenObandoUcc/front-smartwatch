import { describe, expect, it } from 'vitest';

import { medicationColorFor, medicationHex } from './index';

describe('medicationColorFor', () => {
  it('devuelve el mismo color para un hex de la paleta', () => {
    expect(medicationColorFor(medicationHex.purple)).toBe('purple');
  });

  it('lleva un 500 de Tailwind a su tono profundo', () => {
    expect(medicationColorFor('#3b82f6')).toBe('blue');
    expect(medicationColorFor('#ef4444')).toBe('red');
    expect(medicationColorFor('#22c55e')).toBe('green');
    expect(medicationColorFor('#a855f7')).toBe('purple');
  });
});
