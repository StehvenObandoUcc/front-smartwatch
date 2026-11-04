import { readFileSync } from 'node:fs';
import { resolve } from 'node:path';

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

describe('primitivos de medicamento', () => {
  it('index.ts y tokens.css tienen los mismos hex (el reloj usa los mismos)', () => {
    // Vitest no carga el CSS (ni con ?raw): se lee el archivo desde web/.
    const css = readFileSync(resolve('src/design/tokens/tokens.css'), 'utf8');
    const fromCss = Object.fromEntries(
      [...css.matchAll(/--p-med-([a-z]+): (#[0-9a-f]{6});/g)].map((m) => [m[1], m[2]]),
    );
    expect(fromCss).toEqual(medicationHex);
  });
});
