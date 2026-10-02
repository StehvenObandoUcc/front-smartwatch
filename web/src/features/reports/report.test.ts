import { describe, expect, it } from 'vitest';

import { percentText } from './ReportView';

describe('percentText', () => {
  it('redondea y muestra "Sin datos" cuando no hubo dosis', () => {
    expect(percentText(86.6)).toBe('87 %');
    expect(percentText(null)).toBe('Sin datos');
  });
});
