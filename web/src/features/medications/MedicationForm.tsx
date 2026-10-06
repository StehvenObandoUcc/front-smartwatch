import { zodResolver } from '@hookform/resolvers/zod';
import { useFieldArray, useForm, useWatch } from 'react-hook-form';

import { Button } from '../../components/atoms/Button/Button';
import { ColorDot } from '../../components/atoms/ColorDot/ColorDot';
import { Input } from '../../components/atoms/Input/Input';
import { Text } from '../../components/atoms/Text/Text';
import { FormField } from '../../components/molecules/FormField';
import { medicationColors, medicationHex } from '../../design/tokens';
import { cn } from '../../lib/cn';
import { localDate } from '../../lib/dates';
import { medicationFormSchema, type MedicationFormValues } from './schema';

const dayLabels = ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom'];
const colorNames: Record<(typeof medicationColors)[number], string> = {
  red: 'Rojo',
  orange: 'Naranja',
  yellow: 'Amarillo',
  green: 'Verde',
  teal: 'Turquesa',
  blue: 'Azul',
  purple: 'Morado',
  pink: 'Rosa',
};

type Props = {
  timezone: string;
  initial?: MedicationFormValues;
  submitting: boolean;
  error?: string | undefined;
  onSubmit: (values: MedicationFormValues) => void;
  onCancel: () => void;
};

export function MedicationForm({
  timezone,
  initial,
  submitting,
  error,
  onSubmit,
  onCancel,
}: Props) {
  const { register, handleSubmit, control, formState, setValue } = useForm<MedicationFormValues>({
    resolver: zodResolver(medicationFormSchema),
    defaultValues: initial ?? {
      name: '',
      dosage: '',
      instructions: '',
      color: medicationHex.blue,
      times: [{ value: '08:00' }],
      daysOfWeek: ['1', '2', '3', '4', '5', '6', '7'],
      startDate: localDate(new Date(), timezone),
      endDate: '',
    },
  });
  const times = useFieldArray({ control, name: 'times' });
  const { errors } = formState;
  const selectedColor = useWatch({ control, name: 'color' });
  const isCustomColor = !Object.values(medicationHex).includes(selectedColor.toLowerCase());

  return (
    <form noValidate onSubmit={handleSubmit(onSubmit)} className="flex max-w-xl flex-col gap-4">
      <FormField label="Medicamento" error={errors.name?.message}>
        {(p) => <Input {...p} {...register('name')} />}
      </FormField>
      <FormField label="Dosis por toma" error={errors.dosage?.message}>
        {(p) => <Input placeholder="1 tableta" {...p} {...register('dosage')} />}
      </FormField>
      <FormField label="Indicaciones (opcional)" error={errors.instructions?.message}>
        {(p) => <Input {...p} {...register('instructions')} />}
      </FormField>

      <fieldset className="flex flex-col gap-2">
        <legend className="text-body font-semibold">Color</legend>
        <div className="flex flex-wrap gap-2">
          {medicationColors.map((color) => (
            <label
              key={color}
              className="flex min-h-touch items-center gap-2 rounded-md border-2 border-border-strong px-3 has-checked:border-primary has-checked:bg-surface-raised"
            >
              <input
                type="radio"
                value={medicationHex[color]}
                className="sr-only"
                {...register('color')}
              />
              <ColorDot color={color} />
              {colorNames[color]}
            </label>
          ))}
          <label
            className={cn(
              'flex min-h-touch items-center gap-2 rounded-md border-2 px-3',
              isCustomColor ? 'border-primary bg-surface-raised' : 'border-border-strong',
            )}
          >
            <input
              type="color"
              aria-label="Elegir otro color"
              value={selectedColor}
              onChange={(event) =>
                setValue('color', event.target.value, { shouldValidate: true, shouldDirty: true })
              }
              className="size-6 cursor-pointer"
            />
            Otro color
          </label>
        </div>
        {errors.color?.message && (
          <p role="alert" className="text-caption text-danger">
            {errors.color.message}
          </p>
        )}
      </fieldset>

      <fieldset className="flex flex-col gap-2">
        <legend className="text-body font-semibold">Horas</legend>
        {times.fields.map((field, index) => (
          <div key={field.id} className="flex items-center gap-2">
            <Input
              type="time"
              aria-label={`Hora ${index + 1}`}
              invalid={Boolean(errors.times?.[index])}
              {...register(`times.${index}.value`)}
            />
            {times.fields.length > 1 && (
              <Button
                variant="ghost"
                aria-label={`Quitar hora ${index + 1}`}
                onClick={() => times.remove(index)}
              >
                Quitar
              </Button>
            )}
          </div>
        ))}
        {(errors.times?.message ?? errors.times?.root?.message) && (
          <p role="alert" className="text-caption text-danger">
            {errors.times?.message ?? errors.times?.root?.message}
          </p>
        )}
        <div>
          <Button variant="secondary" onClick={() => times.append({ value: '12:00' })}>
            Añadir hora
          </Button>
        </div>
      </fieldset>

      <fieldset className="flex flex-col gap-2">
        <legend className="text-body font-semibold">Días</legend>
        <div className="flex flex-wrap gap-2">
          {dayLabels.map((label, index) => (
            <label
              key={label}
              className="flex min-h-touch min-w-touch items-center justify-center rounded-md border-2 border-border-strong px-3 has-checked:border-primary has-checked:bg-primary has-checked:text-on-primary"
            >
              <input
                type="checkbox"
                value={index + 1}
                className="sr-only"
                {...register('daysOfWeek')}
              />
              {label}
            </label>
          ))}
        </div>
        {errors.daysOfWeek?.message && (
          <p role="alert" className="text-caption text-danger">
            {errors.daysOfWeek.message}
          </p>
        )}
      </fieldset>

      <div className="flex flex-wrap gap-4">
        <FormField label="Desde" error={errors.startDate?.message}>
          {(p) => <Input type="date" {...p} {...register('startDate')} />}
        </FormField>
        <FormField label="Hasta (opcional)" error={errors.endDate?.message}>
          {(p) => <Input type="date" {...p} {...register('endDate')} />}
        </FormField>
      </div>

      {error && (
        <Text tone="danger" as="p">
          {error}
        </Text>
      )}
      <div className="flex gap-2">
        <Button type="submit" loading={submitting}>
          Guardar
        </Button>
        <Button variant="ghost" onClick={onCancel}>
          Cancelar
        </Button>
      </div>
    </form>
  );
}
