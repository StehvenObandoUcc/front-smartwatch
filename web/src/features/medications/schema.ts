import { z } from 'zod';

import type { MedicationCreate, ScheduleCreate } from '../../api/generated';

const time = z.string().regex(/^([01]\d|2[0-3]):[0-5]\d$/, 'Hora no válida');

export const medicationFormSchema = z
  .object({
    name: z.string().trim().min(1, 'Escribe el nombre').max(120),
    dosage: z.string().trim().min(1, 'Escribe la dosis, por ejemplo "1 tableta"').max(80),
    instructions: z.string().trim().max(500),
    color: z.string().regex(/^#[0-9a-fA-F]{6}$/, 'Color no válido'),
    times: z
      .array(z.object({ value: time }))
      .min(1, 'Añade al menos una hora')
      .max(12)
      .refine(
        (items) => new Set(items.map((i) => i.value)).size === items.length,
        'Hay horas repetidas',
      ),
    daysOfWeek: z
      .array(z.enum(['1', '2', '3', '4', '5', '6', '7']))
      .min(1, 'Elige al menos un día'),
    startDate: z.string().regex(/^\d{4}-\d{2}-\d{2}$/, 'Elige la fecha de inicio'),
    endDate: z.string(),
  })
  .refine((v) => !v.endDate || v.endDate >= v.startDate, {
    path: ['endDate'],
    message: 'El fin no puede ser anterior al inicio',
  });

export type MedicationFormValues = z.infer<typeof medicationFormSchema>;

export function toPayload(values: MedicationFormValues): {
  medication: MedicationCreate;
  schedule: ScheduleCreate;
} {
  return {
    medication: {
      name: values.name,
      dosage: values.dosage,
      instructions: values.instructions || null,
      color: values.color,
    },
    schedule: {
      times: values.times.map((t) => t.value).sort(),
      daysOfWeek: values.daysOfWeek.map(Number).sort((a, b) => a - b),
      startDate: values.startDate,
      endDate: values.endDate || null,
    },
  };
}
