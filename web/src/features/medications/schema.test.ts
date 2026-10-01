import { medicationHex } from '../../design/tokens';
import { describe, expect, it } from 'vitest';

import { medicationFormSchema, toPayload, type MedicationFormValues } from './schema';

const valid: MedicationFormValues = {
  name: 'Losartán',
  dosage: '1 tableta',
  instructions: '',
  color: 'blue',
  times: [{ value: '20:00' }, { value: '08:00' }],
  daysOfWeek: ['5', '1'],
  startDate: '2026-10-01',
  endDate: '',
};

describe('medicationFormSchema', () => {
  it('acepta un formulario válido', () => {
    expect(medicationFormSchema.safeParse(valid).success).toBe(true);
  });

  it('rechaza horas repetidas', () => {
    const result = medicationFormSchema.safeParse({
      ...valid,
      times: [{ value: '08:00' }, { value: '08:00' }],
    });
    expect(result.success).toBe(false);
  });

  it('rechaza sin días, hora mal formada o fin anterior al inicio', () => {
    expect(medicationFormSchema.safeParse({ ...valid, daysOfWeek: [] }).success).toBe(false);
    expect(medicationFormSchema.safeParse({ ...valid, times: [{ value: '25:00' }] }).success).toBe(
      false,
    );
    expect(medicationFormSchema.safeParse({ ...valid, endDate: '2026-09-01' }).success).toBe(false);
  });
});

describe('toPayload', () => {
  it('ordena horas y días, usa el hex del color y null para campos vacíos', () => {
    const { medication, schedule } = toPayload(valid);
    expect(medication).toEqual({
      name: 'Losartán',
      dosage: '1 tableta',
      instructions: null,
      color: medicationHex.blue,
    });
    expect(schedule).toEqual({
      times: ['08:00', '20:00'],
      daysOfWeek: [1, 5],
      startDate: '2026-10-01',
      endDate: null,
    });
  });
});
