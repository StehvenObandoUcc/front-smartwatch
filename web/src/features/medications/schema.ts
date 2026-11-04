import type { ParseKeys } from 'i18next';
import { z } from 'zod';

import type { Medication, MedicationCreate, Schedule, ScheduleCreate } from '../../api/generated';
import { i18n } from '../../i18n';

// El mensaje se traduce al validar, en el idioma activo.
const msg = (key: ParseKeys) => ({ error: () => i18n.t(key) });

const time = z.string().regex(/^([01]\d|2[0-3]):[0-5]\d$/, msg('medications.errors.timeInvalid'));

export const medicationFormSchema = z
  .object({
    name: z.string().trim().min(1, msg('medications.errors.nameRequired')).max(120),
    dosage: z.string().trim().min(1, msg('medications.errors.dosageRequired')).max(80),
    instructions: z.string().trim().max(500),
    color: z.string().regex(/^#[0-9a-fA-F]{6}$/, msg('medications.errors.colorInvalid')),
    times: z
      .array(z.object({ value: time }))
      .min(1, msg('medications.errors.timeRequired'))
      .max(12)
      .refine(
        (items) => new Set(items.map((i) => i.value)).size === items.length,
        msg('medications.errors.timesRepeated'),
      ),
    daysOfWeek: z
      .array(z.enum(['1', '2', '3', '4', '5', '6', '7']))
      .min(1, msg('medications.errors.dayRequired')),
    startDate: z.string().regex(/^\d{4}-\d{2}-\d{2}$/, msg('medications.errors.startRequired')),
    endDate: z.string(),
  })
  .refine((v) => !v.endDate || v.endDate >= v.startDate, {
    path: ['endDate'],
    ...msg('medications.errors.endBeforeStart'),
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

/** Valores iniciales del formulario al editar un medicamento y su (primer) horario. */
export function toFormValues(
  medication: Medication,
  schedule: Schedule | null,
): MedicationFormValues {
  return {
    name: medication.name,
    dosage: medication.dosage,
    instructions: medication.instructions ?? '',
    color: medication.color,
    times: (schedule?.times ?? ['08:00']).map((value) => ({ value })),
    daysOfWeek: (schedule?.daysOfWeek ?? [1, 2, 3, 4, 5, 6, 7]).map(
      String,
    ) as MedicationFormValues['daysOfWeek'],
    startDate: schedule?.startDate ?? '',
    endDate: schedule?.endDate ?? '',
  };
}
