import { medicationHex } from '../../design/tokens';
import { describe, expect, it } from 'vitest';

import { medicationFormSchema, toFormValues, toPayload, type MedicationFormValues } from './schema';

const valid: MedicationFormValues = {
  name: 'Losartán',
  dosage: '1 tableta',
  instructions: '',
  color: medicationHex.blue,
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

  it('acepta cualquier color hexadecimal y rechaza otros formatos', () => {
    expect(medicationFormSchema.safeParse({ ...valid, color: '#12ab9F' }).success).toBe(true);
    expect(medicationFormSchema.safeParse({ ...valid, color: 'azul' }).success).toBe(false);
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

describe('toFormValues', () => {
  it('convierte medicamento y horario al formulario y vuelve igual con toPayload', () => {
    const values = toFormValues(
      {
        id: 'm1',
        patientId: 'p1',
        name: 'Losartán',
        dosage: '1 tableta',
        instructions: null,
        color: medicationHex.blue,
        archived: false,
        createdAt: '2026-10-01T00:00:00Z',
        updatedAt: '2026-10-01T00:00:00Z',
      },
      {
        id: 's1',
        medicationId: 'm1',
        times: ['08:00', '20:00'],
        daysOfWeek: [1, 5],
        startDate: '2026-10-01',
        endDate: null,
      },
    );
    expect(medicationFormSchema.safeParse(values).success).toBe(true);
    expect(values.daysOfWeek).toEqual(['1', '5']);
    expect(toPayload(values).schedule).toEqual({
      times: ['08:00', '20:00'],
      daysOfWeek: [1, 5],
      startDate: '2026-10-01',
      endDate: null,
    });
  });
});
